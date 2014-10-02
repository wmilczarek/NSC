package Converter.ModelController.Controller.DB.DocumentDB;

import Converter.ViewModel.NoSQLTypes;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by szef on 2014-06-07.
 */
public abstract class DocumentDataBaseOperations {

    public NoSQLTypes getNoSqlType(){
        return null;
    }

    private DocumentDataBaseOperations InstancoOf;

    private Connector connector;

    public DocumentDataBaseOperations getInstancoOf() {
        return InstancoOf;
    }

    public Connector getConnector() {
        return connector;
    }

    public  void setConnector(Connector connector) {
        this.connector = connector;
    }

    public List<String> GetDataBaseNames(){

        return null;
    }

    public abstract List<String> loadDataBase(String dbName) throws UnknownHostException, SQLException;

    //public abstract List<String> ResolveAndGetFields(String dbName, String EntityName) throws UnknownHostException;

    // dictionary - Filed Name, All Types
    public abstract List<List<String>> showFields(String dbName, String EntityName) throws UnknownHostException;

    public abstract void loadIntoMemory(String dbName);



}
