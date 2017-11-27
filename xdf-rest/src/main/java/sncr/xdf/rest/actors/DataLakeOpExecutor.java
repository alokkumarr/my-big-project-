package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import sncr.xdf.metastore.DSStore;
import sncr.xdf.services.DLMetadata;
import sncr.xdf.services.MetadataBase;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.StatusUpdate;
import sncr.xdf.rest.messages.dl.Create;
import sncr.xdf.rest.messages.dl.Delete;
import sncr.xdf.rest.messages.dl.ListOf;

import java.util.ArrayList;
import java.util.List;

public class DataLakeOpExecutor extends AbstractActor{

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final Cluster cluster = Cluster.get(getContext().system());
    private DLMetadata mdt = null;
    private DSStore mdstore = null;
    private int executorNo = -1;
    private ActorRef coordinator;
    private String dataLakeRoot;


    public DataLakeOpExecutor(String dataLakeRoot){
        super();
        this.dataLakeRoot = dataLakeRoot;
    }

    public static Props props(String dataLakeRoot) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(DataLakeOpExecutor.class, () -> new DataLakeOpExecutor(dataLakeRoot));
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
            return receiveBuilder()
            .match(Init.class, r -> {
                initialize(r);
                coordinator = getSender();
                getSender().tell(r, getSelf());
                log.info("DataLakeOpExecutor[{}] initialized - waiting for tasks...", this.executorNo);
            }).match(ListOf.class, r -> {
                processListRequest(getSender(), r);
            }).match(Create.class, r -> {
                processCreateRequest(getSender(), r);
            }).match(Delete.class, r -> {
                processDeleteRequest(getSender(), r);
            }).match(CleanRequest.class, r -> {
                log.info("DataLakeOpExecutor[{}] shutting down", executorNo);
                cluster.leave(cluster.selfAddress());
                getContext().stop(getSelf());
            }).build();
        } catch(Exception e){
            return receiveBuilder().build();
        }
    }

    private void initialize(Init r){
        try {
            this.mdt = new DLMetadata(dataLakeRoot);
            this.executorNo = r.exeutorNo;
            this.mdstore = new DSStore(dataLakeRoot);
        } catch (Exception e) {
            log.error("Can't initialize FileSystem : {}", e.getMessage());
        }
    }

    private void processDeleteRequest(ActorRef sender, Delete r){
        r.status = "success";
        try{
            switch (r.what) {
                case DLMetadata.DS_DL_SETS: {
                    log.info("Deleting {} {} {} {}", r.project, r.source, r.catalog, r.set);
                    mdt.deleteDataSet(r.project, r.source, r.catalog, r.set);
                    sender.tell(r, getSelf());
                    break;
                }
            }
        } catch (Exception e){
            log.error(e.getMessage());
            r.status = e.getMessage();
            sender.tell(r, getSelf());
        }

    }

    private void processCreateRequest(ActorRef sender, Create r){
        r.status = "success";
        try{
            switch (r.what) {
                case DLMetadata.PROJECTS: {
                    mdt.createProject(r.project);
                    sender.tell(r, getSelf());
                    break;
                }
                case DLMetadata.DS_DL_SETS: {
                    // Do something with metadata
                    String metadata = r.info;

                    mdt.createDataSet(r.project, r.source, r.catalog, r.set, metadata);
                    sender.tell(r, getSelf());
                    break;
                }
                case DLMetadata.PREDEF_RAW_DIR: {
                    // Upload file into RAW directory - async operation
                    processMoveToRawRequest(sender, r);
                    break;
                }
            }
        } catch (Exception e){
            log.error(e.getMessage());
            r.status = e.getMessage();
            sender.tell(r, getSelf());
        }
    }

    public void processListRequest(ActorRef sender, ListOf r) {
        ListOf l = null;
        log.debug("Received message: {}, ", r.toString());
        try {
            switch (r.listOf) {
                case MetadataBase.PROJECTS: {
                    ArrayList<String> list = mdt.getListOfProjects();
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, list);
                    break;
                }
                case MetadataBase.DS_DL_SOURCES: {
                    ArrayList<String> list = mdt.getListOfDataSources(r.project);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, list);
                    break;
                }
                case MetadataBase.DS_DL_CATALOGS: {
                    List<String> list = mdt.getListOfCatalogs(r.project, r.dataSource);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, list);
                    break;
                }
                case MetadataBase.DS_DL_SETS: {
                    List<String> list = mdt.getListOfSets(r.project, r.dataSource, r.catalog);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, list);
                    break;
                }
                case MetadataBase.PREDEF_RAW_DIR: {
                    List<String> list = mdt.getListOfStagedFiles(r.project,  r.catalog);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, list);
                    break;
                }
//Working with MD
                case MetadataBase.DS_MD_CATEGORY: {
                    List<String> list = mdstore.getListOfDSByCategory(r.project, r.category);
                    l = new ListOf(r.listOf, r.project, null, null, r.category, null, list);
                    break;
                }
                case MetadataBase.DS_MD_SUBCATEGORY: {
                    List<String> list = mdstore.getListOfDSBySubCategory(r.project,  r.category, r.subCategory);
                    l = new ListOf(r.listOf, r.project, null, null, r.category, r.subCategory, list);
                    break;
                }
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        log.debug("Result message: " + l.toString());
        sender.tell(l, getSelf());
    }

    private void processMoveToRawRequest(ActorRef sender, Create r){
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // This is bullshit - mixing async and sync
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // This request can take some time to process
        // First notify sender request has been accepted
        r.status = "accepted";
        sender.tell(r, getSelf());

        // Call metadata manager to move file into raw folder
        StatusUpdate update = new StatusUpdate(r.rqid, "success");
        try {
            // following conciderations are used here
            // -- r.info contains absolute local path
            // -- r.set contains original file name, as it was specified in HTTP request
            int i = mdt.moveToRaw(r.project, r.info, r.catalog, r.set);
        } catch(Exception e){
            log.error(e.getMessage());
            update.message = e.getMessage();
        }

        // Notify sender with status update
        coordinator.tell(update, getSelf());
    }




}
