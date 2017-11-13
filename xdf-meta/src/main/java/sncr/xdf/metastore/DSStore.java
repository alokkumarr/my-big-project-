package sncr.xdf.metastore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.xdf.base.MetadataStore;
import sncr.xdf.base.WithSearchInMetastore;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.HFileOperations;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by srya0001 on 10/30/2017.
 */
public class DSStore extends MetadataStore implements WithSearchInMetastore {

    private static String TABLE_NAME = "datasets";

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
        return _search(table, qc);
    }

}
