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
 * Created by srya0001 on 11/3/2017.
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
        ale.add(DataSetProperties.FinishTS.toString(), new JsonPrimitive(ctx.finishedTs));

        ale.add(DataSetProperties.ComponentProducer.toString(), new JsonPrimitive(ctx.componentName));
        ale.add(DataSetProperties.Transformations.toString(), new JsonPrimitive(ctx.transformationName));
        ale.add(DataSetProperties.Project.toString(), new JsonPrimitive(ctx.applicationID));

        return ale;
    }


    /**
     * The method is to update DS and AuditLog with AuditLog entry
     * @param id - DataSet ID
     * @param dsmd - DataSet metadata
     * @param ale_id
     * @param ale - audit log entry
     */
    public void updateDSWithAuditLog(String id, JsonElement dsmd, String ale_id, JsonElement ale) throws Exception {
        JsonObject dsmdjo = dsmd.getAsJsonObject();
        JsonObject ale_jo = ale.getAsJsonObject();
        ale_jo.add(DataSetProperties.Id.toString(), new JsonPrimitive(ale_id)) ;
        dsmdjo.add("lastALE", ale);
        DSStore ds = new DSStore(dlRoot);
        ds.update(dsmdjo.toString());
    }

    public String createAuditLog(Context ctx, Map<String, JsonElement> mdOutputDSMap, JsonObject ale) throws Exception {
        String ale_id = ctx.applicationID + MetadataStore.delimiter + System.currentTimeMillis();
        JsonArray ale_ds_ja = new JsonArray();
        mdOutputDSMap.keySet().forEach(  k -> ale_ds_ja.add(new JsonPrimitive(k)));
        ale.add("dataSets",  ale_ds_ja);
        als.create(ale_id, ale.toString());
        return ale_id;
    }
}
