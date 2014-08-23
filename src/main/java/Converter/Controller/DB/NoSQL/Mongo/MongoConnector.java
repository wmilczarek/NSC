package Converter.Controller.DB.NoSQL.Mongo;

import Converter.Controller.DB.NoSQL.Connector;
import Converter.ViewModel.NoSQLTypes;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;


public class MongoConnector  {

    private static final MongoConnector ourInstance = new MongoConnector();

    private static MongoClient mongoClient;

    private static DB MongoDB;

    private MongoConnector() {}


    public static MongoConnector getInstance() {

        return ourInstance;
    }

    public static MongoClient getMongoClient(NoSQLTypes noSql){
        if(mongoClient == null){
            try {
                mongoClient = new MongoClient("localhost",Integer.parseInt(noSql.defPort) );
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
