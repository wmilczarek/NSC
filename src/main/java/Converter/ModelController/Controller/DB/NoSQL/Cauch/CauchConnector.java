package Converter.ModelController.Controller.DB.NoSQL.Cauch;

import Converter.ViewModel.NoSQLTypes;
import com.couchbase.client.ClusterManager;
import com.couchbase.client.CouchbaseClient;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CauchConnector {

    private static final CauchConnector ourInstance = new CauchConnector();

    private static CouchbaseClient mongoClient;

  //  private static DB MongoDB;




    public static CauchConnector getInstance() {

        return ourInstance;
    }

    public static CouchDbClient getCauchClient(NoSQLTypes noSql) throws URISyntaxException, IOException {

        CouchDbClient dbClient = new CouchDbClient("test", true, "http", "127.0.0.1", 5984, "admin", "admin");
        return dbClient;
    }

}
