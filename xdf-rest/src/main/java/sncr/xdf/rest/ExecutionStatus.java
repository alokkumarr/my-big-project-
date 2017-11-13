package sncr.xdf.rest;

import akka.actor.ActorRef;

public class ExecutionStatus {
    public String id;
    public ActorRef executor;
    public String status;

    public ExecutionStatus(ActorRef ref, String id, String status){
        this.executor = ref;
        this.id = id;
        this.status= status;
    }

    public String toJson(){
        // TBD
        String json = "{'id' : '" + id + "', 'status' : '" + status +  "'}";
        return json;
    }

}
