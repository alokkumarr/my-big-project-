package mapr.streaming;

import exceptions.ErrorCodes;
import exceptions.RTException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by srya0001 on 4/14/2016.
 */
public class EventSender {

    private static Logger m_log = LoggerFactory.getLogger(EventSender.class.getName());

    private static AtomicLong msg_counter = new AtomicLong(0);

    public String queue;
    private KafkaProducer<String, Object> byte_producer;
    private KafkaProducer<String, String> string_producer;
    private boolean isDevNullDest = false;
    private long lastTS = 0;
    private long hbInt = -1;

    private void initStream(String stream, String topic, Properties p){

        try {
            hbInt = p.containsKey("heartbeat") ? Long.parseLong((String) p.remove("heartbeat")) : -1;
            if (stream.equalsIgnoreCase("none")) {
                isDevNullDest = true;
                queue = null;
                m_log.debug("Will send everything to /dev/null");
            } else {
                queue = "/" + stream + ":" + topic;
                m_log.debug("Create producer for: " + queue);
                string_producer = new KafkaProducer<>(p);
                byte_producer = new KafkaProducer<>(p);
            }
        }
        catch(Exception e){
            m_log.error("Init error: ", e);
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
                synchronized (byte_producer) {
                    ProducerRecord pr = new ProducerRecord(queue, msg);
                    byteProdMD = byte_producer.send(pr);
                }
                m_log.debug("Msg with length {} was successfully sent",msg.length);
                return byteProdMD;
            }
            else
                return null;
        }
        else
        {
            m_log.warn("Message is NULL");
            return null;
        }
    }

    private void heartBeat() {
        long msc = msg_counter.incrementAndGet();
        if (hbInt > 0) {
            long currTS = System.currentTimeMillis();
            if ( currTS - lastTS >= hbInt ){
                m_log.info("LastTS: {}, Msg.cnt: {}",lastTS, msc);
                lastTS = currTS;
            }
        }
    }

    public Future<RecordMetadata> send(String msg, String messageID)
    {
        if (msg != null && !msg.isEmpty()) {
            heartBeat();
            if(!isDevNullDest) {
//                m_log.trace("===> The string message : {}\n to ===> {} ", msg, queue);
                Future<RecordMetadata> stringProdMD;
                synchronized (string_producer) {
                    ProducerRecord pr = new ProducerRecord(queue, msg);
                    stringProdMD = string_producer.send(pr);
                }
                m_log.debug("Msg with length: {} and ID: {} was successfully sent",msg.length(), messageID );
                return stringProdMD;
            }
            else
                return null;
        }
        else
        {
            m_log.warn("Message is NULL");
            return null;
        }
    }
}
