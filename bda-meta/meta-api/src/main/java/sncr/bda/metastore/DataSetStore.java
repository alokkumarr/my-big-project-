package sncr.bda.metastore;

import com.google.gson.*;
import com.mapr.db.MapRDB;
import org.ojai.Document;
import org.ojai.store.DocumentMutation;
import org.ojai.store.QueryCondition;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;
import sncr.bda.core.file.HFileOperations;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.Optional;
import sncr.bda.datasets.conf.DataSetProperties;
import org.apache.log4j.Logger;
import java.util.stream.Collectors;

/**
 * Created by srya0001 on 10/30/2017.
 * The class provides functions to create, edit, delete and search for datasets (dataset MaprDB table)
 * The functions are designed with restrictions on updating only part of datasets documents and
 * enforcing referential integrity between datasets and transformations
 *
 */
public class DataSetStore extends MetadataStore implements WithSearchInMetastore {
    private static final Logger logger = Logger.getLogger(DataSetStore.class);
    public static String TABLE_NAME = "datasets";
    public final String STATUS_SECTION = "asOfNow";
    public final String USER_DATA = "userData";

    public DataSetStore(String fsr) throws Exception {
        super(TABLE_NAME, fsr);
    }

    public static void main(String args[]){
        try {
            String json = args[0];
            System.out.print("Convert to document: " +json);
            String jStr = HFileOperations.readFile(json);
            JsonParser jsonParser = new JsonParser();
            JsonElement je = jsonParser.parse(jStr);
            System.out.print("Parsed JSON: \n\n" + je.toString() + "\n");
            DataSetStore dss = new DataSetStore(null);
            Document d = dss.toMapRDBDocument(je);
            System.out.print("Converted to document: \n\n" + d.asJsonString() + "\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Document> search(QueryCondition qc) throws Exception {
        return searchAsMap(table, qc);
    }

    /**
     * DS Store specific ( convenience ) method:
     * update user data of DS metadata
     * @param id - DS ID
     * @param src - new metadata
     * @throws Exception
     */
    public void updateUserData(String id, JsonElement src) throws Exception {
        _updatePath(id, null, USER_DATA, src);
    }


    /**
     *  DS Store specific ( convenience ) method:
     *  update DS status before and after transformation was applied
     *
     * @param id      -- DS ID
     * @param status  -- "status":"INIT|SUCCESS|FAILED",                // Must be updated before and after excution of component changing data of the data set initiated by UI or pipeline
     * @param startTS -- "started":"20171117-214242",                   // Must be right before excution of component changing data of the data set set initiated by UI or pipeline
     * @param finishedTS -- "finished":"20171117-214745",               // Must be updated right after excution of component changing data of the data set set initiated by UI or pipeline
     * @param aleId      --  "aleId":"project1::1510955142031",         // last ALE ID (audit log entry ID - for future use, ALE will contain detailed info about component execution)
     * @param batchSessionId  -- "batchId":"20174217-211133"            // Must be updated right after execution of component changing data of the data set set initiated by UI or pipeline
     * @throws Exception
     */
    public void updateStatus(String id, String status, String startTS, String finishedTS, String aleId, String batchSessionId) throws Exception {
        updateStatus(id, status, startTS, finishedTS, aleId, batchSessionId,Optional.empty(),Optional.empty());
    }
    public void updateStatus(String id, String status, String startTS, String finishedTS, String aleId, String batchSessionId, Optional<Integer> returnCode, Optional<String> errorDesc) throws Exception {
        JsonObject src = createStatusSection(status, startTS, finishedTS, aleId, batchSessionId, returnCode, errorDesc);
        _updatePath(id, null, "asOfNow", src);
    }

    public void addTransformationConsumer(String id, String tranformationId) throws Exception {
        Document d = table.findById(id);
        List<Object> transformations = d.getList("transformations.asInput");
        Set<String> newList = new HashSet<>();
        newList.add(tranformationId);
        if (transformations != null && !transformations.isEmpty())transformations.forEach( t -> newList.add((String)t));
        JsonArray ja = new JsonArray();
        newList.forEach( tid -> ja.add( new JsonPrimitive(tid)));
        _updatePath(id, "transformations", "asInput", ja);
    }
    /**
     * The method queries Data Set Meta store to get all datasets as List of serialized JSON by
     * - project AND
     * - category
     * - subcategory
     * - catalog
     * - dataSource
     * @return  - List of serialized JSON documents
     * @throws Exception
     */
    public List<String> getListOfDS(String project, Map<DataSetProperties, String[]> searchParams) throws Exception
    {
        if (project == null || project.trim().isEmpty()) {
            throw new Exception("ProjectService is empty");
        }
        QueryCondition cond = MapRDB.newCondition();
        cond.and();
        // The below has been commented for the JIRA-ID SIP-5727 & it will be activated when workbench module
        // will start using project metadata store & dynamically retrieve the project store
        //cond.is("system.project", QueryCondition.Op.EQUAL, project);
        
        // The below code is to skip filters irrespective of any project
        cond.like("system.project", "%");
        if(searchParams != null && !searchParams.isEmpty()) {
            for(Map.Entry<DataSetProperties, String[]> entry : searchParams.entrySet()){
                DataSetProperties searchParam = entry.getKey();
                String[] values  =  entry.getValue();
                logger.debug("getListOfDS() : searchParam: "+searchParam+" - values: "+ Arrays.toString(values));
                if ( values != null && values.length != 0){
                    switch (searchParam) {
                        case Category:
                            addQueryCondition(cond, "system.category", values);
                            break;
                        case SubCategory:
                            if (searchParams.get(DataSetProperties.Category) != null
                                && searchParams.get(DataSetProperties.Category).length != 0) {
                                addQueryCondition(cond, "userData.subCategory", values);
                            }
                            break;
                        case Catalog:
                            addQueryCondition(cond, "system.catalog", values);
                            break;
                        case DataSource:
                            addQueryCondition(cond, "userData.type", values);
                            break;
                        case Type:
                            addQueryCondition(cond, "system.dstype", values);
                            break;
                    }
                }
            }
        }
        cond.close();
        cond.build();
        return convertToString(searchAsList(table, cond));
    }

    public String  readDataSet(String project, String datasetName) throws Exception {
        if (project.isEmpty() || datasetName == null || datasetName.isEmpty()) {
            throw new Exception("Search parameters are not correct: either project or name are null or empty.");
        }
        Document res = table.findById(project + delimiter + datasetName);
        return res.asJsonString();
    }
    private void addQueryCondition(QueryCondition cond, String key, String[] values){
        logger.debug("addQueryCondition() - Key: "+key+" - values: "+ Arrays.toString(values));
        List<String> valuesList = Arrays.stream(values)
            .filter(value -> (value != null && !value.trim().isEmpty()))
            .map(value -> value.trim())
            .collect(Collectors.toList());
        logger.debug("addQueryCondition() - valuesList: "+ valuesList);
        if(valuesList.size() == 1) {
            addEqOrLikeClause(cond, key, valuesList.get(0));
        }else if(valuesList.size() > 1){
            addInClause(cond, key, valuesList);
        }
    }
    /**
     * Convenient method to start building query conditions
     * It assumes AND conjunction.
     * @param cond - QueryCondition in the building
     * @param key  - Key to search
     * @param value - search value
     * @return - pre-build QC
     */
    private void addEqOrLikeClause(QueryCondition cond, String key, String value){
        logger.debug("addInClause() - Key: "+key+" - value: "+ value);
        if (value.indexOf('%') >= 0)
            cond.like(key, value);
        else
            cond.is(key, QueryCondition.Op.EQUAL, value);
    }
    private void addInClause(QueryCondition cond, String key, List<String> values){
        logger.debug("addInClause() - Key: "+key+" - values: "+ values);
        cond.in(key, values);
    }
}
