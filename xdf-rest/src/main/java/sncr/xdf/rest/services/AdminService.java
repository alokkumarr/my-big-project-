package sncr.xdf.rest.services;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.typesafe.config.Config;
import sncr.xdf.rest.XDFHttpServer;

import static akka.http.javadsl.server.PathMatchers.segment;

public class AdminService extends AllDirectives {
    private ActorSystem system = null;
    private Config config;
    LoggingAdapter log;

    String newJvmCmd;
    String dataLakeRoot;

    private XDFHttpServer server;

    public AdminService(ActorSystem system, Config config, XDFHttpServer server) {
        this.system = system;
        this.config = config;
        this.log = Logging.getLogger(system, this);
        this.server = server;

        this.newJvmCmd = config.getString("xdf.rest.task-start-cmd");
        this.dataLakeRoot = config.getString("xdf.rest.dl-root");
    }

    public Route createRoute(){
        return route(
            pathPrefix("admin",() ->
                route(
                    get( () ->
                        route(
                            path("shutdown", () -> {
                                log.info("Shutting down REST service");
                                server.shutdown();
                                return complete("[\"Server is going down...\"]");
                            }),
                            path("root", () -> {
                                return complete("[\"" + dataLakeRoot + "\"]");
                            }),
                            path("newjvmcmd", () -> {
                                return complete("[\"" + newJvmCmd + "\"]");
                            })

                        )
                    )
                )
            )
        );
    }
}
