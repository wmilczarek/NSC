package Converter.ViewModel;

import Converter.ModelController.Controller.DB.NoSQL.Cauch.CauchConnector;
import Converter.ModelController.Controller.DB.NoSQL.Cauch.CauchDataBaseOperations;
import Converter.ModelController.Controller.DB.NoSQL.Mongo.MongoDataBaseOperations;
import Converter.ModelController.Controller.DB.NoSQL.NoSQLDataBaseOperations;

/**
 * Created by szef on 2014-05-15.
 */
public enum NoSQLTypes {

    MongoDB("MongoDB", "27017", MongoDataBaseOperations.getInstance()),
    Cauch("Cauch", "8091", CauchDataBaseOperations.getInstance());

    public String name;
    public String defPort;
    public NoSQLDataBaseOperations operations;

    private NoSQLTypes(String name, String defPort, NoSQLDataBaseOperations operations) {
        this.name = name;
        this.defPort = defPort;
        this.operations = operations;
    }

}
