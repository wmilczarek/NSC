package Converter.ModelController.Controller.DB.DocumentDB.Mongo;

import Converter.ViewModel.DocumentTypesDB;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;


public class MongoConnector  {

    private static final MongoConnector ourInstance = new MongoConnector();

    private static MongoClient mongoClient;

    private MongoConnector() {}


    public static MongoConnector getInstance() {

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
