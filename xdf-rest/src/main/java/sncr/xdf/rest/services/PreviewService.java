package sncr.xdf.rest.services;


import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.typesafe.config.Config;
import sncr.xdf.rest.actors.MainDataLakeCoordinator;
import sncr.xdf.rest.actors.MainPreviewCoordinator;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.dl.ListOf;
import sncr.xdf.rest.messages.preview.Preview;
import sncr.xdf.services.MetadataBase;

import static akka.http.javadsl.server.PathMatchers.segment;
import static sncr.xdf.rest.AskHelper.ask;

public class PreviewService extends Service {

    private static final String ACTOR_NAME = "previewCoordinator";
    private static final String FULL_ACTOR_NAME = "/user/" + ACTOR_NAME;

    public PreviewService(ActorSystem system, Config config) {
        super(system, config);

        String newJvmCmd = config.getString("xdf.rest.task-start-cmd");
        String dataLakeRoot = config.getString("xdf.rest.dl-root");
        Integer numberOfExecutors = 3; // TODO: config.getString("xdf.preview.number-of-executors");

        coordinator = system.actorOf(MainPreviewCoordinator.props(numberOfExecutors), ACTOR_NAME);

        Init msg = new Init(newJvmCmd, dataLakeRoot, 0);
        coordinator.tell(msg, coordinator);
    }

    public Route createRoute(){
        return route(
            pathPrefix("preview",() ->
                parameter(P_PROJECT, (project) ->
                    route(
                        get(() ->
                            route(
                                path(MetadataBase.PREDEF_RAW_DIR, () ->
                                    parameter("cat", (cat) ->
                                        parameter("set", (set) ->
                                            previewRawFile(project,
                                                       cat,
                                                       set)
                                        )
                                    )
                                )
                            )
                        ),
                        post(() ->
                            route(
                                path(segment(MetadataBase.PREDEF_RAW_DIR).slash("inspect") , () ->
                                    entity(akka.http.javadsl.unmarshalling.Unmarshaller.entityToString(), (config) ->
                                        inspect(project, config)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );
    }

    private Route inspect(String project, String config){
        String retval;
        try {
            ActorSelection preview = system.actorSelection(FULL_ACTOR_NAME);
            Preview p = new Preview("inspectRaw", project, null, null, null, config, null);
            Preview result = ask(p, preview, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }

    private Route previewRawFile(String project, String cat, String file){
        String retval;
        try {
            ActorSelection preview = system.actorSelection(FULL_ACTOR_NAME);
            Preview p = new Preview(MetadataBase.PREDEF_RAW_DIR, project, null, cat, file, null, null);
            Preview result = ask(p, preview, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }
}
