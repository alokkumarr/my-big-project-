package sncr.xdf.rest.messages;

import java.io.Serializable;

// Base class for all messages used by XDF Rest Server Actors
// When user request is served multiple Actors will communicate to each other
public class ActorMessage implements Serializable {
    // User request id
    public static final String FAILED = "failed";
    public static final String SUCCESS = "success";
    public final String rqid;
    public String user;
    public String project;
    public String status;
    public String info;

    public ActorMessage(String rqid){
        this.rqid = rqid;
        this.status = SUCCESS;
    }

    public String getJson(){
        return "{\"id\" : \"" + rqid + "\", \"status\" : \"" + status + "\"}";
    }
}
