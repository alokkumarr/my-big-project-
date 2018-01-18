package sncr.bda.metastore;

import sncr.bda.base.MetadataStore;

/**
 * Created by srya0001 on 10/31/2017.
 */
public class WorkflowStore  extends MetadataStore {

    private static String TABLE_NAME = "workflows";

    public WorkflowStore(String fsr) throws Exception {
        super(TABLE_NAME, fsr);
    }

}
