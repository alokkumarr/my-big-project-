package sncr.xdf.rest.services;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.server.AllDirectives;
import com.typesafe.config.Config;
import sncr.xdf.rest.messages.ActorMessage;
import sncr.xdf.rest.messages.CleanRequest;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


public abstract class Service extends AllDirectives {

    protected ActorSystem system;
    protected Config config;
    protected LoggingAdapter log;
    protected ActorRef coordinator;

    // Common parameters
    public static final String P_PROJECT = "prj";

    protected Map<String, ExecutionStatus> tasks;

    Service(ActorSystem system, Config config) {
        this.system = system;
        this.config = config;
        this.log = Logging.getLogger(system, this);
    }

    class ExecutionStatus {
        ActorMessage message;
        ActorRef requestor;
        long initiated;
        long lastUpdated;
        int errorCode;
        String statusMessage;
    }

    protected void addTask(ActorMessage m, ActorRef requestor){
        ExecutionStatus es = new ExecutionStatus();
        es.message = m;
        es.requestor = requestor;
        es.initiated = System.currentTimeMillis();
        es.lastUpdated = System.currentTimeMillis();
        tasks.put(m.rqid, es);
    }

    protected ExecutionStatus getTask(String executionId){
        return tasks.get(executionId);
    }

    protected void updateTask(String executionId, int errorCode, String statusMessage){
        ExecutionStatus es = tasks.get(executionId);
        if(es != null){
            es.lastUpdated = System.currentTimeMillis();
            es.errorCode = errorCode;
            es.statusMessage = statusMessage;
        }
    }

    public void shutdown(){
        coordinator.tell(new CleanRequest(), coordinator);
    }
}
