/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.cosmosmigrator.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 *
 * @author houssem
 */
public class MongoConfig {

    private static MongoClient MONGOCLIENT;

    public static MongoClient getMongoClientInstance() {
        if (MONGOCLIENT == null) {
            String mongo_uri = System.getenv("MONGODB_URI");
            if ((mongo_uri == null) || (mongo_uri.equals(""))) {
                mongo_uri = "mongodb://dorra:dorra123@docdb-2022-12-21-18-59-02.cluster-c2jdrdp7aouo.eu-west-1.docdb.amazonaws.com:27017/?"
                        + "ssl=true&ssl_ca_certs=rds-combined-ca-bundle.pem&replicaSet=rs0"
                        + "&retryWrites=false";         
            }
            ConnectionString connString = new ConnectionString(mongo_uri);
            MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString).build();

            MONGOCLIENT = MongoClients.create(settings);
        }
 
        return MONGOCLIENT;
    }

}
 
 
