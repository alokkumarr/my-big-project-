package sncr.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapr.org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.metadata.engine.MetadataStore;
import sncr.metadata.engine.tables;
import sncr.saw.common.config.SAWServiceConfig;

import java.util.Map;

public class AnalysisUtility extends MetadataStore{
    protected static final Logger logger = LoggerFactory.getLogger(AnalysisUtility.class.getName());
    public static void main(String[] args) throws Exception {
        logger.info("Inside AnalysisUtility Class");
        String table = SAWServiceConfig.metadataConfig().getString("path") + "/" + tables.AnalysisMetadata().toString();
        //"/main/metadata/analysis_metadata";
        TableName tn = TableName.valueOf(table);
        Table tab = new AnalysisUtility().connection().getTable(tn);
        Scan scan = new Scan();
        ResultScanner rs = tab.getScanner(scan);
        JsonParser parser = new JsonParser();
        for (Result result : rs) {
            int execFlag = 1;

            Map<byte[], byte[]> familyMap = result.getFamilyMap(Bytes.toBytes("_search"));
            for(Map.Entry<byte[], byte[]> entry : familyMap.entrySet()){
                if (Bytes.toString(entry.getKey()).equals("semanticId")) {
                    execFlag = 0;
                }
            }
            if (execFlag == 1) {
                JsonObject analysis = parser
                    .parse(new String(result.getValue("_source".getBytes(), "content".getBytes())))
                    .getAsJsonObject();
                if (analysis.has("semanticId")) {
                    Put put = new Put(analysis.get("id").getAsString().getBytes());
                    put.addColumn("_search".getBytes(), "semanticId".getBytes(), analysis.get("semanticId").getAsString().getBytes());
                    tab.put(put);
                    logger.info(" Analysis Id = " +analysis.get("id").getAsString()+
                        " and semanticId = "+analysis.get("semanticId").getAsString());
                }
            }
        }
        rs.close();
        logger.info("End of AnalysisUtility Class");
    }
}
