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
            System.out.println("file exist");
            //read token from file 
            BufferedReader br;
            String token = null;
            try {
                br = new BufferedReader(new FileReader(tokenFile));
                token = br.readLine();
                System.out.println("this is the old token:" + token);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            // call createChangeStreamOnCollWithToken  
            mdto.createChangeStreamOnCollWithToken(this.databse, this.collection, token).forEach((doc) -> {
                // add the token in the file here
                String newToken = doc.getResumeToken().toString();
                System.out.println("this is the new token:" + newToken);
                PrintWriter prw;
                try {
                    System.out.println("write new token");
                    prw = new PrintWriter(filePath);
                    prw.println(newToken);
                    prw.close();
                    System.out.println("token updated");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MigrationThread.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (this.isKafkaRunning == false) {
                    System.out.println("namespace: " + this.databse + "." + this.collection + "\n" + "Doc: " + doc.getFullDocument());
                } else {

                    String docId = doc.getDocumentKey().getObjectId("_id").getValue().toHexString();
                    System.out.println("namespace: " + this.databse + "." + this.collection + "  " + "DocId: " + docId);
                    kdto.sendDocument(this.databse.concat(".").concat(this.collection), doc.getNamespaceDocument().toJson(), doc.getFullDocument().toJson());
                }
            });
        } else {
            System.out.println("in else");
            //call createChangeStreamOnColl
            mdto.createChangeStreamOnColl(this.databse, this.collection).forEach((doc) -> {
                File file = new File("/tmp/cosmosMigrator/"+this.databse + "." + this.collection + ".txt"); 
//add the token in the file here
              String newToken = doc.getResumeToken().toString();
                System.out.println("new token in else is " + newToken);
                PrintWriter prw;
                try {
                    System.out.println("create new file");
                   
                   // boolean created = file.createNewFile();
                    //if (created) {
                        System.out.println("file created");
                        FileOutputStream outputStream = new FileOutputStream(filePath);
                        byte[] strToBytes = newToken.getBytes();
                        outputStream.write(strToBytes);
                        outputStream.close();
                    //} else {
                   //     System.out.println("file is not created");
                    //}
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
                    kdto.sendDocument(this.databse.concat(".").concat(this.collection), doc.getNamespaceDocument().toJson(), doc.getFullDocument().toJson());
                }
            });

        }

    }
}

