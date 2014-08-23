package Converter.ViewModel;

import Converter.Controller.DB.NoSQL.Mongo.MongoDataBaseOperations;
import Converter.Controller.DB.NoSQL.NoSQLDataBaseOperations;

/**
 * Created by szef on 2014-05-15.
 */
public enum NoSQLTypes {

    MongoDB("MongoDB", "27017", MongoDataBaseOperations.getInstance()),
    Cassandra("Cassandra", "7199", null),
    Neo4J("Neo4J", "0000", null);

    public String name;
    public String defPort;
    public NoSQLDataBaseOperations operations;

    private NoSQLTypes(String name, String defPort, NoSQLDataBaseOperations operations) {
        this.name = name;
        this.defPort = defPort;
        this.operations = operations;
    }

}
