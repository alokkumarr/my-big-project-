package mapr.streaming;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by srya0001 on 4/14/2016.
 */
public class EventSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSender.class.getName());

    private static AtomicLong msgCounter = new AtomicLong(0);

    protected String queue;
    private KafkaProducer<String, Object> byteProducer;
    private KafkaProducer<String, String> stringProducer;
    private boolean isDevNullDest = false;
    private long lastTS = 0;
    private long hbInt = -1;

    private void initStream(String stream, String topic, Properties p){

        try {
            hbInt = p.containsKey("heartbeat") ? Long.parseLong((String) p.remove("heartbeat")) : -1;
            if (stream.equalsIgnoreCase("none")) {
                isDevNullDest = true;
                queue = null;
                LOGGER.debug("Will send everything to /dev/null");
            } else {
                queue = "/" + stream + ":" + topic;
                LOGGER.debug("Create producer for: {}",  queue);
                stringProducer = new KafkaProducer<>(p);
                byteProducer = new KafkaProducer<>(p);
            }
        }
        catch(Exception e){
            LOGGER.error("Init error: ", e);
        }
    }

    public EventSender(Properties p){
        String stream = (String) p.get("queue_name");
        String topic = (String) p.get("topic");
        initStream(stream, topic, p);
    }

    public EventSender(String s, String t, Properties p){
        initStream(s, t, p);
    }

    public Future<RecordMetadata> send(byte[] msg)
    {
        if (msg != null && msg.length != 0) {
            heartBeat();
            if (!isDevNullDest) {
                Future<RecordMetadata> byteProdMD;
                synchronized (byteProducer) {
                    ProducerRecord pr = new ProducerRecord(queue, msg);
                    byteProdMD = byteProducer.send(pr);
                }
                LOGGER.debug("Msg with length {} was successfully sent",msg.length);
                return byteProdMD;
            }
            else
                return null;
        }
        else
        {
            LOGGER.warn("Message is NULL");
            return null;
        }
    }

    private void heartBeat() {
        long msc = msgCounter.incrementAndGet();
        if (hbInt > 0) {
            long currTS = System.currentTimeMillis();
            if ( currTS - lastTS >= hbInt ){
                LOGGER.info("LastTS: {}, Msg.cnt: {}",lastTS, msc);
                lastTS = currTS;
            }
        }
    }

    public Future<RecordMetadata> send(String msg, String messageID)
    {
        if (msg != null && !msg.isEmpty()) {
            heartBeat();
            if(!isDevNullDest) {
                Future<RecordMetadata> stringProdMD;
                synchronized (stringProducer) {
                    ProducerRecord pr = new ProducerRecord(queue, msg);
                    stringProdMD = stringProducer.send(pr);
                }
                LOGGER.debug("Msg with length: {} and ID: {} was successfully sent",msg.length(), messageID );
                return stringProdMD;
            }
            else
                return null;
        }
        else
        {
            LOGGER.warn("Message is NULL");
            return null;
        }
    }
}
