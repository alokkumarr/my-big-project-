package sncr.xdf.rest.messages.dl;

import sncr.xdf.rest.messages.ActorMessage;

import java.util.UUID;

public class Delete extends ActorMessage {

    public String what;
    public String source;
    public String catalog;
    public String set;

    public Delete (String what, String project, String source, String catalog, String set) {
        super(UUID.randomUUID().toString());

        this.what = what;
        this.project = project;
        this.source = source;
        this.catalog = catalog;
        this.set = set;
        this.status = null;

    }

    public String toJson(){
        StringBuilder sb = new StringBuilder();
        sb.append('{')
            .append('"')
            .append("status")
            .append('"')
            .append(':')
            .append('"')
            .append(status)
            .append("\",")
            .append("\"id\":\"")
            .append(rqid)
            .append("\"")
            .append('}');
        return sb.toString();
    }

}
