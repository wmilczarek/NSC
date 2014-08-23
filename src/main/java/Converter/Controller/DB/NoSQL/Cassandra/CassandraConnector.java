package Converter.Controller.DB.NoSQL.Cassandra;

import Converter.ViewModel.NoSQLTypes;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Created by szef on 2014-07-26.
 */
public class CassandraConnector {

    private static final CassandraConnector ourInstance = new CassandraConnector();

    private static Cluster cassandraClient;

    private static Session cassandraSession;

    private CassandraConnector() {}


    public static CassandraConnector getInstance() {

        return ourInstance;
    }

    public static Cluster getClient(NoSQLTypes noSql){

        if(cassandraClient == null){

                cassandraClient = Cluster.builder().addContactPoint("127.0.0.1").build();

        }

        return cassandraClient;
    }

    public Session getDB(String sessionName)  {
        cassandraSession = cassandraClient.connect(sessionName);
        return cassandraSession;
    }
}
