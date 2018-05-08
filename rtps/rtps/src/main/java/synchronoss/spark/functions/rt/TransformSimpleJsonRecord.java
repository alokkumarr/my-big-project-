package synchronoss.spark.functions.rt;

import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.FlatMapFunction;
import synchronoss.data.generic.model.GenericJsonModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by asor0002 on 8/25/2017.
 *
 */
public class TransformSimpleJsonRecord implements FlatMapFunction<Iterator<ConsumerRecord<String, String>>, String> {

    private static final Logger logger = Logger.getLogger(TransformSimpleJsonRecord.class);

    public Iterator<String> call(Iterator<ConsumerRecord<String, String>> in) throws Exception {
        List<String> ret = new ArrayList<>();
        while (in.hasNext()) {
            String src = in.next().value();
            if (src!= null && !src.isEmpty()) {
                try {
                    // Create resulting record with all URL parameters
                    JsonObject flattenedRecord =  GenericJsonModel.flatten(src);
                    ret.add(flattenedRecord.toString());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    logger.error(src);
                }
            }
        }
        return ret.iterator();
    }
}