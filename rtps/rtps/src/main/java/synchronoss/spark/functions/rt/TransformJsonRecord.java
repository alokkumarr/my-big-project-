package synchronoss.spark.functions.rt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import scala.Tuple2;
import synchronoss.data.generic.model.GenericJsonModel;
import synchronoss.data.generic.model.transformation.Transform;
import synchronoss.spark.drivers.rt.EventProcessingApplicationDriver;

import java.util.*;

/**
 * Created by asor0002 on 7/26/2016.
 *
 */
public class TransformJsonRecord implements FlatMapFunction<Iterator<ConsumerRecord<String, String>>, String> {

    private static final Logger logger = Logger.getLogger(TransformJsonRecord.class);
    private static final long serialVersionUID = -3110743591467895247L;

    private String fieldDefinitionJSON;
    private static Map<String, List<Transform>> transformations;
    private static Set<String> types;

    public TransformJsonRecord(String fieldDefinitionJSON) {
        this.fieldDefinitionJSON = fieldDefinitionJSON;
        transformations = null;
        types = null;
    }

    public Iterator<String> call(Iterator<ConsumerRecord<String, String>> in) throws Exception {
        List<String> ret = new ArrayList<>();

        if (transformations == null) {
            transformations = GenericJsonModel.createTransformationsList(fieldDefinitionJSON);
        }

        if(types == null){
            types = GenericJsonModel.getObjectTypeList(fieldDefinitionJSON);
        }

        while (in.hasNext()) {
            String src = in.next().value();
            if (src!= null && !src.isEmpty()) {
                try {

                    JsonObject flattenedRecord =  GenericJsonModel.flatten(src);
                    String transformedRecord = GenericJsonModel.transform(flattenedRecord, "EVENT_TYPE", types, transformations);
                    ret.add(transformedRecord);
                } catch (com.google.gson.stream.MalformedJsonException e){
                    logger.error("Malformed JSON");
                    logger.error(e.getMessage());
                    logger.error(src);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    logger.error(src);
                }
            }
        }
        return ret.iterator();
    }
}

