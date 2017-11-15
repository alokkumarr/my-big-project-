package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.remote.RemoteScope;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;

abstract class MainJVMCoordinator extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    // Akka cluster reference
    protected Cluster cluster = Cluster.get(getContext().system());

    protected String dataLakeRoot;
    // JVM related attributes
    // Command to start new JVM
    protected String JVM_CMD;
    // AKKA role of JVM, will be used to recognize this coordinator's JVM
    // among others
    protected String JVM_ROLE;
    // JVM log name
    protected String JVM_LOGNAME;
    // JVM log directory
    protected String JVM_LOGDIR;
    // Service Readiness flag
    protected Integer isReady = 0;
    // Array of independent executors to support (relatively)long tasks
    protected ActorRef[] executors = null;
    protected boolean[] executorsAvailability = null;
    // Remote address to deploy executors (can be separate local JVM or remote JVM)
    // As of now we are supporting local JVMs only
    // In general we support following relations 1 coordinator to 1 JVM
    // In some cases (e.g. Spark jobs) this model doesn't work - we will need 1 coordinator to multiple JVM model
    protected Deploy d = null;

    protected MainJVMCoordinator(int numberOfExecutors,
                                 String dataLakeRoot,
                                 String JVM_ROLE,
                                 String JVM_LOGDIR,
                                 String JVM_LOGNAME,
                                 String JVM_CMD){
        executors = new ActorRef[numberOfExecutors];
        executorsAvailability = new boolean[numberOfExecutors];
        for(int i = 0; i < numberOfExecutors; i++)
            executorsAvailability[i] = false;

        this.dataLakeRoot = dataLakeRoot;

        this.JVM_CMD = JVM_CMD;
        this.JVM_ROLE = JVM_ROLE;
        this.JVM_LOGNAME = JVM_LOGNAME;
        this.JVM_LOGDIR = JVM_LOGDIR;
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
        log.info("Starting...");
        cluster.subscribe(
            self(),
            ClusterEvent.initialStateAsEvents(),
            ClusterEvent.MemberEvent.class,
            ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() {
        log.info("Stopping...");
        cluster.unsubscribe(self());
    }

    // Restart JVM
    private void processUnreachableMember(ClusterEvent.UnreachableMember mUnreachable){
        // Check if member represents Data Lake Support JVM
        for (String s : mUnreachable.member().getRoles()) {
            if (JVM_ROLE != null && JVM_ROLE.equals(s)) {
                // his is JVM corresponding to (created by) this Coordinator
                log.warning("{}: JVM is down - removing {} from cluster", JVM_ROLE, mUnreachable.member());
                cluster.down(mUnreachable.member().address());
                isReady = 0;
                recover();
            }
        }
    }

    // Call back defining what to do in case of corresponding JVM become Unreachable
    protected void recover(){
        // Restart and reinitialize JVM
        log.warning("{}: JVM is down - reinitializing", JVM_ROLE);
        initialize(JVM_CMD, JVM_ROLE, JVM_LOGDIR, JVM_LOGNAME, log);
    }

    // Process standard Akka events
    protected ReceiveBuilder processCommonEvents(Props executorProps){
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
                            executors[i] = getContext().actorOf(executorProps.withDeploy(d), JVM_ROLE + i);
                            // Start task processing
                            executors[i].tell(new Init(i), getSelf());
                        }
                    }
                    break;
                }
            })
            .match(ClusterEvent.UnreachableMember.class, mUnreachable ->
                processUnreachableMember(mUnreachable)
            )
            .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                // Ignore
            })
            .match(CleanRequest.class, r ->{
                shutdown(r);
            })
            .match(Init.class, r -> {
                if(getSender().equals(getSelf())){
                    // This message came from Service class, requesting initialization
                    // Only Service will use address of this coordinator as a sender
                    // This is initialization request
                    initialize(JVM_CMD, JVM_ROLE, JVM_LOGDIR, JVM_LOGNAME, log);
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

    // Shutdown actor and JVM
    protected void shutdown(CleanRequest r){
        log.info("Shutting down...");
        isReady = 0;
        for(int i = 0; i < executors.length; i++)
            executors[i].tell(r, getSelf());
        getContext().stop(getSelf());
    }

    // Start new JVM
    protected static int initialize(String newJvmCmd, String role, String logDir, String logName, LoggingAdapter log){
        // Start new JVM for Data Lake operations
        String cmd = newJvmCmd + " " + role + " " + logDir + " " + logName;
        log.info("Staring new JVM. Command line : {} ", cmd);

        try {
            java.lang.Runtime.getRuntime().exec(cmd);
            return 0;
        } catch(Exception e){
            log.error(e.getMessage());
            return -1;
        }
    }
}

