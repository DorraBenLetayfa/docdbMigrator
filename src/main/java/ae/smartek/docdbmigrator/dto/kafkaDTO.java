/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.docdbmigrator.dto;

import ae.smartek.docdbmigrator.config.KafkaConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author houssem
 */
public class kafkaDTO {

    public void sendDocument(String topic,String key, String doc) {
        ProducerRecord record = new ProducerRecord(topic,key, doc);
       try{
	       System.out.println("sending record to: "+topic);

           KafkaConfig.getKafkaProducerInstance().send(record);
           System.out.println("record sent");
       }catch(Exception e){
           System.out.println("document "+key+" not sent to kafka");
       }
    }

}
