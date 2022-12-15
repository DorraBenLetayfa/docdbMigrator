/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.cosmosmigrator.dto;

import ae.smartek.cosmosmigrator.config.KafkaConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author houssem
 */
public class kafkaDTO {

    public void sendDocument(String topic,String key, String doc) {
        ProducerRecord record = new ProducerRecord(topic,key, doc);
       try{
           KafkaConfig.getKafkaProducerInstance().send(record);
       }catch(Exception e){
           System.out.println("document "+key+" not sent to kafka");
       }
    }

}