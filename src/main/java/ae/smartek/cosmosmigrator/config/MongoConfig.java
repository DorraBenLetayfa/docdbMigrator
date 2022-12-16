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
                mongo_uri = "mongodb://cosmosformongo:2TeAu8s1vUcXOP8qxhS1xNYW1xWb1JV20BXeYsR5cu0XYLA1cYtgfWMtWuZhe8Y4h57hYwEIQNlKACDbGstqjQ==@cosmosformongo.mongo.cosmos.azure.com:10255/?ssl=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@cosmosformongo@";
            }
            ConnectionString connString = new ConnectionString(mongo_uri);
            MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString).build();

            MONGOCLIENT = MongoClients.create(settings);
        }

        return MONGOCLIENT;
    }

}

