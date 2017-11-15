package sncr.xdf.rest.messages;

import java.util.UUID;

public class NewRequest extends ActorMessage{
    public String component;
    public String batch;
    public String componentConfig;

    public NewRequest(String component, String project, String batch,  String componentConfig) {
        super(UUID.randomUUID().toString());
        this.component = component;
        this.batch = batch;
        this.project = project;
        this.componentConfig = componentConfig;
    }

    public NewRequest(NewRequest other ) {
        super(other.rqid);
        this.component = other.component;
        this.batch = other.batch;
        this.project = other.project;
        this.componentConfig = other.componentConfig;
    }
}

