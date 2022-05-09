package ortiz.burgos.marc.m06json.controllers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @author christopher
 */
public class MongoConnection {
    
    private MongoClientURI connectionString;
    private MongoClient mongoClient;
    
    public MongoConnection (String URI) {
        connectionString = new MongoClientURI(URI);
        mongoClient = new MongoClient(connectionString);
    }

    public MongoClientURI getConnectionString() {
        return connectionString;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
    
    public MongoDatabase getMongoDatabase(String bdName) {
        return mongoClient.getDatabase(bdName);
    }
}
