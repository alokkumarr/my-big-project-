package sncr.xdf.rest.services;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.FileInfo;
import com.typesafe.config.Config;
import sncr.xdf.rest.messages.dl.Document;
import sncr.xdf.services.DLMetadata;
import sncr.xdf.services.MetadataBase;
import sncr.xdf.rest.actors.MainDataLakeCoordinator;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.dl.Create;
import sncr.xdf.rest.messages.dl.Delete;
import sncr.xdf.rest.messages.dl.ListOf;


import java.util.Map;

import static akka.http.javadsl.server.PathMatchers.segment;
import static sncr.xdf.rest.AskHelper.ask;

public class DataLakeService extends Service {

    private static final String ACTOR_NAME = "dataLakeCoordinator";
    private static final String FULL_ACTOR_NAME = "/user/" + ACTOR_NAME;
    private static final String STATUS = "status";

    public DataLakeService(ActorSystem system, Config config){
        super(system, config);
        String newJvmCmd = config.getString("xdf.rest.task-start-cmd");
        String dataLakeRoot = config.getString("xdf.rest.dl-root");
        Integer numberOfExecutors = 1; // TODO: config.getString("xdf.dl.number-of-executors");

        coordinator = system.actorOf(
            MainDataLakeCoordinator.props(numberOfExecutors, dataLakeRoot, newJvmCmd), ACTOR_NAME);

        Init msg = new Init( 0);
        coordinator.tell(msg, coordinator);
    }

    public Route createRoute(){
         return route(
             pathPrefix("dl",() ->
                route(
                    delete( () ->
                        route(
                            parameter(P_PROJECT, (project) ->
                                route(
                                    path(segment("delete").slash("set"), () ->

                                        parameter("set", (set) ->
                                            parameterMap((parameters) ->
                                                delete(DLMetadata.DS_DL_SETS,
                                                       project,
                                                       parameters.get("src"),
                                                       parameters.get("cat"),
                                                       set)
                                            )
                                        )
                                    ),
                                    path(segment("delete").slash(DLMetadata.PREDEF_RAW_DIR), () ->
                                        parameter("set", (set) ->
                                            parameterMap((parameters) ->
                                                    delete(DLMetadata.PREDEF_RAW_DIR,
                                                        project,
                                                        parameters.get("src"),
                                                        parameters.get("cat"),
                                                        set)
                                          )
                                      )
                                  )
                                )
                            )
                        )
                    ),
                    get( () ->
                        route(
                            parameter(P_PROJECT, (project) ->
                                parameterMap((parameters) ->
                                    route(
                                        path(MetadataBase.DS_DL_SOURCES, () ->
                                            // List of catalogs
                                            getListOf(MetadataBase.DS_DL_SOURCES, project, null, null)
                                        ),
                                        path(MetadataBase.DS_DL_CATALOGS, () ->
                                            // List of catalogs
                                            getListOf(MetadataBase.DS_DL_CATALOGS, project, parameters.get("src"), null)
                                        ),
                                        path(MetadataBase.DS_DL_OBS_SETS, () ->
                                            // List of data sets
                                            getListOf(MetadataBase.DS_DL_SETS, project, parameters.get("src"), parameters.get("cat"))
                                        ),
//TODO:: path is prefixed with DL although we work with metadata
                                        path(MetadataBase.DS_DL_SETS, () ->
                                            // List of datasets from MD store by project and category
                                            getMDListOf(MetadataBase.DS_DL_SETS, project, parameters)
                                        ),
                                        path(MetadataBase.DS_DL_SET, () ->
                                            // List of datasets from MD store by project, category and subcategory
                                            getMDDataSet(MetadataBase.DS_DL_SET, project, parameters)
                                        ),
// end of MD services
                                        path(MetadataBase.PREDEF_RAW_DIR, () ->
                                            getListOf(MetadataBase.PREDEF_RAW_DIR, project, null, parameters.get("cat"))
                                        ),
                                        path("crash", () ->{
                                            // Simulate internal crash
                                            coordinator.tell("crash", coordinator);
                                            return complete(StatusCodes.INTERNAL_SERVER_ERROR);
                                        })
                                    )
                                )
                            ),
                            path(MetadataBase.PROJECTS, () ->
                                getListOf(MetadataBase.PROJECTS, null, null, null)
                            ),
                            path(STATUS, () ->
                                parameter("id", (id) ->
                                    getStatus(id)
                                )
                            )
                        )
                    ),
                    post( () ->
                        parameter("prj", (project) ->
                            route(
                                path(segment("create").slash("project"), () ->
                                    create(MetadataBase.PROJECTS, project, null, null, null, null)
                                ),
                                path(segment("create").slash("set"), () ->
                                    parameter("set", (set) ->
                                        parameterMap((parameters) ->
                                             entity(akka.http.javadsl.unmarshalling.Unmarshaller.entityToString(), (meta) ->
                                                 create(DLMetadata.DS_DL_SETS,
                                                        project,
                                                        parameters.get("src"),
                                                        parameters.get("cat"),
                                                        set,
                                                        meta)
                                             )
                                        )
                                    )
                                ),
                                path(segment("create").slash(DLMetadata.PREDEF_RAW_DIR), () ->
                                         parameter("cat", (cat) ->
                                             create(DLMetadata.PREDEF_RAW_DIR,
                                                    project,
                                                    null,
                                                    cat,
                                                    null,
                                                    null)
                                         )
                                ),
                                path(segment("upload").slash("raw"), () ->
                                    parameterMap((params) ->
                                        uploadedFile("file", (info, file) ->
                                            uploadRawFile(project, info, file.getAbsolutePath(), params.get("cat"))
                                        )
                                    )
                                ) //<-- upload/raw
                            ) //
                        )
                    )
                )
            )
        );
    }

    private Route delete(String what, String project, String source, String catalog, String set){
        String retval;
        try {
            ActorSelection dlc = system.actorSelection(FULL_ACTOR_NAME);
            Delete r = new Delete(what, project, source, catalog, set);
            Delete result = ask(r, dlc, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }
    private Route create(String what, String project, String source, String catalog, String set, String additionalData){
        String retval;
        try {
            ActorSelection dlc = system.actorSelection(FULL_ACTOR_NAME);
            Create r = new Create(what, project, source, catalog, set, additionalData);
            Create result = ask(r, dlc, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }

    private Route getListOf(String what, String project, String datasources, String catalog) {
        String retval;
        try {
            ActorSelection dlc = system.actorSelection(FULL_ACTOR_NAME);
            ListOf r = new ListOf(what, project, datasources, catalog, null, null, null);
            ListOf result = ask(r, dlc, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }

    private Route getMDListOf(String whatfromMD, String project, Map<String, String> parameters) {
        String retval;
        try {
            ActorSelection dlc = system.actorSelection(FULL_ACTOR_NAME);
            ListOf r = new ListOf(whatfromMD, project,  parameters.get("src"),
                                                        parameters.get("cat"),
                                                        parameters.get("category"),
                                                        parameters.get("scategory"), null);
            ListOf result = ask(r, dlc, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }

    private Route getMDDataSet(String whatfromMD, String project, Map<String, String> parameters) {
        String retval;
        try {
            ActorSelection dlc = system.actorSelection(FULL_ACTOR_NAME);
            Document r = new Document(whatfromMD, project, parameters);
            Document result = ask(r, dlc, 3000L);
            retval = result.mdEntity;
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(retval);
    }


    private Route uploadRawFile(String project, FileInfo info, String absolutePath, String directory){
        String retval;
        try {
            //ActorSelection dlc = system.actorSelection(FULL_ACTOR_NAME);
            Create r = new Create(MetadataBase.PREDEF_RAW_DIR, project, null, directory, info.getFileName(), absolutePath);
            Create result = ask(r, coordinator, 3000L);
            retval = result.toJson();
        } catch(Exception e){
            log.error(e.getMessage());
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return complete(StatusCodes.ACCEPTED, retval);
    }

    private Route getStatus(String id){
        return complete(StatusCodes.OK);
    }
}
