/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.cosmosmigrator.service;

import ae.smartek.cosmosmigrator.dto.MongoDTO;
import ae.smartek.cosmosmigrator.dto.kafkaDTO;

/**
 *
 * @author houssem
 */
public class MigrationThread extends Thread {

    private final String databse;
    private final String collection;
    private boolean isKafkaRunning;

    public MigrationThread(String databse, String collection) {
        this.databse = databse;
        this.collection = collection;
        this.isKafkaRunning = true;
    }

    public MigrationThread(String databse, String collection, boolean isKafkaRunning) {
        this.databse = databse;
        this.collection = collection;
        this.isKafkaRunning = isKafkaRunning;
    }
   

  

    public void run() {
        MongoDTO mdto = new MongoDTO();
        kafkaDTO kdto = new kafkaDTO();
        
        mdto.createChangeStreamOnColl(this.databse, this.collection).forEach((doc) -> {
            if (this.isKafkaRunning == false) {
                System.out.println("namespace: " + this.databse + "." + this.collection + "\n" + "Doc: " + doc.getFullDocument());
            } else {
                String docId = doc.getDocumentKey().getObjectId("_id").getValue().toHexString();
                System.out.println("namespace: " + this.databse + "." + this.collection + "  " + "DocId: " + docId);
                kdto.sendDocument(this.databse.concat(".").concat(this.collection),doc.getNamespaceDocument().toJson() ,doc.getFullDocument().toJson());
            }
        });
    }
}
