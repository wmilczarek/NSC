package Converter.ModelController.Controller.DB.DocumentDB.Cauch;

import Converter.ViewModel.DocumentTypesDB;
import com.couchbase.client.CouchbaseClient;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;


public class CouchDBConnector {

    private static final CouchDBConnector ourInstance = new CouchDBConnector();

    private static CouchbaseClient mongoClient;

  //  private static DB MongoDB;




    public static CouchDBConnector getInstance() {

        return ourInstance;
    }

    public CouchDbClient getCauchClient(DocumentTypesDB documentTypsDB, String bucketName) throws URISyntaxException, IOException {
        Properties prop = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mongodb.properties");

        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CouchDbClient dbClient = new CouchDbClient(bucketName, true, "http", documentTypsDB.getHost(), documentTypsDB.getDefPort(), prop.getProperty("username"), prop.getProperty("password"));
        return dbClient;
    }

}
