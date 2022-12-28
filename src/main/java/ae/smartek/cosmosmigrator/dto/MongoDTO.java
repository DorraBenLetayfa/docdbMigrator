/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.cosmosmigrator.dto;

import ae.smartek.cosmosmigrator.config.MongoConfig;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import com.mongodb.client.model.changestream.FullDocument;
import java.util.Arrays;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author houssem
 */
public class MongoDTO {

    public MongoIterable<String> getAllDatabases() {
        return MongoConfig.getMongoClientInstance().listDatabaseNames();
    }

    public MongoIterable<String> getAllCollectionsInDB(String db) {
        return MongoConfig.getMongoClientInstance().getDatabase(db).listCollectionNames();
    }

    public ChangeStreamIterable<Document> createChangeStreamOnColl(String db, String coll) {
        
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(
                        Filters.in("operationType",
                                Arrays.asList("insert", "update", "replace"))),
                Aggregates.project(fields(include("_id", "ns", "documentKey", "fullDocument"))));
                return MongoConfig.getMongoClientInstance().getDatabase(db).getCollection(coll).watch(pipeline).fullDocument(FullDocument.UPDATE_LOOKUP);
    }
    
     public ChangeStreamIterable<Document> createChangeStreamOnCollWithToken(String db, String coll, String token) {
BsonDocument documentToken = BsonDocument.parse(token);
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(
                        Filters.in("operationType",
                                Arrays.asList("insert", "update", "replace"))),
                Aggregates.project(fields(include("_id", "ns", "documentKey", "fullDocument"))));
ChangeStreamIterable<Document> res = MongoConfig.getMongoClientInstance().getDatabase(db).getCollection(coll)
                        .watch(pipeline).fullDocument(FullDocument.UPDATE_LOOKUP).resumeAfter(documentToken);
       //  MongoConfig.getMongoClientInstance().getDatabase(db).getCollection(coll).watch().cursor().close();
        return res;   
    }

}
