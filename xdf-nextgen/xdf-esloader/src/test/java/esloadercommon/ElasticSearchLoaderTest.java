package esloadercommon;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sncr.bda.conf.Alias;
import sncr.bda.conf.ESLoader;
import sncr.xdf.esloader.esloadercommon.ESConfig;
import sncr.xdf.esloader.esloadercommon.ElasticSearchLoader;
import sncr.xdf.esloader.esloadercommon.ElasticSearchStructureManager;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static sncr.xdf.esloader.esloadercommon.ElasticSearchLoader.*;

/**
 * Created by skbm0001 on 1/2/2018.
 */
public class ElasticSearchLoaderTest {

    ESConfig config;
    String esHost = "localhost";
    String esUser = "elasticsearch";
    String esPassword = "elasticsearch";
    int esPort = 9200;
    String index = "index1";
    String clusterName = "elasticsearch";

    ArrayList<Row> dataEntries;

    @Before
    public void setUp() {
        config = new ESConfig(esHost, esUser, esPassword, esPort, index);
        config.setEsClusterName(clusterName);

        dataEntries = new ArrayList<>();
    }

    @Test
    public void testGenerateESParamMap() {
        Map<String, String> paramMap = ElasticSearchLoader.generateESParamMap(config);

        assertEquals(esHost, paramMap.get(ES_PARAM_NODES));
        assertEquals(esUser, paramMap.get(ES_PARAM_USER));
        assertEquals(esPassword, paramMap.get(ES_PARAM_PASSWORD));
        assertEquals(String.valueOf(esPort), paramMap.get(ES_PARAM_ADMIN_PORT));
        assertEquals(clusterName, paramMap.get(ES_PARAM_CLUSTER));
    }

    @Test
    @Ignore
    public void testGetParsedIndexNameAndType() {
        ESLoader configuration = new ESLoader("index/type", "file:///this",
                null, null, new ArrayList<Alias>(){{
                    add(new Alias("alias1", Alias.Mode.APPEND));
        }}, "localhost", "elasticsearch",
                9200, "id1", "elastic", "elastic");
        ElasticSearchStructureManager essm = new ElasticSearchStructureManager(configuration);
        String partition = null;

        try {
            String indexType = essm.getParsedIndexNameAndType(partition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testFilterData() {
        addDataEntry(1, "Sunil" ,"Kumar", "Synchronoss", "Senior Software Engineer", new DateTime().getMillis());
        addDataEntry(2, "Amar" ,"N", "Microsoft", "Consultant 2", new DateTime().getMillis());
        addDataEntry(3, "Akshatha", "G", "Synchronoss", "Senior Software Test Engineer", new DateTime().getMillis());
    }

    private void addDataEntry(int userId, String firstName, String lastName, String org, String designation, long createdTs) {
        dataEntries.add(RowFactory.create(userId, firstName, lastName, org, designation, createdTs));
    }

    private StructType getSchema() {
        ArrayList<StructField> fields = new ArrayList<StructField>(){{
            add(intField("userId"));
            add(stringField("firstName"));
            add(stringField("lastName"));
            add(stringField("organisation"));
            add(stringField("designation"));
            add(longField("createdTS"));
        }};

        return DataTypes.createStructType(fields);
    }

    private StructField intField(String name) {
        return field(name, DataTypes.IntegerType);
    }

    private StructField longField(String name) {
        return field(name, DataTypes.LongType);
    }

    private StructField stringField(String name) {
        return field(name, DataTypes.StringType);
    }

    private StructField field(String name, DataType type) {
        return DataTypes.createStructField(name, type, false);
    }
}
