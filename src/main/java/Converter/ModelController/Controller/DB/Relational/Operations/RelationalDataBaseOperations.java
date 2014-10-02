package Converter.ModelController.Controller.DB.Relational.Operations;

import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;
import Converter.ModelController.Controller.DB.Translator.DocumentDBToSQL;
import Converter.ConverterMetaDataModels.BaseModel.DocumentDataBase;
import Converter.ConverterMetaDataModels.MongoModel.*;
import Converter.ModelController.Relations;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataField;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataObject;
import Converter.ModelController.SqlFieldType;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;


public class RelationalDataBaseOperations {

    private static Connection connection;

    private static Logger log = Logger.getLogger(RelationalDataBaseOperations.class.getName());


    private RelationalDataBaseOperations() {
    }

    public static void createDataBase(String name) {
        try {
            connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/?user=root&password=root");

            Statement s = connection.createStatement();
            int Result = s.executeUpdate("CREATE DATABASE '" + name + "';");

            s.close();
        } catch (SQLException e) {


        }
    }

    public static void createEntitiesSchemaScript(DocumentDataBase DataBase) throws SQLException {


        StringBuilder entityQuery = new StringBuilder("CREATE DATABASE '" + DataBase.getDBname() + "';\n");

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (TranslationMetaDataEntity entity : (Set<TranslationMetaDataEntity>)DataBase.getEntitiesSchema()) {

            entityQuery.append("CREATE TABLE " + entity.getMetaDataObjectName() + " ( ");
            for (TranslationMetaDataField field : (Set<TranslationMetaDataField>) entity.getTranslationMetaDataFieldsSchema()) {
                
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
        wrtiteToSqlFile("schema.sql", entityQuery.toString());

    }


    public static void createSQLInsertScript(DocumentDataBase documentDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (TranslationMetaDataEntity entity :(Set<TranslationMetaDataEntity>) documentDataBase.getEntitiesSchema()) {


            for (DocumentRowMetaData row : entity.getTranslationMetaDataDocuments()) {

                entityQuery.append("INSERT INTO " + entity.getMetaDataObjectName() + "( ");
                StringBuilder fieldId = new StringBuilder();
                StringBuilder fieldValue = new StringBuilder(" VALUES (");

                for (Map.Entry entry : row.getFieldValue().entrySet()) {

                    log.debug(entry.getKey() + " " + DocumentDBToSQL.MongoToSqlValueConverter(entry.getValue()));
                    fieldId.append(entry.getKey() + ",");
                    fieldValue.append("'" + DocumentDBToSQL.MongoToSqlValueConverter(entry.getValue()) + "',");
                }

                fieldId.deleteCharAt(fieldId.length() - 1);
                fieldValue.deleteCharAt(fieldValue.length() - 1);
                fieldId.append(") ");
                fieldValue.append(");");
                entityQuery.append(fieldId.toString() + fieldValue.toString() + "\n");
            }

        }
        log.debug("End insert script generation");
        wrtiteToSqlFile("data.sql", entityQuery.toString());

    }


    public static void createSQLReferencesScript(DocumentDataBase documDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (TranslationMetaDataEntity entity :(Set<TranslationMetaDataEntity>) documDataBase.getEntitiesSchema()) {

            for (TranslationMetaDataField field: (Set<TranslationMetaDataField>) entity.getTranslationMetaDataFieldsSchema()) {

                if (field.getRelationProperties().getRelations() == Relations.ForeginKey) {
                    log.debug(entity.getMetaDataObjectName() + "set reference to table " + field.getMetaDataObjectName());
                    entityQuery.append("ALTER TABLE " + entity.getMetaDataObjectName() + " ADD FOREIGN KEY(" + field.getMetaDataObjectName().replace("_id", "") + "_id) REFERENCES " + field.getMetaDataObjectName().replace("_id", "") + "(" + field.getMetaDataObjectName().replace("_id", "") + "_PK); \n");
                }
            }
        }
        log.debug("End reference script generation");
        wrtiteToSqlFile("relation.sql", entityQuery.toString());
    }


    private static void wrtiteToSqlFile(String filename, String quary) {

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
