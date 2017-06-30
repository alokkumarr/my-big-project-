package synchronoss.spark.functions.rt;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;
import synchronoss.data.countly.model.CountlyModel;
import synchronoss.data.generic.model.GenericJsonModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asor0002 on 10/7/2016.
 *
 */
public class SaveBatchToElasticSearch implements Function2<JavaRDD<Row>, Time, JavaRDD<Row>> {

    private static final Logger logger = Logger.getLogger(SaveBatchToElasticSearch.class);
    private static final long serialVersionUID = 5823416713928063158L;
    private String esIndex;
    private String esType;
    private Map<String, String> esConfig;
    private StructType schema = null;
    private String definitions;
    private static List<Column> esColumns = null;

    public SaveBatchToElasticSearch(String esIndex, Map<String, String> esConfig, String definitions) {

        if(esIndex != null && !esIndex.isEmpty()) {
            if (esIndex.indexOf("/") > 0) {
                // This means we have Index/Type
                this.esIndex = esIndex.split("/")[0];
                this.esType = "/" + esIndex.split("/")[1];
            } else {
                this.esIndex = esIndex;
                this.esType = "";
            }
        } else {
            this.esIndex = null;
        }

        this.esConfig = esConfig;
        this.definitions = definitions;
    }

    public SaveBatchToElasticSearch(String esIndex, Map<String, String> esConfig) {
        this.esIndex = esIndex;
        this.esConfig = esConfig;
        this.definitions = null;
    }

    public JavaRDD<Row> call(JavaRDD<Row> rdd, org.apache.spark.streaming.Time time){
        // Some applications may not need to store data in ES
        // Check if index name has been provided - if not (null or empty) don't try to store data in ES
        if(esIndex != null && !esIndex.isEmpty()) {
            if (schema == null) {
                if (definitions == null) {
                    schema = CountlyModel.createGlobalSchema();
                } else {
                    schema = GenericJsonModel.createGlobalSchema(definitions);
                    esColumns = GenericJsonModel.createColumnList("", definitions);
                }
            }

            if (!rdd.isEmpty()) {
                SparkSession sess = SparkSession.builder().config(rdd.context().getConf()).getOrCreate();
                StructType s = null;
                Dataset<Row > df = null;

                if (esColumns != null && esColumns.size() > 0)
                    df = sess.createDataFrame(rdd.rdd(), s, true)
                        .select(scala.collection.JavaConversions.asScalaBuffer(esColumns));
                else
                    df = sess.createDataFrame(rdd, schema);

                // See configuration files for detailed explanations
                // on how data is written to ES
                // We use ES.Hadoop settings to control this flow
                String currentEsIndex = SaveBatchToElasticSearch.parseIndexName(esIndex, new Date()) + esType;
                JavaEsSparkSQL.saveToEs(df, currentEsIndex, esConfig);

                sess.close();
            }
        }
        return rdd;
    }

    // We have to support time based indexes
    // Configuration can specify index name with multiple date/time formats in curly brackets
    // In this case we will create index name based on current timestamp
    public static String parseIndexName(String template, Date now) {

        String indexName = template;
        Pattern patternIndexName = Pattern.compile("\\{(.*?)\\}");

        Matcher matchPattern = patternIndexName.matcher(template);
        boolean result = matchPattern.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String format = matchPattern.group(1);
                String replacement;
                DateFormat df = new SimpleDateFormat(format);
                replacement = df.format(now);

                matchPattern.appendReplacement(sb, replacement);
                result = matchPattern.find();
            } while (result);
            matchPattern.appendTail(sb);
            indexName = sb.toString();
        }
        return indexName.toLowerCase();
    }
}
