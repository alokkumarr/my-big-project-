package sncr.xdf.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.log4j.Logger;
import sncr.xdf.base.MetadataStore;
import sncr.xdf.context.Context;
import sncr.xdf.datasets.conf.DataSetProperties;
import sncr.xdf.metastore.AuditLogStore;
import sncr.xdf.metastore.DSStore;

import java.util.Map;

/**
 * The class provides functionality to
 * create, store and retrieve user activity on data lake.
 *
 *
 */
public class AuditLogService {

    private static final Logger logger = Logger.getLogger(AuditLogService.class);
    private AuditLogStore als;
    private String dlRoot;


    public AuditLogService(String dlr) throws Exception {
        als = new AuditLogStore(dlr);
        dlRoot = dlr;
    }


    /**
     * The audit log entry is processMap user activity log:
     * - when component invocation takes place
     * - what is result of execution
     * - is dataset is created or replaced
     * - and so on.
     * The ALE will also be added to System level Audit log.
     * @param status
     * @return
     */
    public JsonObject generateDSAuditLogEntry(Context ctx,
                                              String status,
                                              Map<String, Map<String, String>> input,
                                              Map<String, Map<String, String>> output) {
        JsonObject ale = new JsonObject();

        ale.add(DataSetProperties.Creator.toString(), new JsonPrimitive(ctx.user));
        ale.add(DataSetProperties.Status.toString(), new JsonPrimitive(status));
        ale.add(DataSetProperties.BatchID.toString(), new JsonPrimitive(ctx.batchID));

        ale.add(DataSetProperties.StartTS.toString(), new JsonPrimitive(ctx.startTs));

        ale.add(DataSetProperties.FinishTS.toString(), new JsonPrimitive((ctx.finishedTs == null)?"":ctx.finishedTs));

        ale.add(DataSetProperties.ComponentProducer.toString(), new JsonPrimitive(ctx.componentName));
        ale.add(DataSetProperties.Transformations.toString(), new JsonPrimitive(ctx.transformationName));
        ale.add(DataSetProperties.Project.toString(), new JsonPrimitive(ctx.applicationID));

        JsonArray ale_ids_ja = new JsonArray();
        input.keySet().forEach(  k -> ale_ids_ja.add(new JsonPrimitive(k)));
        ale.add("inputDataSets",  ale_ids_ja);

        JsonArray ale_ods_ja = new JsonArray();
        output.keySet().forEach(  k -> ale_ods_ja.add(new JsonPrimitive(k)));
        ale.add("outputDataSets",  ale_ods_ja);

        return ale;
    }

    public String createAuditLog(Context ctx, JsonObject ale) throws Exception {
        String ale_id = ctx.applicationID + MetadataStore.delimiter + System.currentTimeMillis();
        als.create(ale_id, ale.toString());
        return ale_id;
    }
}
