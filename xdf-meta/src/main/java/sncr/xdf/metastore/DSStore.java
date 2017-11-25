package sncr.xdf.metastore;

import com.google.gson.*;
import com.mapr.db.MapRDB;
import org.ojai.Document;
import org.ojai.FieldPath;
import org.ojai.store.QueryCondition;
import sncr.xdf.base.MetadataStore;
import sncr.xdf.base.WithSearchInMetastore;
import sncr.xdf.core.file.HFileOperations;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by srya0001 on 10/30/2017.
 * The class provides functions to create, edit, delete and search for datasets (dataset MaprDB table)
 * The functions are designed with restrictions on updating only part of datasets documents and
 * enforcing referential integrity between datasets and transformations
 *
 */
public class DSStore extends MetadataStore implements WithSearchInMetastore {

    private static String TABLE_NAME = "datasets";
    public final String STATUS_SECTION = "asOfNow";
    public final String mutable_fields[] = {"root.userData", "root.transformations.asInput" };

    public DSStore(String fsr) throws Exception {
        super(TABLE_NAME, fsr);
    }

    public static void main(String args[]){
        try {
            String json = args[0];
            System.out.print("Convert to document: " +json);
            String jStr = HFileOperations.readFile(json);
            JsonParser jsonParser = new JsonParser();
            JsonElement je = jsonParser.parse(jStr);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print("Parsed JSON: \n\n" + je.toString() + "\n");

            DSStore dss = new DSStore(null);
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
        _updatePath(id, mutable_fields[0], src);
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
     * @param batchSessionId  -- "batchId":"20174217-211133"            // Must be updated right after excution of component changing data of the data set set initiated by UI or pipeline
     * @throws Exception
     */
    public void updateStatus(String id, String status, String startTS, String finishedTS, String aleId, String batchSessionId) throws Exception {
        JsonObject src = new JsonObject();
        src.add("status", new JsonPrimitive(status));
        src.add("started", new JsonPrimitive(startTS));
        if ( finishedTS != null)
            src.add("finished", new JsonPrimitive(finishedTS));
        else
            src.add("finished", new JsonPrimitive( "" ) );

        src.add("aleId", new JsonPrimitive(aleId));
        src.add("batchId", new JsonPrimitive(batchSessionId));

        _updatePath(id, mutable_fields[1], src);
    }

    /**
     * DS Store specific ( convenience ) method:
     * data set produced by Transformation3, immutable, only one value allowed here
     * @param id  - DS ID
     */
    public void setTransformationProducer(String id, String tranformationId) throws Exception {
        JsonPrimitive p = new JsonPrimitive(tranformationId);
        _updatePath(id, "transformations.asOutput", p);
    }

    public void addTransformationConsumer(String id, String tranformationId) throws Exception {
        Document d = table.findById(id);
        List<Object> transformations = d.getList("transformations.asInput");
        Set<String> newList = new HashSet<>();
        transformations.forEach( t -> newList.add((String)t));
        JsonArray ja = new JsonArray();
        newList.forEach( tid -> ja.add( new JsonPrimitive(tid)));
        _updatePath(id, "transformations.asInput", ja);
    }

    /**
     * The method queries Data Set Meta store to get all datasets as List of serialized JSON by
     * - project AND
     * - category
     * @param project - project to search for;
     * @param category - search criteria: "userData.category"
     * @return  - List of serialized JSON documents
     * @throws Exception
     */
    public List<String> getListOfDSByCategory(String project, String category) throws Exception {
        if (project == null) {
            throw new Exception("Project is not specified");
        }
        if (category == null) {
            throw new Exception("Category is not specified");
        }
        QueryCondition cond = buildQCforProjectAndCategory(project, category);
        cond.close();
        cond.build();
        return convertToString(searchAsList(table, cond));
    }


    /**
     * The method queries Data Set Meta store to get all datasets as List of serialized JSON by
     * - project AND
     * - category AND
     * - subcategory
     * Category and sub-category can be search with 'like' conditions, e.g.: 'myCategoryOf%'
     * @param project  - project to search for;
     * @param category  - search criteria: "userData.category"
     * @param subCategory - search criteria: "userData.subCategory"
     * @return - List of serialized JSON documents
     * @throws Exception
     */
    public List<String> getListOfDSBySubCategory(String project, String category, String subCategory) throws Exception {
        if (project == null) {
            throw new Exception("Project is not specified");
        }
        if (category == null) {
            throw new Exception("Category is not specified");
        }
        if (subCategory == null) {
            throw new Exception("Sub-category is not specified");
        }
        QueryCondition cond = buildQCforProjectAndCategory(project, category);
        if (subCategory.indexOf('%') >= 0)
            cond.like("userData.subCategory", subCategory);
        else
            cond.is("userData.subCategory", QueryCondition.Op.EQUAL, subCategory);
        cond.close();
        cond.build();
        return convertToString(searchAsList(table, cond));
    }

    /**
     * Convenient method to start building query conditions
     * It assumes AND conjunction.
     * @param project - first query parameter
     * @param category - second query parameter
     * @return - pre-build QC
     */
    private QueryCondition buildQCforProjectAndCategory(String project, String category){
        QueryCondition cond = MapRDB.newCondition();
        cond.and();
        cond.is("system.project", QueryCondition.Op.EQUAL, project);
        if (category.indexOf('%') >= 0)
            cond.like("userData.category", category);
        else
            cond.is("userData.category", QueryCondition.Op.EQUAL, category);
        return cond;
    }

}
