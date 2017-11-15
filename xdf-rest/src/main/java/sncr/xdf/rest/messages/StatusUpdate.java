package sncr.xdf.rest.messages;

public class StatusUpdate extends ActorMessage {

    //Predefined statuses
    // Somebody is asking for status update
    public static final String REQUEST = "request";
    public static final String READY = "ready";
    public static final String COMPLETE = "complete";
    public static final String IN_PROGRESS = "in progress";
    public static final String PREPARING = "preparing for execution";
    public String message;


    public StatusUpdate(String id, String status){
        super(id);
        this.status = status;
    }

    public void set(StatusUpdate r){
        this.status = r.status;
    }

    public String toString(){
        return "rqid: " + rqid + " , status: " + status;
    }

}
