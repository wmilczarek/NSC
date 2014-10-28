package Converter.ModelController;

import Converter.ModelController.Controller.DB.DocumentDB.Cauch.CouchDataBaseOperations;
import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;
import Converter.ModelController.Controller.DB.DocumentDB.Mongo.MongoDataBaseOperations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public enum DocumentTypesDB {

    MongoDB("MongoDB", "mongodb.properties", MongoDataBaseOperations.getInstance()),
    CouchDB("CouchDB", "couchdb.properties", CouchDataBaseOperations.getInstance()), ;

    private String address;
    private String name;
    private int defPort;
    public DocumentDataBaseOperations operations;

    private DocumentTypesDB(String name, String propFile, DocumentDataBaseOperations operations) {

        Properties prop = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.name = name;
        this.address = prop.getProperty("host");
        this.defPort = Integer.valueOf(prop.getProperty("port"));
        this.operations = operations;
    }

    public DocumentDataBaseOperations getOperations() {
        return operations;
    }

    public int getDefPort() {
        return defPort;
    }

    public void setDefPort(int defPort) {
        this.defPort = defPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
