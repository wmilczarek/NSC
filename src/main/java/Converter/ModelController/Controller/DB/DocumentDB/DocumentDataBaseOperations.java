package Converter.ModelController.Controller.DB.DocumentDB;

import Converter.ConverterMetaDataModels.MongoModel.TranslationDataBase;
import Converter.ModelController.Controller.DB.Relational.Operations.SqlPrintOperations;
import Converter.ViewModel.DocumentTypesDB;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

public abstract class DocumentDataBaseOperations {

    protected TranslationDataBase dataBase;

    private DocumentDataBaseOperations InstancoOf;

    protected SqlPrintOperations printSql = SqlPrintOperations.getInstance();

    public DocumentTypesDB getNoSqlType() {
        return null;
    }

    public DocumentDataBaseOperations getInstancoOf() {
        return InstancoOf;
    }

    public List<String> GetDataBaseNames() {

        return null;
    }

    public abstract List<String> loadDataBase(String dbName) throws UnknownHostException, SQLException;

    //public abstract List<String> ResolveAndGetFields(String dbName, String EntityName) throws UnknownHostException;

    protected void printMetaDataToSQL() throws SQLException {
        printSql.createEntitiesSchemaScript(dataBase);
        printSql.createSQLReferencesScript(dataBase);
        printSql.createSQLInsertScript(dataBase);
    }

    // dictionary - Filed Name, All Types
    public abstract List<List<String>> showFields(String dbName, String EntityName) throws UnknownHostException;

    public abstract void loadIntoMemory(String dbName);


}
