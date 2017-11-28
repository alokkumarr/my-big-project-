package sncr.xdf.rest.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import sncr.xdf.rest.AskHelper;
import sncr.xdf.rest.messages.StatusUpdate;
import sncr.xdf.rest.messages.dl.Create;
import sncr.xdf.rest.messages.dl.Delete;
import sncr.xdf.rest.messages.dl.Document;
import sncr.xdf.rest.messages.dl.ListOf;

import java.util.ArrayList;
import java.util.List;

import static akka.pattern.PatternsCS.ask;

// This class coordinates all request for data lake management

public class MainDataLakeCoordinator extends MainJVMCoordinator {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public MainDataLakeCoordinator(int numberOfExecutors,  String dataLakeRoot, String jvmCmd){
        super(numberOfExecutors,
              dataLakeRoot,
              "dlsupport",
              "dlsupport",
              "dlsupport",
              jvmCmd);
    }

    public static Props props(Integer numberOfExecutors, String dataLakeRoot, String jvmCmd) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(MainDataLakeCoordinator.class,
                            () -> new MainDataLakeCoordinator(numberOfExecutors, dataLakeRoot, jvmCmd));
    }

    @Override
    public Receive createReceive() {
        return processCommonEvents(DataLakeOpExecutor.props(dataLakeRoot))
            .match(ListOf.class, r -> {
                //TODO: make more generic
                ListOf s;
                ActorRef a = getExecutor();
                log.debug("Get message to process: {}", r.toString() );
                if(a != null) {
                    try {
                        s = AskHelper.ask(r, getExecutor(), 3000L);
                        getSender().tell(s, getSelf());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        List<String> lst = new ArrayList<>();
                        lst.add("{\"error\":\"Data Lake Service is not ready\"}");
                        s = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, lst);
                    }
                } else {
                    // Do nothing
                    log.info("Not ready for request processing");
                    List<String> lst = new ArrayList<>();
                    lst.add("{\"error\":\"Data Lake Service is not ready\"}");
                    s = new ListOf(r.listOf, r.project, r.dataSource, r.catalog, null, null, lst);
                }

                getSender().tell(s, getSelf());
            })
            .match(Document.class, r -> {
                //TODO: make more generic
                Document d;
                ActorRef a = getExecutor();
                log.debug("Get message to process: {}", r.toString() );
                if(a != null) {
                    try {
                        d = AskHelper.ask(r, getExecutor(), 3000L);
                        getSender().tell(d, getSelf());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        List<String> lst = new ArrayList<>();
                        lst.add("{\"error\":\"Data Lake Service is not ready\"}");
                        d = new Document(r.jsMDEntityType, r.project, r.mdEntity);
                    }
                } else {
                    // Do nothing
                    log.info("Not ready for request processing");
                    List<String> lst = new ArrayList<>();
                    lst.add("{\"error\":\"Data Lake Service is not ready\"}");
                    d = new Document(r.jsMDEntityType, r.project, r.mdEntity);
                }

                getSender().tell(d, getSelf());
            })
            .match(Create.class, r -> {
                //TODO: make more generic
                Create retval;
                if(isReady == 1) {
                    // All sync requests
                    try {
                        // Synchronous request - TBD make it async
                        retval = AskHelper.ask(r, getExecutor(), 3000L);
                        getSender().tell(retval, getSelf());
                    } catch(Exception e){
                        log.error(e.getMessage());
                        retval = r;
                        retval.status = "Failed to process 'create' request";
                    }
                } else {
                    // Do nothing
                    log.info("Not ready for request processing");
                    retval = r;
                    retval.status = "Failed, Data Lake Service is not ready";
                }
                getSender().tell(retval, getSelf());
            })
            .match(Delete.class, r -> {
                Delete retval;
                if(isReady == 1) {
                    // All sync requests
                    try {
                        // Synchronous request - TBD make it async
                        retval = AskHelper.ask(r, getExecutor(), 3000L);
                        getSender().tell(retval, getSelf());
                    } catch(Exception e){
                        log.error(e.getMessage());
                        retval = r;
                        retval.status = "Failed to process 'delete' request";
                    }
                } else {
                    // Do nothing
                    log.info("Not ready for request processing");
                    retval = r;
                    retval.status = "Failed, Data Lake Service is not ready";
                }
                getSender().tell(retval, getSelf());

            })
            .match(StatusUpdate.class, r -> {
                log.debug("\n=== Received status update : {}\n", r.toString());
            })
            .match(String.class, s -> {
                // Simulate crash here
                StatusUpdate ss = null;
                // Null pointer exception
                ss.toString();
            })
            .build();
    }
}
