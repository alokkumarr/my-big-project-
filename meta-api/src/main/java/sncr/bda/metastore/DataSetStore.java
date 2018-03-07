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

/**
 * Created by srya0001 on 10/30/2017.
 * The class provides functions to create, edit, delete and search for datasets (dataset MaprDB table)
 * The functions are designed with restrictions on updating only part of datasets documents and
 * enforcing referential integrity between datasets and transformations
 *
 */
public class DataSetStore extends MetadataStore implements WithSearchInMetastore {

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
        JsonObject src = createStatusSection(status, startTS, finishedTS, aleId, batchSessionId);
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
    public List<String> getListOfDS(
            String project,
            String dataSource,
            String catalog,
            String category,
            String subCategory
    ) throws Exception {
        if (project.isEmpty()) {
            throw new Exception("ProjectService is empty");
        }
        QueryCondition cond = MapRDB.newCondition();
        cond.and();
        cond.is("system.project", QueryCondition.Op.EQUAL, project);
        if ( category != null && !category.isEmpty()) cond = addEqOrLikeClause(cond, "userData.category", category);

        if ( subCategory != null && !subCategory.isEmpty()
             && category != null && !category.isEmpty())
            cond = addEqOrLikeClause(cond, "userData.subCategory", subCategory);

        if ( catalog != null && !catalog.isEmpty()) cond = addEqOrLikeClause(cond, "system.catalog", catalog);
        if ( dataSource != null && !dataSource.isEmpty()) cond = addEqOrLikeClause(cond, "userData.type", dataSource);

        cond.close();
        cond.build();
        return convertToString(searchAsList(table, cond));
    }

    public String  readDataSet(String project, String name) throws Exception {
        if (project.isEmpty() || name == null || name.isEmpty()) {
            throw new Exception("Search parameters are not correct: either project or name are null or empty.");
        }
        Document res = table.findById(project + delimiter + name);
        return res.asJsonString();
    }

    /**
     * Convenient method to start building query conditions
     * It assumes AND conjunction.
     * @param cond - QueryCondition in the building
     * @param key  - Key to search
     * @param value - search value
     * @return - pre-build QC
     */
    private QueryCondition addEqOrLikeClause(QueryCondition cond, String key, String value){

        if (value.indexOf('%') >= 0)
            cond.like(key, value);
        else
            cond.is(key, QueryCondition.Op.EQUAL, value);
        return cond;
    }


}
