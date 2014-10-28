package Converter.ModelController.Controller.DB.DocumentDB.Mongo;

import Converter.ModelController.DocumentTypesDB;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;


public class MongoDBConnector {

    private static final MongoDBConnector ourInstance = new MongoDBConnector();

    private static MongoClient mongoClient;

    private MongoDBConnector() {}


    public static MongoDBConnector getInstance() {

        return ourInstance;
    }

    public MongoClient getMongoClient(DocumentTypesDB noSql){
        if(mongoClient == null){
            try {
                mongoClient = new MongoClient(noSql.getHost(),noSql.getDefPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return mongoClient;
    }

    public DB getDB(String DbName) throws UnknownHostException {
        mongoClient = new MongoClient();
        return mongoClient.getDB(DbName);
    }
}
