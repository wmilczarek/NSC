package Converter.ViewModel;

import Converter.ModelController.Controller.DB.DocumentDB.Cauch.CauchDataBaseOperations;
import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;
import Converter.ModelController.Controller.DB.DocumentDB.Mongo.MongoDataBaseOperations;

/**
 * Created by szef on 2014-05-15.
 */
public enum NoSQLTypes {

    MongoDB("MongoDB", "27017", MongoDataBaseOperations.getInstance()),
    Cauch("Cauch", "8091", CauchDataBaseOperations.getInstance());

    public String name;
    public String defPort;
    public DocumentDataBaseOperations operations;

    private NoSQLTypes(String name, String defPort, DocumentDataBaseOperations operations) {
        this.name = name;
        this.defPort = defPort;
        this.operations = operations;
    }

}
