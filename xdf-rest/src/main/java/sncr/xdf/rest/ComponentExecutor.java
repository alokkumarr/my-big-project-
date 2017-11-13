package sncr.xdf.rest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import sncr.xdf.rest.actors.TaskCoordinator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ComponentExecutor {

    private ActorSystem system = null;
    private Config config;
    private String taskId;
    String ip;

    public static void main(String[] args){


        System.out.println("Starting task executor... " + System.getProperty("java.home"));

        String fileName = args[1];
        String id = args[2];
        try {
            String config = new String(Files.readAllBytes(FileSystems.getDefault().getPath(fileName)));
            ComponentExecutor rm = new ComponentExecutor(id, config);
            rm.startExecutor();
        } catch(java.io.IOException e){
            System.err.println("Configuration file not found " + fileName);
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public ComponentExecutor(String taskId, String configString){
        this.taskId = taskId;

        // Assume one node, non-distributed configuration
        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            ip = ipAddr.getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        List<String> roles = new ArrayList<>();
        roles.add(taskId);

        Config extConfig = ConfigFactory.parseString(configString);
        this.config = extConfig
            .withFallback(ConfigFactory.load("component-executor"))
            .withFallback(ConfigFactory.load())
            .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(0))
            .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(ip))
            .withValue("akka.cluster.roles", ConfigValueFactory.fromAnyRef(roles));
    }


    public void startExecutor(){
        // Create seed nodes
        String actorSystemName = config.getString("xdf.rest.name");

        // Create actor system
        system = ActorSystem.create(actorSystemName, config);

        List<Address> seedAddresses = new ArrayList<>();
        List<Integer> seedPorts = config.getIntList("xdf.rest.seeds");
        for(Integer port : seedPorts){
            seedAddresses.add(new Address("akka.tcp", actorSystemName, ip, port));
        }

        // Connect to cluster seeds
        if(seedAddresses.size() > 0)
            Cluster.get(system).joinSeedNodes(seedAddresses);

    }
}
