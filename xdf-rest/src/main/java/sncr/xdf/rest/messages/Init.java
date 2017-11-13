package sncr.xdf.rest.messages;

import java.util.UUID;

public class Init extends ActorMessage{
    public String newJvmCmd;
    public String dataLakeRoot;
    public int exeutorNo;

    public Init(String newJvmCmd, String dataLakeRoot, int executorNo){
        super(UUID.randomUUID().toString());
        this.newJvmCmd = newJvmCmd;
        this.dataLakeRoot = dataLakeRoot;
        this.exeutorNo = executorNo;
    }
}
