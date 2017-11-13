package sncr.xdf.rest.messages;

import java.util.UUID;

public class NewRequest extends ActorMessage{
    public String component;
    public String batch;
    public String app;
    public String taskCmd;
    public String componentConfig;


    public NewRequest(String component, String app, String batch, String taskCmd, String componentConfig) {
        //super("1");
        super(UUID.randomUUID().toString());
        this.component = component;
        this.batch = batch;
        this.app = app;
        this.taskCmd = taskCmd;
        this.componentConfig = componentConfig;
    }

    public NewRequest(NewRequest other ) {
        super(other.rqid);
        this.component = other.component;
        this.batch = other.batch;
        this.app = other.app;
        this.taskCmd = other.taskCmd;
        this.componentConfig = other.componentConfig;
    }
}

