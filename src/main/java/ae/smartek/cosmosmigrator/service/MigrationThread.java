/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.cosmosmigrator.service;

import ae.smartek.cosmosmigrator.dto.MongoDTO;
import ae.smartek.cosmosmigrator.dto.kafkaDTO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        //String filePath = "/home/houssem/cosmos/tokens/" + this.databse + "." + this.collection + ".txt";
        String filePath = "/tmp/cosmosMigrator/"+this.databse + "." + this.collection + ".txt";
        //check if the file exists or not
        File tokenFile = new File(filePath);
        boolean exists = tokenFile.exists();
        if (exists) {
            //read token from file 
            BufferedReader br;
            String token = null;
            try {
                br = new BufferedReader(new FileReader(tokenFile));
                token = br.readLine();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            mdto.createChangeStreamOnCollWithToken(this.databse, this.collection, token).forEach((doc) -> {
                String newToken = doc.getResumeToken().toString();
                PrintWriter prw;
                try {
                    
                    prw = new PrintWriter(filePath);
                    prw.println(newToken);
                    prw.close();
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (this.isKafkaRunning == false) {
			
                    System.out.println("namespace: " + this.databse + "." + this.collection + "\n" + "Doc: " + doc.getFullDocument());
                } else {
                     
                    String docId = doc.getDocumentKey().getObjectId("_id").getValue().toHexString();
                    System.out.println("namespace: " + this.databse + "." + this.collection + "  " + "DocId: " + docId);
                    System.out.println("token is: " + newToken);

		   kdto.sendDocument(this.databse.concat(".").concat(this.collection), doc.getNamespaceDocument().toJson(), doc.getFullDocument().toJson());
                }
            });
        } else {
            mdto.createChangeStreamOnColl(this.databse, this.collection).forEach((doc) -> {
                File file = new File("/tmp/cosmosMigrator/"+this.databse + "." + this.collection + ".txt"); 
              String newToken = doc.getResumeToken().toString();
                PrintWriter prw;
                try {                                                                                                
                        FileOutputStream outputStream = new FileOutputStream(filePath);
                        byte[] strToBytes = newToken.getBytes();
                        outputStream.write(strToBytes);
                        outputStream.close();
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (this.isKafkaRunning == false) {
                    System.out.println("namespace: " + this.databse + "." + this.collection + "\n" + "Doc: " + doc.getFullDocument());
                } else {

                    String docId = doc.getDocumentKey().getObjectId("_id").getValue().toHexString();
                    System.out.println("namespace: " + this.databse + "." + this.collection + "  " + "DocId: " + docId);
                    System.out.println("token is: " + newToken);
		    kdto.sendDocument(this.databse.concat(".").concat(this.collection), doc.getNamespaceDocument().toJson(), doc.getFullDocument().toJson());
                }
            });

        }

    }
}

