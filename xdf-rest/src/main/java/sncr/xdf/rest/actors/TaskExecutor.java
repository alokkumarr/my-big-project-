package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import sncr.xdf.component.Component;
import sncr.xdf.component.ZeroComponent;
import sncr.xdf.dataprofiler.DataProfilerComponent;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.NewRequest;
import sncr.xdf.rest.messages.StatusUpdate;
import sncr.xdf.sql.SQLComponent;

// This class executing components
// must be in dedicated JVM because of Spark limitation with Spark Sessions
public class TaskExecutor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final Cluster cluster = Cluster.get(getContext().system());
    private ActorRef coordinator = null;


    //subscribe to cluster changes
    @Override
    public void preStart() {
        log.info("Starting up...");
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(),
                          ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        log.info("Shutting down...");
        cluster.unsubscribe(self());
        cluster.shutdown();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Init.class, r-> {
                log.info("Executor[{}] Received init message", r.exeutorNo);
                coordinator = getSender();
                // According to common protocol -
                // Must respond with the same Init message back to coordinator
                // This will set executor availability to 'true'
                coordinator.tell(r, getSelf());
                // Notify coordinator with custom message
                coordinator.tell(new StatusUpdate(r.rqid, StatusUpdate.READY), getSelf());
            })
            .match(NewRequest.class, r -> {
                coordinator= getSender();
                int retval = processRequest(r);
                if(retval == 0)
                    coordinator.tell(new StatusUpdate(r.rqid, StatusUpdate.COMPLETE), getSelf());
                else
                    coordinator.tell(new StatusUpdate(r.rqid, StatusUpdate.FAILED), getSelf());

            })
            .match(CleanRequest.class, r ->{
                // Graceful shutdown
                log.info("Leaving");
                cluster.leave(cluster.selfAddress());
                getContext().stop(getSelf());
            })
            .build();
    }

    private int processRequest(NewRequest r){
        JsonObject conf= new JsonParser().parse(r.componentConfig).getAsJsonObject();
        log.info(conf.toString());
        int retval = -1;

        if(conf.has("zero")){
            ZeroComponent zc = new ZeroComponent();
            retval = Component.startComponent(zc, "zero", r.app, r.batch);
            log.info("Zero Component returned {}", retval);
        } else if(conf.has("csvInspector")){
            log.info("Executed csvInspector, exiting");
            retval = 0;
        } else if(conf.has("sql")){
            SQLComponent sql = new SQLComponent();
            retval = Component.startComponent(sql, "sql", r.app, r.batch);
            log.info("SQL Component returned {}", retval);
        }
        return retval;
    }
}
