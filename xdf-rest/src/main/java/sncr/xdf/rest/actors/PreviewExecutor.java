package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import sncr.xdf.preview.CsvInspector;
import sncr.xdf.rest.messages.ActorMessage;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.dl.ListOf;
import sncr.xdf.rest.messages.preview.Preview;
import sncr.xdf.services.DLMetadata;
import sncr.xdf.services.MetadataBase;

public class PreviewExecutor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final Cluster cluster = Cluster.get(getContext().system());
    private ActorRef coordinator;
    private DLMetadata mdt = null;
    private int executorNo = -1;

    private String dataLakeRoot;

    public PreviewExecutor(String dataLakeRoot){
        super();
        this.dataLakeRoot = dataLakeRoot;
    }

    public static Props props(String dataLakeRoot) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(PreviewExecutor.class, () -> new PreviewExecutor(dataLakeRoot));
    }


    @Override
    public void preStart() {
        getContext().system().log().info("Starting {}", this.getClass().getSimpleName() );
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() {
        getContext().system().log().info("Stopping {}[{}]", this.getClass().getSimpleName(), this.executorNo);
        cluster.unsubscribe(self());
    }


    @Override
    public Receive createReceive() {
        try {
            return receiveBuilder().match(Init.class, r -> {
                initialize(r);
                coordinator = getSender();
                getSender().tell(r, getSelf());
                log.info("PreviewExecutor[{}] initialized - waiting for tasks...", this.executorNo);
            }).match(CleanRequest.class, r -> {
                log.info("PreviewExecutor[{}] shutting down...", this.executorNo);
                cluster.leave(cluster.selfAddress());
                getContext().stop(getSelf());
            }).match(Preview.class, p -> {
                processPreviewRequest(getSender(), p);
            }).build();
        } catch(Exception e){
            log.error("Exception:" + e.getMessage());
            return receiveBuilder().build();
        }
    }

    private void initialize(Init r){
        executorNo = r.exeutorNo;
        try {
            this.mdt = new DLMetadata(dataLakeRoot);
        } catch (Exception e) {
            log.error("Can't initialize PreviewExecutor[{}] : {}", r.exeutorNo, e.getMessage());
        }
    }

    private  void processPreviewRequest(ActorRef sender, Preview p) {
        log.info("Executor[{}], project : {}, processing {} request", executorNo, p.project,  p.what);
        ActorMessage result;
        switch(p.what){

            case "inspectRaw":
                result = processCsvInspect(p);
                break;
            case MetadataBase.PREDEF_RAW_DIR:
            default:
                log.error("Executor[{}] : Unknown request : {}", executorNo, p.what);
                result = new Preview(p);
                p.status = "This type of request currently not supported";
        }
        sender.tell(result, getSelf());
    }

    private  ActorMessage processCsvInspect(Preview p) {
        Preview result = new Preview(p);
        try {
            CsvInspector inspector = new CsvInspector(p.info, mdt.getStagingDirectory(p.project));
            inspector.parseSomeLines();
            result.info = inspector.toJson();
        }catch(Exception e){
            log.error(e.getMessage());
            result.status = e.getMessage();
        }
        return result;
    }
}
