package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.remote.RemoteScope;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;

abstract class MainJVMCoordinator extends AbstractActor {

    // Akka cluster reference
    protected Cluster cluster = Cluster.get(getContext().system());
    protected String newJvmCmd = null;
    protected String dataLakeRoot = null;
    // Identifier of JVM (cluster node) - must be declared in implementation class
    protected String JVM_ROLE;
    // Service Readiness flag
    protected Integer isReady = 0;
    // Array of imdependent executors to support (relatively)long tasks
    protected ActorRef[] executors = null;
    protected boolean[] executorsAvailability = null;
    // Remote address to deploy executors (can be separate local JVM or remote JVM)
    // As of now we are supporting local JVMs only
    // In general we support following relations 1 coordinator to 1 JVM
    // In some cases (e.g. Spark jobs) this model doesn't work - we will need 1 coordinator to multiple JVM model
    protected Deploy d = null;

    protected MainJVMCoordinator(int numberOfExecutors){
        executors = new ActorRef[numberOfExecutors];
        executorsAvailability = new boolean[numberOfExecutors];
        for(int i = 0; i < numberOfExecutors; i++)
            executorsAvailability[i] = false;
        JVM_ROLE = null;
    }

    protected ActorRef getExecutor(){
        for(int i = 0; i < executorsAvailability.length; i++) {
            if(executorsAvailability[i])
                return executors[i];
        }
        return null;
    }

    //subscribe to cluster changes
    @Override
    public void preStart() {
        getContext().system().log().info("Starting {}", this.getClass().getSimpleName() );
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() {
        getContext().system().log().info("Stopping {}", this.getClass().getSimpleName() );
        cluster.unsubscribe(self());
    }

    protected int initialize(String dataLakeRoot, String newJvmCmd, String jvmRole, LoggingAdapter log){
        this.dataLakeRoot = dataLakeRoot;
        this.newJvmCmd = newJvmCmd;
        this.JVM_ROLE = jvmRole;

        // Start new JVM for Data Lake operations
        if(startNewJvm(log) == 0){
            //success
        } else {
            // failure
        }
        return 0;
    }

    // Restart JVM
    protected void recover(ClusterEvent.UnreachableMember mUnreachable, LoggingAdapter log){
        // Check if member represents Data Lake Support JVM
        log.warning("Node down detected - investigating. My role is {}", JVM_ROLE);
        log.warning("Node has following roles:");

        for (String s : mUnreachable.member().getRoles()) {

            log.warning(" {}", s);
            if (JVM_ROLE != null && JVM_ROLE.equals(s)) {
                log.warning("JVM {} failed - trying to recover", JVM_ROLE);

                log.info("Downing : {}", mUnreachable.member());
                cluster.down(mUnreachable.member().address());

                isReady = 0;
                // Restart JVM if needed
                initialize(dataLakeRoot, newJvmCmd, JVM_ROLE, log);
            }
        }
    }

    private int startNewJvm(LoggingAdapter log){

        String cmd = newJvmCmd + " " + JVM_ROLE;
        log.info("Staring new JVM. Command line : {} ", cmd);
        try {
            java.lang.Runtime.getRuntime().exec(cmd);
            return 0;
        } catch(Exception e){
            log.error(e.getMessage());
            return -1;
        }
    }

    protected ReceiveBuilder processCommonEvents(Class executorType, LoggingAdapter log){
        return receiveBuilder()
            .match(ClusterEvent.MemberUp.class, mUp -> {
                // Check if new member node/JVM was requested by this instance
                // We can use role which has been set during startup of the node

                // Look for role passed to child during startup - should math our request id
                for (String s : mUp.member().getRoles()) {
                    if (JVM_ROLE != null && JVM_ROLE.equals(s)) {
                        log.info("JVM [{}] is started", JVM_ROLE);
                        // Create new data Lake operations executor in separate JVM
                        d = new Deploy(new RemoteScope(mUp.member().address()));
                        for(Integer i = 0; i < executors.length; i++) {
                            //log.info("Starting executor {}", i);
                            executors[i] = getContext().actorOf(Props.create(executorType).withDeploy(d), JVM_ROLE + i);
                            // Start task processing
                            executors[i].tell(new Init(newJvmCmd, dataLakeRoot, i), getSelf());
                        }
                    }
                    break;
                }
            })
            .match(ClusterEvent.UnreachableMember.class, mUnreachable -> {
                recover(mUnreachable, log);
            })
            .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                // Ignore
            })
            .match(CleanRequest.class, r ->{
                shutdown(r, log);
            })
            .match(Init.class, r -> {
                if(getSender().equals(getSelf())){
                    // This message came from Service class, requesting initialization
                    // Only Service will use address of this coordinator as a sender
                    // This is initialization request
                    initialize(r.dataLakeRoot, r.newJvmCmd, JVM_ROLE, log);
                } else {
                    // This message came from one of subsequent executors (nobody else knows about this coordinator)
                    // We assume what if one executor is ready whole service is aup and ready
                    // More strong approach would be to track individual executor messages and declare them as
                    // "ready" on one-by-one basis
                    //log.info("{} is ready - received confirmation from {}", JVM_ROLE, r.exeutorNo);
                    executorsAvailability[r.exeutorNo] = true;
                    isReady = 1;
                }
            });
    }

    protected void shutdown(CleanRequest r, LoggingAdapter log){
        log.info("Shutting down...");
        isReady = 0;
        for(int i = 0; i < executors.length; i++)
            executors[i].tell(r, getSelf());
        getContext().stop(getSelf());
    }
}

