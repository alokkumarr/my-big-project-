package synchronoss.spark.functions.rt;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by asor0002 on 6/30/2017.
 */
public class TransformSimpleRecord implements FlatMapFunction<Iterator<ConsumerRecord<String, String>>, String> {
    public Iterator<String> call(Iterator<ConsumerRecord<String, String>> in) throws Exception {
        List<String> ret = new ArrayList<>();
        while (in.hasNext()) {
            String src = in.next().value();
            if (!src.isEmpty()) {
                ret.add(src);
            }
        }
        return ret.iterator();
    }

}
