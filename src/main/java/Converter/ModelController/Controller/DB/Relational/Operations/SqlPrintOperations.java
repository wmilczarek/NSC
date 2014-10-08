package Converter.ModelController.Controller.DB.Relational.Operations;

import Converter.ModelController.Controller.DB.Translator.DocumentDBToSQL;
import Converter.ConverterMetaDataModels.MongoModel.*;
import Converter.ModelController.Relations;
import Converter.ModelController.SqlFieldType;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;


public class SqlPrintOperations {

    private static Connection connection;

    public static SqlPrintOperations getOurInstance() {
        return ourInstance;
    }

    private static SqlPrintOperations ourInstance = new SqlPrintOperations();

    private Properties fileProperties;

    public static SqlPrintOperations getInstance() {
        return ourInstance;
    }

    private static Logger log = Logger.getLogger(SqlPrintOperations.class.getName());


    private SqlPrintOperations() {

        fileProperties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sql.properties");

    }

    public void createEntitiesSchemaScript(TranslationDataBase DataBase) throws SQLException {


        StringBuilder entityQuery = new StringBuilder("CREATE DATABASE " + DataBase.getDBname() + ";\n");

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (TranslationEntitySchema entity : DataBase.getEntitiesSchema()) {

            entityQuery.append("CREATE TABLE " + entity.getMetaDataObjectName() + " ( ");
            for (TranslationFieldSchema field : entity.getTranslationMetaDataFieldsSchema()) {
                
                if(field.getSqlType() == SqlFieldType.NotRelationalField){
                    log.debug(field.getSqlType().toString());
                    continue;
                }
                log.debug(field.getMetaDataObjectName() + " " + field.getSqlType().toString());
                entityQuery.append( field.getMetaDataObjectName() + " " + field.getSqlType().toString() + " ,");

                if (field.getRelationProperties().getRelations() == Relations.PrimaryKey) {

                    entityQuery.append(Relations.PrimaryKey.toString() + "(" + field.getMetaDataObjectName() + "), ");
                }
            }

            entityQuery.deleteCharAt(entityQuery.length() - 1);
            entityQuery.append(" ); \n");
        }
        log.debug("end creating schema script");
        wrtiteToSqlFile(fileProperties.getProperty("schemaOutDir"), entityQuery.toString());

    }


    public void createSQLInsertScript(TranslationDataBase documentDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (TranslationEntitySchema entity : documentDataBase.getEntitiesSchema()) {


            for (DocumentRowMetaData row : entity.getTranslationMetaDataDocuments()) {

                entityQuery.append("INSERT INTO " + entity.getMetaDataObjectName() + "( ");
                StringBuilder fieldId = new StringBuilder();
                StringBuilder fieldValue = new StringBuilder(" VALUES (");

                for (Map.Entry entry : row.getFieldValue().entrySet()) {

                    log.debug(entry.getKey() + " " + DocumentDBToSQL.MongoToSqlValueConverter(entry.getValue()));
                    fieldId.append(entry.getKey() + ",");
                    fieldValue.append( DocumentDBToSQL.MongoToSqlValueConverter(entry.getValue()) + ",");
                }

                fieldId.deleteCharAt(fieldId.length() - 1);
                fieldValue.deleteCharAt(fieldValue.length() - 1);
                fieldId.append(") ");
                fieldValue.append(");");
                entityQuery.append(fieldId.toString() + fieldValue.toString() + "\n");
            }

        }
        log.debug("End insert script generation");
        wrtiteToSqlFile(fileProperties.getProperty("dataOutDir"), entityQuery.toString());

    }


    public void createSQLReferencesScript(TranslationDataBase documDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        for (TranslationEntitySchema entity : documDataBase.getEntitiesSchema()) {

            for (TranslationFieldSchema field: entity.getTranslationMetaDataFieldsSchema()) {

                if (field.getRelationProperties().getRelations() == Relations.ForeginKey) {
                    log.debug(entity.getMetaDataObjectName() + "set reference to table " + field.getMetaDataObjectName());
                    entityQuery.append("ALTER TABLE " + entity.getMetaDataObjectName() + " ADD FOREIGN KEY(" + field.getMetaDataObjectName().replace("_id", "") + "_id) REFERENCES " + field.getMetaDataObjectName().replace("_id", "") + "(" + field.getMetaDataObjectName().replace("_id", "") + "_PK); \n");
                }
            }
        }
        log.debug("End reference script generation");
        wrtiteToSqlFile(fileProperties.getProperty("relationOutDir"), entityQuery.toString());
    }


    private void wrtiteToSqlFile(String filename, String quary) {

        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "utf-8"));
            writer.write(quary);
        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
    }
}
