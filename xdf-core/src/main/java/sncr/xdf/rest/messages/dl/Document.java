package sncr.xdf.rest.messages.dl;

import sncr.xdf.rest.messages.ActorMessage;
import java.util.Map;
import java.util.UUID;

/**
 * Created by srya0001 on 11/27/2017.
 */
public class Document extends ActorMessage {

    public final String jsMDEntityType;
    public final String project;
    public final String name;
    public final String mdEntity;


    public Document(String mdEntityType, String project, Map<String, String> parameters) {
        super(UUID.randomUUID().toString());
        jsMDEntityType = mdEntityType;
        this.project = project;
        name = parameters.get("name");
        mdEntity = null;
    }

    public Document(String mdEntityType, String project, String doc){
        super(UUID.randomUUID().toString());
        jsMDEntityType = mdEntityType;
        this.project = project;
        mdEntity = doc;
        name = null;
    }

    public String toString(){
        return mdEntity;
    }
}
