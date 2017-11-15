package sncr.xdf.rest.actors;

import akka.actor.ActorRef;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.RemoteScope;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.NewRequest;
import sncr.xdf.rest.messages.StatusUpdate;

// This class coordinates all request executions
// It will create separate JVM (akka cluster node) for execution
// We need Separate JVM since Spark allowing only one SparkContext per JVM
public class TaskCoordinator extends MainJVMCoordinator {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private NewRequest newRequest;
    private ActorRef mainCoordinator;

    public TaskCoordinator(String dataLakeRoot, String jvmCmd){
        super(1,
              dataLakeRoot,
              null,
              null,
              null,
              jvmCmd);
        JVM_ROLE = "SparkTaskCoordinator"; ///?
    }

    public static Props props(String dataLakeRoot, String jvmCmd) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(TaskCoordinator.class, () -> new TaskCoordinator(dataLakeRoot, jvmCmd));
    }

    @Override
    public Receive createReceive() {
        return processCommonEvents(TaskExecutor.props(dataLakeRoot))
            // New execution request (from main coordinator)
            .match(NewRequest.class, r -> {
                // Received new request
                log.info("Preparing execution for [{}, {}, {}, {}]", r.rqid, r.component, r.project, r.batch);

                mainCoordinator = getSender();
                newRequest = new NewRequest(r);
                // Notify main coordinator with status - we are preparing for execution
                mainCoordinator.tell(new StatusUpdate(newRequest.rqid, "Preparing for execution"), getSelf());
                // Start new dedicated cluster node/JVM for our task
                JVM_LOGNAME = r.component + "-" + r.batch;
                JVM_LOGDIR = r.component;
                JVM_ROLE = r.rqid;
                initialize(JVM_CMD, JVM_ROLE, JVM_LOGDIR, JVM_LOGNAME, log);
            })
            // Cleanup request TBD (from main coordinator)
            .match(CleanRequest.class, r ->{
                log.info("Shutting down...");
                getExecutor().tell(r, getSelf());
                getContext().stop(getSelf());
            })
            // Received status update
            .match(StatusUpdate.class, r-> {
                status(getSender(), r);
            })
            .build();
    }

    private void status(ActorRef sender, StatusUpdate r){
        switch(r.status){
            case StatusUpdate.READY: {
                log.info("Received READY status from {}", sender);
                log.info("My executor is {}", getExecutor());

                if(newRequest != null ) {
                    // Task executor is ready - sending task
                    getExecutor().tell(newRequest, getSelf());
                } else {
                    log.error("New request info is null");
                }
            }

            case StatusUpdate.COMPLETE:{
                if(sender.equals(getExecutor())){
                    mainCoordinator.tell(r, getSelf());
                    getExecutor().tell(new CleanRequest(), getSelf());
                }
                break;
            }

            case StatusUpdate.FAILED:{
                if(sender.equals(getExecutor())){
                    log.error("Execution of {} failed", newRequest.component);
                    log.info("Shutting down Executor and JVM");
                    getExecutor().tell(new CleanRequest(), getSelf());
                    log.info("Notifying main coordinator");
                    mainCoordinator.tell(r, getSelf());
                }
                break;
            }

            default:{
                // We got status update from task executor
                // notify main coordinator
                mainCoordinator.tell(r, getSelf());
                //latestStatus.set(r);
            }
        }
    }

    @Override
    protected void recover(){
        if(newRequest != null)
            log.info("JVM for {} ({}, {}) has been downed.", newRequest.component, newRequest.project, newRequest.batch);
    }
}
