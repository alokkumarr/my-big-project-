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

    public TaskCoordinator(int numberOfExecutors){
        super(numberOfExecutors);
        JVM_ROLE = "SparkTaskCoordinator"; ///?
    }

    public static Props props(Integer numberOfExecutors) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(TaskCoordinator.class, () -> new TaskCoordinator(numberOfExecutors));
    }


    @Override
    public Receive createReceive() {
        return processCommonEvents(TaskExecutor.class, log)

            //===============================================
            // Application events/messages
            //
            // New execution request (from main coordinator)
            .match(NewRequest.class, r -> {
                // Received new request
                log.info("Preparing execution for [{}, {}, {}, {}]", r.rqid, r.component, r.app, r.batch);
                mainCoordinator = getSender();
                newRequest = new NewRequest(r);
                // Notify main coordinator with status - we are preparing for execution
                mainCoordinator.tell(new StatusUpdate(newRequest.rqid, "Preparing for execution"), getSelf());
                // Start new dedicated cluster node/JVM for our task
                initialize("", r.taskCmd, r.rqid, log);
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
                    mainCoordinator.tell(r, getSelf());
                    getExecutor().tell(new CleanRequest(), getSelf());
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



    /*
    //===============================================
            // Cluster events
            //
            // New node cluster node/JVM is up
            .match(ClusterEvent.MemberUp.class, mUp -> {
                // Check if new member node/JVM was requested by this instance
                // We can use role which has been set during startup of the node

                // Look for role passed to child during startup - should math our request id
                for(String s : mUp.member().getRoles()){
                    if(newRequest.rqid.equals(s)){
                        // This is node/JVM we have requested - it is now available
                        // Start new child task actor inside dedicated node/JVM
                        log.info("New task executor @ {}", mUp.member().address() );
                        Deploy d = new Deploy(new RemoteScope(mUp.member().address()));

                        if(newRequest != null){
                            //executors[0] = getContext().actorOf(Props.create(TaskExecutor.class).withDeploy(d), newRequest.rqid);
                            // Start task processing
                            //executor.tell(newRequest, getSelf());
                            // Notify main coordinator
                            mainCoordinator.tell(new StatusUpdate(newRequest.rqid, "Execution started"), getSelf());
                        } else {
                            log.error("Can't start task executor - task request is null (incorrect event sequence)");
                            mainCoordinator.tell(new StatusUpdate(newRequest.rqid, "Failed : Can't start task executor - task request is null (incorrect event sequence)"), getSelf());
                        }
                        break;
                    } //<-- if(...)
                } //<-- for(...)
            })
            .match(ClusterEvent.UnreachableMember.class, mUnreachable -> {
                log.info("Member detected as unreachable: {}", mUnreachable.member());
                log.info("Downing : {}", mUnreachable.member());
                cluster.down(mUnreachable.member().address());
            })
     */

}
