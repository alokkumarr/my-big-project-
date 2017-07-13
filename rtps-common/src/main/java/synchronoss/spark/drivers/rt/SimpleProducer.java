package synchronoss.spark.drivers.rt;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by asor0002 on 6/30/2017.
 */
public class SimpleProducer {
    // Set the stream and topic to publish to.
    public static String topic = "/<path to and name of the stream>:<name of topic>";
    // Set the number of messages to send.
    public static int numMessages = 50;

    // Declare a new producer.
    public static KafkaProducer producer;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        System.out.println("Sending message to stream...");
        configureProducer();
        topic = args[0];
        numMessages = Integer.parseInt(args[1]);
        String messageText = args[2];

        System.out.println(messageText);
        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, messageText);
        // Send the record to the producer client library.
        for(int i = 0; i < numMessages;i++) producer.send(rec);

        producer.close();
        System.out.println("Done");
    }
    /* Set the value for a configuration parameter.
       This configuration parameter specifies which class
       to use to serialize the value of each message.*/
    public static void configureProducer() {
        Properties props = new Properties();
        props.put("value.serializer",
                  "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.serializer",
                  "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(props);
    }


}
