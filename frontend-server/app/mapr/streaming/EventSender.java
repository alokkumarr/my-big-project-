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

    private static AtomicInteger msg_counter = new AtomicInteger(0);

    public final String queue;
    private KafkaProducer<String, Object> byte_producer;
    private KafkaProducer<String, String> string_producer;


    public EventSender(Properties p){
        m_log.debug("Read properties:");
        String stream = (String) p.get("queue_name");
        String topic = (String) p.get("topic");
        queue = "/" + stream + ":" + topic;
        m_log.debug("Create producer for: " + queue);

        string_producer = new KafkaProducer<>(p);
        byte_producer = new KafkaProducer<>(p);
    }

    public EventSender(String s, String t, Properties p){
        m_log.debug("Read properties:");
        queue = "/" + s + ":" + t;
        m_log.debug("Create producer for: " + queue);
        string_producer = new KafkaProducer<>(p);
        byte_producer = new KafkaProducer<>(p);
    }

    public Future<RecordMetadata> send(byte[] msg)
    {
        if (msg != null && msg.length != 0) {
            Future<RecordMetadata> byteProdMD;
            int msc;
            synchronized (byte_producer)
            {
                ProducerRecord pr = new ProducerRecord(queue, msg);
                byteProdMD = byte_producer.send(pr);
                msc = msg_counter.incrementAndGet();
            }

            m_log.debug( "The message # " + msc + " with length [ " + msg.length + "] was successfully sent" );
            return byteProdMD;
        }
        else
        {
            m_log.warn("Message is NULL");
            return null;
        }
    }

    public Future<RecordMetadata> send(String msg, String messageID)
    {
        if (msg != null && !msg.isEmpty()) {
            m_log.trace( "===> The string message : " + msg + "\n to ===> " + queue);
            Future<RecordMetadata> stringProdMD;
            int msc = msg_counter.incrementAndGet();
            m_log.debug( "Sent message ID: " + messageID  + " Sent messages so far: " + msc);
            synchronized (string_producer)
            {
                ProducerRecord pr = new ProducerRecord(queue, msg);
                stringProdMD = string_producer.send(pr);
            }
            m_log.debug( "The string message #  " + msc + " with length [ " + msg.length() + "] was successfully sent" );
            return stringProdMD;
        }
        else
        {
            m_log.warn("Message is NULL");
            return null;
        }
    }
}
