package sncr.bda.generic.services;

import com.mapr.db.exceptions.DBException;
import org.apache.log4j.Logger;
import org.ojai.Document;
import sncr.bda.metastore.DataSetStore;
import sncr.bda.services.DLDataSetService;

import java.util.HashMap;
import java.util.List;

public class DataSetService
{

    private static final Logger logger = Logger.getLogger(DLDataSetService.class);

    private DataSetStore dsStore;

    private String project;
    public String getRoot(){
        return dsStore.getRoot();
    }

    public DataSetService(String fsr, String project) throws Exception {
        dsStore = new DataSetStore(fsr);
        this.project = project;
    }

    private boolean checkDS(String id){
        try{
            Document d = dsStore.getTable().findById(id);
            return ( d != null );
        }
        catch (DBException e){
            logger.error( "Could not check DS in MDDB: ", e);
            return false;
        }
    }

    public String readDataSet(String name){
        try {
            return dsStore.readDataSet(project, name);
        }
        catch (Exception e){
            logger.error( "Could not read from in MDDB: ", e);
            return "";
        }

    }

    public int createDataSet(String id, String src){
        if (checkDS(id)) {
            logger.debug("DS already exists");
            return  -1;
        }
        try {
            dsStore.create(id, src);
            return 0;
        }
        catch (Exception e){
            logger.error("Could not create DS: ", e);
            return -1;
        }
    }

    public int updateDataSet(String id, String src){
        if (!checkDS(id)) {
            logger.debug("DS does not exists");
            return  -1;
        }
        try {
            dsStore.update(id, src);
            return 0;
        }
        catch (Exception e){
            logger.error("Could not update DS: ", e);
            return -1;
        }
    }


    /**
     * The method searches MDDB with given filter,
     * filter should be presented as JSON.
     *	"query" : {
     * "filter" : [
     * { "field-path" : "datalake.catalog", "condition" : "=", "value" : "asorokin" }
     * ]
     * }
     * @param filter
     * @return
     */
    public String[] searchDataSets(String filter){
        try {
            List<Document> result = dsStore.searchAsList(dsStore.getTable(), filter);
            String[] doclist = new String[result.size()];
            int[] i= {0};
            result.forEach(d -> { String js = d.asJsonString(); doclist[i[0]++] = js;});
            return doclist;
        } catch (Exception e) {
            logger.error("Search in MD failed: ", e);
        }
        String[] er = new String[1]; er[0] = "";
        return er;
    }


}
