package synchronoss.spark.functions.rt;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import scala.reflect.ClassTag$;
import synchronoss.data.countly.model.CountlyModel;
import synchronoss.data.generic.model.GenericJsonModel;

/**
 * Created by asor0002 on 10/7/2016.
 *  Convert RDD of JSONs represented as strings to Rows
 */
public class ConvertToRows implements Function2<JavaRDD<String>, Time, JavaRDD<Row>> {

    private static final Logger logger = Logger.getLogger(ConvertToRows.class);
    private static final long serialVersionUID = -1938247437058741522L;
    private StructType schema = null;
    private String json;

    public ConvertToRows(String json){
        this.json = json;
    }

    public ConvertToRows() {
        this.json = null;
    }
    public JavaRDD<Row> call(JavaRDD<String> rdd, org.apache.spark.streaming.Time time){
        if(schema == null){
            if(json == null)
                schema = CountlyModel.createGlobalSchema();
            else
                schema = GenericJsonModel.createGlobalSchema(json);
        }

        if (!rdd.isEmpty()) {
            SQLContext sqlContext = new org.apache.spark.sql.SQLContext(rdd.context());
            return sqlContext.read().schema(schema).json(rdd).toJavaRDD();
        } else {
            return new JavaSparkContext(rdd.context()).emptyRDD();
        }
    }
}
