package sncr.xdf.rest.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import sncr.xdf.rest.AskHelper;

import sncr.xdf.rest.messages.preview.Preview;

// This class coordinates all request for data preview
public class MainPreviewCoordinator extends MainJVMCoordinator {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public MainPreviewCoordinator(int numberOfExecutors){
        super(numberOfExecutors);
        JVM_ROLE = "preview";
        JVM_LOGDIR = JVM_ROLE;
        JVM_LOGNAME = JVM_ROLE + ".log";
    }

    public static Props props(Integer numberOfExecutors) {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        return Props.create(MainPreviewCoordinator.class, () -> new MainPreviewCoordinator(numberOfExecutors));
    }

    @Override
    public Receive createReceive() {
        return processCommonEvents(PreviewExecutor.class, log)
            // business request processing
            .match(Preview.class, p -> {
                //TODO: make more generic
                Preview s = new Preview(p);
                ActorRef executor = getExecutor();
                if(executor != null) {
                    try {
                        s = AskHelper.ask(p, getExecutor(), 3000L);
                        getSender().tell(s, getSelf());
                    } catch(Exception e){
                        log.error(e.getMessage());
                        s.list = "{\"error\":\"Preview Service is not ready\"}";
                    }
                } else {
                    // Do nothing
                    log.info("Not ready for request processing - all executors are busy");
                    s.list = "{\"error\":\"Preview Service is not ready\"}";
                }
                getSender().tell(s, getSelf());
            })
            .build();
    }
}

