package sncr.xdf.rest.services;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.typesafe.config.Config;
import sncr.xdf.rest.AskHelper;
import sncr.xdf.rest.actors.MainTaskCoordinator;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.NewRequest;
import sncr.xdf.rest.messages.StatusUpdate;

import static akka.http.javadsl.server.PathMatchers.segment;
import static sncr.xdf.rest.AskHelper.ask;

public class LongRunningJobService extends Service {

    private static final String ACTOR_NAME = "ljc";
    private static final String FULL_ACTOR_NAME = "/user/" + ACTOR_NAME;
    private static final String STATUS = "status";

    String newJvmCmd;


    public LongRunningJobService(ActorSystem system, Config config) {
        super(system, config);
        coordinator = system.actorOf(Props.create(MainTaskCoordinator.class), ACTOR_NAME );

        newJvmCmd = config.getString("xdf.rest.task-start-cmd");
        String dataLakeRoot = config.getString("xdf.rest.dl-root");
        Init msg = new Init(newJvmCmd, dataLakeRoot, 0);
        coordinator.tell(msg, coordinator);
    }

    public Route createRoute(){
        return route(
            pathPrefix("run",() ->
                route(
                    get(() ->
                        path(STATUS, () ->
                            parameter("id", (id) ->
                                status(id)
                            )
                        )
                    ),
                    post(() ->
                        parameter(P_PROJECT, (project) ->
                            parameter("component", (component) ->
                                parameter("batch", (batch) ->
                                    entity(akka.http.javadsl.unmarshalling.Unmarshaller.entityToString(), (cnf) ->
                                        run(project, component, batch, cnf)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );
    }

    private Route run(String project, String component, String batch, String config){

        log.info("Request to run component [{}] for project [{}] batch [{}]", component, project, batch);
        NewRequest rq = new NewRequest(component,  // component
                                       project,  // app
                                       batch,   // batch
                                       newJvmCmd,
                                       config
        );
        coordinator.tell(rq, coordinator);
        return complete(rq.rqid);

    }

    private Route status(String id){
        StatusUpdate rq;

        // Create status update request for specific task
        rq = new StatusUpdate(id, StatusUpdate.REQUSET);

        // Ask main longJobCoordinator to provide status
        try {
            StatusUpdate s = AskHelper.ask(rq, coordinator, 3000L);
            return complete(s.status);
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
