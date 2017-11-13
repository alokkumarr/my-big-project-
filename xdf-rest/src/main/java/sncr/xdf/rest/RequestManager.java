package sncr.xdf.rest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.IncomingConnection;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.HttpMethods;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import akka.util.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import sncr.xdf.services.MetadataBase;
import sncr.xdf.rest.actors.MainDataLakeCoordinator;
import sncr.xdf.rest.actors.MainPreviewCoordinator;
import sncr.xdf.rest.actors.MainTaskCoordinator;
import sncr.xdf.rest.messages.CleanRequest;
import sncr.xdf.rest.messages.Init;
import sncr.xdf.rest.messages.NewRequest;
import sncr.xdf.rest.messages.StatusUpdate;
import sncr.xdf.rest.messages.dl.ListOf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;

public class RequestManager implements Function<HttpRequest, HttpResponse> {

    LoggingAdapter log;

    private ActorSystem system = null;
    private Materializer materializer = null;
    private Source<IncomingConnection, CompletionStage<ServerBinding>> serverSource = null;

    private ActorRef longJobCoordinator = null;
    private ActorRef dataLakeCoordinator = null;
    private ActorRef previewCoordinator = null;


    private Config config = null;
    private String ip;

    private static final String DL = "/dl";
    private static final String PROJECTS = "/dl";
    private static final String DL_DATA_SOURCES = "/dl/DataSources";
    private static final String DL_CATALOG = "/dl/PhysicalLocation";
    private static final String OBJECTS = "/dl/objects";

    public static void main(String[] args){
        System.out.println("Starting server... " + System.getProperty("java.home"));

        String fileName = args[1];
        try {
            String config = new String(Files.readAllBytes(FileSystems.getDefault().getPath(fileName)));
            RequestManager rm = new RequestManager(0, config);
            rm.startServer();
        } catch(java.io.IOException e){
            System.err.println("Configuration file not found " + fileName);
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private RequestManager(Integer seedNo, String configString) {
        // Parse configuration
        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            this.ip = ipAddr.getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Using local IP address " + ip);
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

    private void startServer(){

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

        // Create coordinators
        String newJvmCmd = config.getString("xdf.rest.task-start-cmd");
        String dataLakeRoot = config.getString("xdf.rest.dl-root");

        Init msg = new Init(newJvmCmd, dataLakeRoot, 0);

        longJobCoordinator = system.actorOf(Props.create(MainTaskCoordinator.class), "ljc");
        longJobCoordinator.tell(msg, longJobCoordinator);
        dataLakeCoordinator = system.actorOf(Props.create(MainDataLakeCoordinator.class), "dlc");
        dataLakeCoordinator.tell(msg, dataLakeCoordinator);
        previewCoordinator = system.actorOf(Props.create(MainPreviewCoordinator.class), "prc");
        previewCoordinator.tell(msg, previewCoordinator);

        // Prepare http server
        Integer httpPort = config.getInt("xdf.rest.http-port");
        log.info("Starting http listener on {}:{}", ip, httpPort);

        materializer = ActorMaterializer.create(system);
        serverSource =
            Http.get(system).bind(ConnectHttp.toHost(ip, httpPort), materializer);

        CompletionStage<ServerBinding> serverBindingFuture =
            serverSource.to(Sink.foreach(connection -> {
                System.out.println("Accepted new connection from " + connection.remoteAddress());
                connection.handleWithSyncHandler(this, materializer);
            })).run(materializer);
    }

    // Main HTTP request handler
    @Override
    public HttpResponse apply(HttpRequest request) throws Exception {
        Uri uri = request.getUri();
        if (request.method() == HttpMethods.POST) {
            if (uri.path().equals("/")) {
                return root(uri);
            } else if (uri.path().startsWith("/run")) {
                return run(request);
            } else if (uri.path().startsWith("/dl")) {
                return postForDataLake(request);
            } else if (uri.path().startsWith("/preview")) {
                return postForPreview(request);
            } else {
                return NOT_FOUND;
            }
        } else if(request.method() == HttpMethods.GET) {
            if (uri.path().equals("/")) {
                return root(uri);
            } else if (uri.path().startsWith("/status")) {
                return status(uri);
            } else if (uri.path().startsWith("/clean")) {
                return clean(uri);
            } else if (uri.path().startsWith(DL)) {
                return getForDataLake(request);
            } else if (uri.path().startsWith("/preview")) {
                return getForPreview(request);
            }else
                return NOT_FOUND;
        } else {
            return NOT_FOUND;
        }
    }

    // NOT FOUND response
    private final HttpResponse NOT_FOUND = HttpResponse.create()
        .withStatus(404).withEntity("Unknown resource!");


    // Retreive system status
    private HttpResponse status(Uri uri) throws Exception {
        String[] uriParts = uri.path().split("/");
        StatusUpdate rq;
        if (uriParts.length == 3) {
            // Create status update request for specific task
            rq = new StatusUpdate(uriParts[2], StatusUpdate.REQUSET);
        } else {
            // Create status update request for all tasks
            rq = new StatusUpdate(null, StatusUpdate.REQUSET);
        }
        // Ask main longJobCoordinator to provide status
        CompletableFuture<Object> askStatus = ask(longJobCoordinator, rq, 3000).toCompletableFuture();
        CompletableFuture<StatusUpdate> status = CompletableFuture.allOf(askStatus).thenApply(v -> {
            StatusUpdate s = (StatusUpdate)askStatus.join();
            return new StatusUpdate(s.rqid, s.status);
        });
        StatusUpdate s = status.get();
        return HttpResponse.create().withEntity(s.status);
    }

    public String parse(ByteString line) {
        return line.utf8String();
    }

    // Execute XDF component
    private HttpResponse run(HttpRequest request) throws Exception {
        Uri uri = request.getUri();

        final CompletionStage<HttpEntity.Strict> x
            = request.entity().toStrict(10000, materializer);

        final CompletionStage<String> s =
            x.thenCompose(strict -> {
                return strict.getDataBytes().runFold(
                    ByteString.empty(), (acc, b) ->
                        acc.concat(b), materializer).thenApply(this::parse);
            });

        String componentConfig = s.toCompletableFuture().join();

        // Send new request
        String[] uriParts = uri.path().split("/");
        NewRequest rq = new NewRequest(uriParts[2],  // component
                                       uriParts[3],  // app
                                       uriParts[4],   // batch
                                       config.getString("xdf.rest.task-start-cmd"),
                                       componentConfig
        );
        longJobCoordinator.tell(rq, longJobCoordinator);
        return HttpResponse.create().withEntity( rq.rqid + "SUBMITTED");
    }

    // Cleanup finished actors
    private HttpResponse clean(Uri uri) throws Exception {
        longJobCoordinator.tell(new CleanRequest(), longJobCoordinator);
        return HttpResponse.create().withEntity("{'accepted'}");
    }

    // Root request
    private HttpResponse root(Uri uri) throws Exception {
        return HttpResponse.create()
            .withEntity(ContentTypes.APPLICATION_JSON, "{\"html\" : {\"body\" : \"XDF rest server\"}}");
    }

    private HttpResponse postForDataLake(HttpRequest request) throws Exception {
        return NOT_FOUND;
    }

    private HttpResponse getForDataLake(HttpRequest request) throws Exception {
        Uri uri = request.getUri();
        switch(uri.path()) {
            case PROJECTS: {
                ListOf r = new ListOf(MetadataBase.PROJECTS);
                log.info("Project list request {}", r.rqid);
                CompletableFuture<Object> getList = ask(dataLakeCoordinator, r, 3000).toCompletableFuture();
                CompletableFuture<ListOf> lst = CompletableFuture.allOf(getList).thenApply(v -> {
                    ListOf s = (ListOf)getList.join();
                    return new ListOf(s);
                });
                ListOf s = lst.get();
                log.info("Done {}", r.rqid);
                return HttpResponse.create().withEntity(s.toJson());
            }
            case DL_DATA_SOURCES: {
                String project = uri.query().get("prj").orElse(null);
                if(project != null){
                    ListOf r = new ListOf(MetadataBase.DS_SOURCES, project, null, null, null);
                    log.info("Container list request {} for {}", r.rqid, project);
                    CompletableFuture<Object> getList = ask(dataLakeCoordinator, r, 3000).toCompletableFuture();
                    CompletableFuture<ListOf> lst = CompletableFuture.allOf(getList).thenApply(v -> {
                        ListOf s = (ListOf)getList.join();
                        return new ListOf(s);
                    });
                    ListOf s = lst.get();
                    log.info("Done {}", r.rqid);
                    return HttpResponse.create().withEntity(s.toJson());
                } else {
                    return NOT_FOUND;
                }
            }
            case OBJECTS : {
                String project = uri.query().get("prj").orElse("*");
                String container = uri.query().get("cntr").orElse("*");
                String location = uri.query().get("lcn").orElse("*");
                ListOf r = new ListOf(MetadataBase.DS_SETS, project, container, location, null);
                log.info("Objects list request {} for {}:{}:{}", r.rqid, project, container, location);
                CompletableFuture<Object> getList = ask(dataLakeCoordinator, r, 3000).toCompletableFuture();
                CompletableFuture<ListOf> lst = CompletableFuture.allOf(getList).thenApply(v -> {
                    ListOf s = (ListOf)getList.join();
                    return new ListOf(s);
                });
                ListOf s = lst.get();
                log.info("Done {}", r.rqid);
                return HttpResponse.create().withEntity(s.toJson());

            }
            default : return NOT_FOUND;
        }
    }

    private HttpResponse postForPreview(HttpRequest request) throws Exception {
        return NOT_FOUND;
    }

    private HttpResponse getForPreview(HttpRequest request) throws Exception {

        // Send preview request to coordinator with predefined timeout
        CompletableFuture<Object> askStatus = ask(previewCoordinator, null, 10000).toCompletableFuture();
        CompletableFuture<StatusUpdate> status = CompletableFuture.allOf(askStatus).thenApply(v -> {
            StatusUpdate s = (StatusUpdate)askStatus.join();
            return new StatusUpdate(s.rqid, s.status);
        });
        StatusUpdate s = status.get();
        return HttpResponse.create().withEntity("[\"Response\"]");

    }

}
