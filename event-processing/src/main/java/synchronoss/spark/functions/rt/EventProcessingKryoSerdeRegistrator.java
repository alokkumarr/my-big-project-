package synchronoss.spark.functions.rt;

import com.esotericsoftware.kryo.Kryo;
import org.apache.spark.serializer.KryoRegistrator;

/**
 * Created by asor0002 on 7/28/2016.
 */
public class EventProcessingKryoSerdeRegistrator implements KryoRegistrator {
    public void registerClasses(Kryo kryo) {
        // Nothing to register yet
    }
}
