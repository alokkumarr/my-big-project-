package sncr.xdf.rest.messages.dl;

import sncr.xdf.rest.messages.ActorMessage;

import java.util.UUID;

public class Create extends ActorMessage {

    public String what;
    public String source;
    public String catalog;
    public String set;

    public Create (String what, String project, String source, String catalog, String set, String info) {
        super(UUID.randomUUID().toString());

        this.what = what;
        this.project = project;
        this.source = source;
        this.catalog = catalog;
        this.set = set;
        this.info = info;
        this.status = null;

    }

    public Create (Create o) {
        super(UUID.randomUUID().toString());

        this.what = o.what;
        this.project = o.project;
        this.source = o.source;
        this.catalog = o.catalog;
        this.set = o.set;
        this.info = o.info;
        this.status = o.status;

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
