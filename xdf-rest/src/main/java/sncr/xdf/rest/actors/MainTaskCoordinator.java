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

    //subscribe to cluster changes
    @Override
    public void preStart() {
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

    private void cleanup(){
        if(tasks.size() > 0){
            for(Map.Entry<String, ExecutionStatus> e : tasks.entrySet()){
                if(e.getValue().status.equals("COMPLETED") ){
                    e.getValue().executor.tell(new CleanRequest(), self());
                }
            }
        }
    }

    private void status(ActorRef sender, StatusUpdate r){
        switch(r.status){
            case StatusUpdate.REQUSET: {
                log.info("Received status request from client");
                String status = getStatusText(r.rqid);
                sender.tell(new StatusUpdate(r.rqid, status), getSelf());
            }
            default:{
                updateStatus(r.rqid, r.status);
            }
        }
    }

    private String getStatusText(String id){
        if(tasks.size() == 0){
            return "['No tasks currently running']";
        } else {
            String retval = "";
            if(id != null) {
                ExecutionStatus s = tasks.get(id);
                if(s != null){
                    retval = s.toJson();
                } else {
                    retval = "'No such task + " + id + "'";
                }
            } else {

                // TBD : not processMap best code below...
                for(Map.Entry<String, ExecutionStatus> e : tasks.entrySet()){
                    retval += e.getValue().toJson() + ",\n";
                }

                retval = "[\n" + retval + "\n]";
            }
            return retval;
        }
    }

    private void newRequest(NewRequest r){
        log.info("Processing new request [{}, {}, {}, {}]", r.rqid, r.component, r.app, r.batch);

        // Create new component executor
        ActorRef executor = getContext().actorOf(TaskCoordinator.props(1), "tc-" + l);
        l++;
        // Keep track of executors - store executor info locally
        tasks.put(r.rqid, new ExecutionStatus(executor, r.rqid, "INIT"));
        executor.tell(r, self());
    }
}
