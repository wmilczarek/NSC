package Converter.ModelController.Controller.DB.DocumentDB.Cauch;

import Converter.ModelController.DocumentTypesDB;
import com.couchbase.client.CouchbaseClient;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;


public class CouchDBConnector {

    private static final CouchDBConnector ourInstance = new CouchDBConnector();

    private static CouchbaseClient mongoClient;

    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("couchdb.properties");
    Properties fileProperties = new Properties();


    public CouchDBConnector() {

        {
            try {
                fileProperties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static CouchDBConnector getInstance() {

        return ourInstance;
    }

    public CouchDbClient getCauchClient(DocumentTypesDB documentTypsDB, String bucketName) throws URISyntaxException, IOException {

        CouchDbClient dbClient = new CouchDbClient(bucketName, true, "http", documentTypsDB.getHost(), documentTypsDB.getDefPort(), fileProperties.getProperty("username"), fileProperties.getProperty("password"));
        return dbClient;
    }

}
