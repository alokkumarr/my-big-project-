package sncr.xdf.rest.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
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

public class DataLakeOpExecutor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final Cluster cluster = Cluster.get(getContext().system());
    private DLMetadata mdt = null;
    private int executorNo = -1;
    private ActorRef coordinator;

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
        return receiveBuilder()
            .match(Init.class, r -> {
                initialize(r);
                coordinator = getSender();
                getSender().tell(r, getSelf());
                log.info("DataLakeOpExecutor[{}] initialized - waiting for tasks...", this.executorNo);
            }).match(ListOf.class, r -> {
                processListRequest(getSender(), r);
            })
            .match(Create.class, r -> {
                processCreateRequest(getSender(), r);
            })
            .match(Delete.class, r -> {
                processDeleteRequest(getSender(), r);
            })
            .match(CleanRequest.class, r ->{
                log.info("DataLakeOpExecutor[{}] shutting down", executorNo);
                cluster.leave(cluster.selfAddress());
                getContext().stop(getSelf());
            })
            .match(ClusterEvent.MemberExited.class, mExited -> {
                // Ignore
                log.info("Exited event received from {}", mExited.member().address());
                if(mExited.member().hasRole("RequestManager")) {
                    log.info("Looks like one of leaders just left...");
                    log.info("Should I follow?");
                }
            })
            .build();
    }

    private void initialize(Init r){
        try {
            this.mdt = new DLMetadata(r.dataLakeRoot);
            this.executorNo = r.exeutorNo;
        } catch (Exception e) {
            log.error("Can't initialize FileSystem : {}", e.getMessage());
        }
    }

    private void processDeleteRequest(ActorRef sender, Delete r){
        r.status = "success";
        try{
            switch (r.what) {
                case DLMetadata.DS_SETS: {
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
                case DLMetadata.DS_SETS: {
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
        try {
            switch (r.listOf) {
                case MetadataBase.PROJECTS: {
                    ArrayList<String> list = mdt.getListOfProjects();
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, list);
                    break;
                }
                case MetadataBase.DS_SOURCES: {
                    ArrayList<String> list = mdt.getListOfDataSources(r.project);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, list);
                    break;
                }
                case MetadataBase.DS_CATALOGS: {
                    List<String> list = mdt.getListOfCatalogs(r.project, r.dataSource);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, list);
                    break;
                }
                case MetadataBase.DS_SETS: {
                    List<String> list = mdt.getListOfSets(r.project, r.dataSource, r.catalog);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, list);
                    break;
                }
                case MetadataBase.PREDEF_RAW_DIR: {
                    List<String> list = mdt.getListOfStagedFiles(r.project,  r.catalog);
                    l = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, list);
                    break;
                }
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
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
