package sncr.bda.cli;

import com.google.gson.*;
import com.mapr.db.MapRDB;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.bda.admin.ProjectAdmin;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.metastore.DataSetStore;
import sncr.bda.metastore.ProjectStore;
import sncr.bda.metastore.TransformationStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static sncr.bda.cli.Request.MetaCategory.*;

/**
 * The class handles basic requests to Metadata Store
 * The requests are JSON documents in the following formats
 * {
 *   [
 *     "category" : "DataSet|Tranformation|DataPod|DataSegment",
 *     "action" : "create|delete|update|read|search",
 *     "output" : "<Path and Filename where the result wil be saved to>"
 *     "id" : "<id> - required for create, delete, update and read",
 *     "query" : [
 *         {
 *            "conjunction" : "and - default|or",
 *            "filter" : [
 *              { "field-path" : "field path", "condition" : "=|>|<|!=", "value" : "some value"}
*             ]
 *         },
 *
 *     ]
 *      "source" :
 *      {
 *         <Metadata>
 *      }
 *    ]
 * }
 * Created by srya0001 on 11/4/2017.
 */
public class Request {

    private static final Logger logger = Logger.getLogger(Request.class);

    protected JsonElement request;
    private Actions action;
    private String id;
    private String response;
    private String rFile;
    private JsonObject src;
    private MetaCategory category;
    private String xdfRoot;
    private JsonElement result;
    private OutputStream os;
    private JsonObject query;
    private QueryCondition maprDBCondition;
    private JsonArray filter;
    private JsonParser jsonParser;


    public Request(String jStr)
    {
        jsonParser = new JsonParser();
        request = jsonParser.parse(jStr);
    }


    public void process(){
        try {

            if (request.isJsonArray()) {
            JsonArray ja = request.getAsJsonArray();
            ja.forEach( arrayElem -> {
                if (arrayElem.isJsonObject()){
                    JsonObject jo = arrayElem.getAsJsonObject();
                    try {
                        processItem(jo);
                    } catch (Exception e) {
                        generateResponse("item-processing", e);
                    }
                }else{
                    logger.error("Cannot handle provided JSON item: " + arrayElem);
                }
            });
            }
            else if (request.isJsonObject()){
                JsonObject jo = request.getAsJsonObject();
                processItem(jo);
            }else{
                logger.error("Cannot handle provided JSON");
            }
        } catch (Exception e) {
            generateResponse("process", e);
        }
    }

    private void processItem(JsonObject item){

        if (analyzeAndValidate(item)){
            logger.info("Start item processing, action: " + action + ", output: " + rFile);

            try {
                os = HFileOperations.writeToFile(rFile);
            } catch (FileNotFoundException e1) {
                logger.error("Could not write response to file: " + rFile, e1);
                return;
            }

            try {
                switch (action) {
                    case create:
                    case delete:
                    case update:
                    case read:
                        doAction(item);
                        break;
                    case search:
                        doSearch();
                        break;
                    default:
                        logger.warn("Action is not supported");
                }
            }
            catch(Exception e){
                logger.error("Could not process requested item: ", e);

            }

            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    logger.error("Exception in request destructor: ", e);
                }
            }

        }
        else{
            logger.error("Could not process current item, skip it");
        }
    }


    private void doSearch() throws Exception {

        maprDBCondition =  MapRDB.newCondition();

        boolean toBeClosed = false;
        if (filter.size() > 1) {
            if (query.has("conjunction"))
                if (query.get("conjunction").getAsString().equalsIgnoreCase("or"))
                    maprDBCondition.or();
                else
                    maprDBCondition.and();
            else
                maprDBCondition.and();
            toBeClosed = true;
        }

        filter.forEach(c -> {
            if ( c.isJsonObject() ){
                JsonObject cjo = c.getAsJsonObject();
                String fp = cjo.getAsJsonPrimitive("field-path").getAsString();
                String cond = cjo.getAsJsonPrimitive("condition").getAsString();
                String val = cjo.getAsJsonPrimitive("value").getAsString();
                if (fp != null && fp.isEmpty() && cond != null && cond.isEmpty() && val != null && val.isEmpty())
                {
                    if (cond.equalsIgnoreCase("like"))
                        maprDBCondition.like(fp, val);
                    else
                        maprDBCondition.is(fp, getOperation( cond ), val );
                }else {
                    logger.error("Skip incorrect filter element: " + c.getAsJsonObject().toString());
                }
            }
            else{
                logger.warn("Incorrect query");
            }
        });
        if (toBeClosed) maprDBCondition.close();
        maprDBCondition.build();

        Map<String, Document> searchResult = null;
        switch ( category ){
            case DataSet:
                DataSetStore dss = new DataSetStore(xdfRoot);
                searchResult = dss.search(maprDBCondition);
                break;
            case DataPod:
                logger.warn("Not implemented yet");
                break;
            case DataSegment:
                logger.warn("Not implemented yet");
                break;
            case Transformation:
                TransformationStore tr = new TransformationStore(xdfRoot);
                searchResult = tr.search(maprDBCondition);
                break;
            default:
                logger.error("Not supported category");
                return;
        }
        writeSearchResult(searchResult);
    }

    private void writeSearchResult(Map<String, Document> searchResult) {

        if (searchResult == null || searchResult.isEmpty()) {
            logger.info("No data found");
            return;
        }
        JsonObject response = new JsonObject();
        response.addProperty("scope", "search");
        JsonArray respJA = new JsonArray();
        response.add("result", respJA);
        final int[] c = {0};
        searchResult.forEach( (id, doc) ->
            {
                c[0]++;
                JsonObject docDesc = new JsonObject();
                docDesc.addProperty("id", id);
                docDesc.add(String.valueOf(c[0]), jsonParser.parse(doc.asJsonString()));
                respJA.add(docDesc);
            }
        );
        try {
            os.write(response.toString().getBytes());
        } catch (IOException e) {
            logger.error("Could not write data to response file: ", e);
        }

    }

    private QueryCondition.Op getOperation(String cond) {
        switch (cond){
            case "=":
            case "==":
            case "eq":
            case "EQ":
                return QueryCondition.Op.EQUAL;
            case "!=":
            case "<>":
            case "ne":
            case "NE":
                return QueryCondition.Op.NOT_EQUAL;
            case ">":
            case "gt":
            case "GT":
                return QueryCondition.Op.GREATER;
            case "<":
            case "lt":
            case "LT":
                return QueryCondition.Op.GREATER_OR_EQUAL;
            case ">=":
            case "ge":
            case "GE":
                return QueryCondition.Op.GREATER_OR_EQUAL;
            case "<=":
            case "le":
            case "LE":
                return QueryCondition.Op.LESS_OR_EQUAL;
        }
        return null;
    }


    private void doAction(JsonObject item) throws Exception {
        switch ( category ){
            case DataSet:
                DataSetStore dss = new DataSetStore(xdfRoot);
                switch (action){
                    case create: dss.create(id, src); break;
                    case delete: dss.delete(id); break;
                    case update: dss.update(id, src); break;
                    case read: result = dss.read(id); break;
                    default:
                        logger.warn("Action is not supported");
                }
                break;
            case DataPod:
                logger.warn("Not implemented yet");
                break;
            case DataSegment:
                logger.warn("Not implemented yet");
                break;
            case Project:
                ProjectAdmin ps = new ProjectAdmin(xdfRoot);
                switch (action){
                    case create:
                            ps.createProject(id, src);
                        break;
                    case delete: ps.deleteProject(id); break;
                    case update: ps.updateProject(id, src); break;
                    case read: result = ps.readProjectData(id); break;
                    default:
                        logger.warn("Action is not supported");
                }
                break;
            case Transformation:
                TransformationStore ts = new TransformationStore(xdfRoot);
                switch (action){
                    case create: ts.create(id, item); break;
                    case delete: ts.delete(id); break;
                    case update: ts.update(id, item); break;
                    case read: result = ts.read(id); break;
                    default:
                        logger.warn("Action is not supported");
                }
                break;
            default:
                logger.error("Not supported category");
                return;
        }
        generateResponse("action", null);
    }

    private void generateResponse(String scope, Exception e) {
        try{
            JsonObject response = new JsonObject();
            response.addProperty("scope", scope);
            if (e == null){
                response.add("result", new JsonPrimitive("success"));
                if ( result != null )
                    response.add("metadata", result);

            }else{
                response.add("result", new JsonPrimitive(e.getMessage()));
            }
            logger.debug("Response: \n" + response.toString());
            os.write(response.toString().getBytes());
        } catch (IOException e2) {
            logger.error("IOException at attempt to write result: " + rFile, e2);
            return;
        }

    }

    private boolean analyzeAndValidate(JsonObject item) {

        try {
            if (!(item.has("action") && item.has("output") && item.has("category"))){
                logger.error("Action, output and category keys are mandatory");
                return false;
            }

            rFile = item.get("output").getAsString();
            String a = item.get("action").getAsString();
            if (item.has("xdf-root"))
                xdfRoot = item.get("xdf-root").getAsString();

            String cat = item.get("category").getAsString();
            category = valueOf(cat);

            action = Actions.valueOf(a);

            if (action == Actions.create ||
                action == Actions.delete ||
                action == Actions.update ||
                action == Actions.read)
                return analyzeAndValidateCRUD(item);
            else{
                return analyzeAndValidateSearch(item);
            }
        }
        catch ( Exception e){
            logger.error("Exception at request validation/analysis phase: ", e);
            return false;
        }
    }


    private boolean analyzeAndValidateCRUD(JsonObject item){

        //TODO: Changes required here
        if ((action == Actions.create ||
             action == Actions.delete ||
             action == Actions.update ||
             action == Actions.read) &&
             !item.has("id")){
            logger.error("Requested action requires ID");
            return false;
        }
        if (item.has("id")) id = item.get("id").getAsString();

        if (category == AuditLog)
        {
            logger.error("Create/Update/Delete are not supported for AuditLog");
            return false;
        }

        if ((action == Actions.create || action == Actions.update ) &&  !item.has("source")){
            logger.error("Create/Update action require 'source' body");
            return false;
        }

        if (action == Actions.create || action == Actions.update) {
            JsonElement src0 = item.get("source");
            if (!src0.isJsonObject()) {
                logger.error("'source' must be valid JSON object");
                return false;
            }
            src = src0.getAsJsonObject();

            DateTime currentTime = new DateTime();

            if (action == Actions.create) {
                src.addProperty(DataSetProperties.CreatedTime.toString(), (currentTime.getMillis() / 1000));
            }

            src.addProperty(DataSetProperties.ModifiedTime.toString(), (currentTime.getMillis() / 1000));

        }
        return true;
    }


    private boolean analyzeAndValidateSearch(JsonObject item) {
        if (action == Actions.search && !item.has("query")) {
            logger.error("Provided action requires query");
            return false;
        } else {
            JsonElement query0 = item.get("query");
            if ( query0.isJsonObject() ) {
                query = query0.getAsJsonObject();
                JsonElement filter0 = query.get("filter");
                if (filter0 == null || !filter0.isJsonArray()) {
                    logger.error("Filter should not be empty or null; filter entries should be valid JSON array");
                    return false;
                }
                filter = filter0.getAsJsonArray();
            }
            else{
                logger.error("Query should not be empty or null; Query should be valid JSON object");
                return false;
            }
        }
        return true;
    }


    enum Actions{
        read,
        create,
        update,
        delete,
        search;
    }

    enum MetaCategory{
        Project,
        Transformation,
        DataSet,
        AuditLog,
        DataPod,
        DataSegment;
    }

}
