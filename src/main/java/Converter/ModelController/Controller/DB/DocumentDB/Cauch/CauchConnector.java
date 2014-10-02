package Converter.ModelController.Controller.DB.DocumentDB.Cauch;

import Converter.ViewModel.NoSQLTypes;
import com.couchbase.client.CouchbaseClient;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.net.URISyntaxException;


public class CauchConnector {

    private static final CauchConnector ourInstance = new CauchConnector();

    private static CouchbaseClient mongoClient;

  //  private static DB MongoDB;




    public static CauchConnector getInstance() {

        return ourInstance;
    }

    public static CouchDbClient getCauchClient(NoSQLTypes noSql) throws URISyntaxException, IOException {

        CouchDbClient dbClient = new CouchDbClient("objarray", true, "http", "127.0.0.1", 5984, "admin", "admin");
        return dbClient;
    }

}
