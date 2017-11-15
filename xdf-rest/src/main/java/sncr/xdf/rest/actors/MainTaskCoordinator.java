package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import sncr.xdf.rest.ExecutionStatus;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.NewRequest;
import sncr.xdf.rest.messages.StatusUpdate;

import java.util.HashMap;
import java.util.Map;

// This class coordinates all request for long running job executions
public class MainTaskCoordinator extends AbstractActor {

    private Long l = 0L;

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private Cluster cluster = Cluster.get(getContext().system());
    private Map<String, ExecutionStatus> tasks;
    private String dataLakeRoot;
    private String jvmCmd;

    public MainTaskCoordinator(String dataLakeRoot, String jvmCmd){
        super();
        this.dataLakeRoot = dataLakeRoot;
        this.jvmCmd = jvmCmd;
    }

    public static Props props(String dataLakeRoot, String jvmCmd) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(MainTaskCoordinator.class,
                            () -> new MainTaskCoordinator(dataLakeRoot, jvmCmd));
    }

    //subscribe to cluster changes
    @Override
    public void preStart() {
        log.info("Starting up (data lake root = {})", dataLakeRoot);
        tasks = new HashMap<>();
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(),
                          ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            // Application events
            .match(NewRequest.class, r -> {
                newRequest(r);
            })
            .match(StatusUpdate.class, r-> {
                status(getSender(), r);
            })
            .match(CleanRequest.class, r -> {
                log.info("Received cleanup request from client");
                cleanup();
            })
            .build();
    }

    private void updateStatus(String id, String status){
        ExecutionStatus s = tasks.get(id);
        if(s != null){
            s.status = status;
            tasks.put(id, s);
        }
    }

    private void status(ActorRef sender, StatusUpdate r){
        switch(r.status){
            case StatusUpdate.REQUEST: {
                log.info("Received status request from client");
                String status = getStatusText(r.rqid);
                sender.tell(new StatusUpdate(r.rqid, status), getSelf());
                break;
            }
            default:{
                updateStatus(r.rqid, r.status);
                break;
            }
        }
    }

    private String getStatusText(String id){
        if(tasks.size() == 0){
            return "";
        } else {
            String retval = "";
            if(id != null) {
                ExecutionStatus s = tasks.get(id);
                if(s != null){
                    retval = s.status;
                } else {
                    retval = "'No such task";
                }
            }
            return retval;
        }
    }

    private void newRequest(NewRequest r){
        log.info("Processing new request [{}, {}, {}, {}, ]", r.rqid, r.component, r.project, r.batch);
        log.info("Data Lake root = {}", dataLakeRoot);

        // Create new component executor
        ActorRef executor = getContext().actorOf(TaskCoordinator.props(dataLakeRoot, jvmCmd), "tc-" + l);
        l++;
        // Keep track of executors - store executor info locally
        tasks.put(r.rqid, new ExecutionStatus(executor, r.rqid, StatusUpdate.PREPARING));
        executor.tell(r, self());
    }
    private void cleanup(){
        if(tasks.size() > 0){
            for(Map.Entry<String, ExecutionStatus> e : tasks.entrySet()){
                if(e.getValue().status.equals("COMPLETED") ){
                    e.getValue().executor.tell(new CleanRequest(), self());
                }
            }
        }
    }
}
