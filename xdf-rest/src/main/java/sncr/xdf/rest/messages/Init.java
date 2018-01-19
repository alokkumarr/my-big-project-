package sncr.xdf.rest.messages;

import java.util.UUID;

public class Init extends ActorMessage{
    public int exeutorNo;

    public Init(int executorNo){
        super(UUID.randomUUID().toString());
        this.exeutorNo = executorNo;
    }
}
