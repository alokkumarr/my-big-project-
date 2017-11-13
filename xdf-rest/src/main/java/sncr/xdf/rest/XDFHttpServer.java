package sncr.xdf.rest;

import akka.Done;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;

import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.HttpOrigin;
import akka.http.javadsl.model.headers.HttpOriginRange;
import akka.http.javadsl.server.Directives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.RejectionHandler;
import akka.http.javadsl.server.Route;
import akka.japi.function.Function;
import akka.stream.Materializer;


import ch.megard.akka.http.cors.javadsl.settings.CorsSettings;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import sncr.xdf.rest.services.AdminService;
import sncr.xdf.rest.services.DataLakeService;
import sncr.xdf.rest.services.LongRunningJobService;
import sncr.xdf.rest.services.PreviewService;

/* CORS Support */

import static ch.megard.akka.http.cors.javadsl.CorsDirectives.cors;
import static ch.megard.akka.http.cors.javadsl.CorsDirectives.corsRejectionHandler;

public class XDFHttpServer extends HttpApp {

    LoggingAdapter log;

    private Config config = null;
    private String ip;
    private int shutdown = 0;

    private ActorSystem system = null;
    private Materializer materializer = null;


    private DataLakeService dataLakeService;
    private AdminService adminServices;
    private LongRunningJobService longRunningJobService;
    private PreviewService previewService;

    public static void main(String[] args){
        System.out.println("Starting server... " + System.getProperty("java.home"));

        String fileName = args[1];
        try {
            String config = new String(Files.readAllBytes(FileSystems.getDefault().getPath(fileName)));
            XDFHttpServer server = new XDFHttpServer(0, config);
            server.run();
        } catch(java.io.IOException e){
            System.err.println("Configuration file not found " + fileName);
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }

    public XDFHttpServer(Integer seedNo, String configString){
        // Define local network parameters
        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            this.ip = ipAddr.getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Using local IP address " + ip);

        // Parse configuration
        Config extConfig = ConfigFactory.parseString(configString);

        // Extract server specific parameters
        List<Integer> seedPorts = extConfig.getIntList("xdf.rest.seeds");

        // Create final akka config
        this.config = extConfig
            .withFallback(ConfigFactory.load("request-manager"))
            .withFallback(ConfigFactory.load())
            .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(seedPorts.get(seedNo)))
            .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(ip));
    }

    // Shutdown HTTP Server
    public void shutdown(){
        shutdown = 1;
    }

    private void run() throws Exception {
        String actorSystemName = config.getString("xdf.rest.name");
        List<Integer> seedPorts = config.getIntList("xdf.rest.seeds");

        // Initialize actor system
        system = ActorSystem.create(actorSystemName, config);
        log = Logging.getLogger(system, this);

        // Join cluster
        List<Address> seedAddresses = new ArrayList<>();
        for(Integer port : seedPorts){
            seedAddresses.add(new Address("akka.tcp", actorSystemName, ip, port));
        }

        if(seedAddresses.size() > 0)
            Cluster.get(system).joinSeedNodes(seedAddresses);

        // Initialize services
        dataLakeService = new DataLakeService(system, config);
        adminServices = new AdminService(system, config, this);
        longRunningJobService = new LongRunningJobService(system, config);
        previewService = new PreviewService(system, config);

        // Prepare http server
        Integer httpPort = config.getInt("xdf.rest.http-port");
        log.info("Starting http listener on {}:{}", ip, httpPort);
        startServer(ip, httpPort, system);
    }

    @Override
    protected CompletionStage<Done> waitForShutdownSignal (ActorSystem system) {
        final CompletableFuture<Done> promise = new CompletableFuture<>();
        Runtime.getRuntime().addShutdownHook(new Thread(
            () -> {
                system.log().info("Shutdown hook");
                promise.complete(Done.getInstance());
            }
        ));
        CompletableFuture.runAsync(() -> {
            try {
                while(true) {
                    // Run indefinitely, check for shutdown every 5 seconds
                    Thread.sleep(5000);
                    if(shutdown == 1) {
                        system.log().info("Processing shutdown request...");
                        dataLakeService.shutdown();
                        previewService.shutdown();
                        promise.complete(Done.getInstance());
                    }
                }
            } catch (Exception e) {
                systemReference.get().log().error(e, "Problem occurred! " + e.getMessage());
            }
        });
        return promise;
    }

    @Override
    public Route routes() {
        try {
            return createRoutes();
        } catch(Exception  e){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public Route createRoutes() throws Exception {


        // From
        // https://github.com/lomigmegard/akka-http-cors/blob/master/akka-http-cors-example/src/main/java/ch/megard/akka/http/cors/javadsl/CorsServer.java

        // Allow all
        final CorsSettings settings = CorsSettings.defaultSettings();
            //.withAllowedOrigins(HttpOriginRange.create(HttpOrigin.parse(".*")));

        // Rejection handler
        final RejectionHandler rejectionHandler = corsRejectionHandler().withFallback(RejectionHandler.defaultHandler());

        // Exception handler
        final ExceptionHandler exceptionHandler = ExceptionHandler.newBuilder()
            .match(NoSuchElementException.class, ex -> complete(StatusCodes.NOT_FOUND, ex.getMessage()))
            .match(Exception.class, ex -> complete(StatusCodes.NOT_FOUND, ex.getMessage()))
            .build();

        // Combining the two handlers only for convenience
        final Function<Supplier<Route>, Route> handleErrors = inner -> Directives.allOf(
            s -> handleExceptions(exceptionHandler, s),
            s -> handleRejections(rejectionHandler, s),
            inner
        );

        return handleErrors.apply(() -> cors(settings, () ->route(
                            pathEndOrSingleSlash(() -> complete("[\"XDF Server up and running\"]")),
                            dataLakeService.createRoute(),
                            adminServices.createRoute(),
                            longRunningJobService.createRoute(),
                            previewService.createRoute())
                    )
            );
    }
}
