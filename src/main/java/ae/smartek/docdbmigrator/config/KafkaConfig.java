/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.docdbmigrator.config;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 *
 * @author houssem
 */
public class KafkaConfig {

    private static KafkaProducer PRODUCER;

    public static KafkaProducer getKafkaProducerInstance() {

        if (PRODUCER == null) {
            String kafka_bootstrap_uri = System.getenv("BOOTSTRAP_URI");
            if ((kafka_bootstrap_uri == null) || (kafka_bootstrap_uri.equals(""))) {
                kafka_bootstrap_uri = "localhost:9092";
            }
            Properties props = new Properties();
            props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
            props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
            props.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
            props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_bootstrap_uri);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            PRODUCER = new KafkaProducer<>(props);
	    System.out.println("producer created");
        }
        return PRODUCER;
    }
}
