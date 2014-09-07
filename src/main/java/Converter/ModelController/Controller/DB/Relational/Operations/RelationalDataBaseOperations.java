package Converter.ModelController.Controller.DB.Relational.Operations;

import Converter.ModelController.Controller.DB.Translator.MongoToSQL;
import Converter.ModelController.*;
import Converter.ModelController.MongoModel.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;


public class RelationalDataBaseOperations {

    private static Connection connection;


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

    public static void createEntitiesSchemaFromMongo(MongoDataBase mongoDataBase) throws SQLException {


        StringBuilder entityQuery = new StringBuilder("CREATE DATABASE '" + mongoDataBase.getDBname() + "';");

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (MongoEntitySchema entity : mongoDataBase.getEntitiesSchema()) {

            entityQuery.append("CREATE TABLE " + entity.getEntityName() + " ( ");
            for (MongoFieldSchema field : entity.getEntityFields()) {


                        entityQuery.append(field.getFieldName() + " " + field.getSqlType().toString() + " ,");

                if (field.getRelationProperties().getRelations() == Relations.PrimaryKey) {

                    entityQuery.append(Relations.PrimaryKey.toString() + "(" + field.getFieldName() + "), ");
                }
            }

            entityQuery.deleteCharAt(entityQuery.length() - 1);
            entityQuery.append(" ); \n");
        }

        wrtiteToSqlFile("schema.sql", entityQuery.toString());

    }


    public static void createSQLInsert(MongoDataBase mongoDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (MongoEntityData entity : mongoDataBase.getEntitiesData()) {


            for (MongoRowData row : entity.getMongoFieldData()) {

                entityQuery.append("INSERT INTO " + entity.getEntityName() + "( ");
                StringBuilder fieldId = new StringBuilder();
                StringBuilder fieldValue = new StringBuilder("VALUES (");

                for (Map.Entry entry : row.getFieldValue().entrySet()) {

                    fieldId.append(entry.getKey() + ",");
                    fieldValue.append("'" + MongoToSQL.MongoToSqlValueConverter(entry.getValue()) + "',");
                }

                fieldId.deleteCharAt(fieldId.length() - 1);
                fieldValue.deleteCharAt(fieldValue.length() - 1);
                fieldId.append(") ");
                fieldValue.append(");");
                entityQuery.append(fieldId.toString() + fieldValue.toString() + "\n");
            }

        }

        wrtiteToSqlFile("data.sql", entityQuery.toString());

    }


    public static void createSQLReferences(MongoDataBase mongoDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (MongoEntitySchema entity : mongoDataBase.getEntitiesSchema()) {

            for (MongoFieldSchema field : entity.getEntityFields()) {

                if (field.getRelationProperties().getRelations() == Relations.ForeginKey) {

                    entityQuery.append("ALTER TABLE " + entity.getEntityName() + " ADD FOREIGN KEY(" + field.getFieldName().replace("_id", "") + "_id) REFERENCES " + field.getFieldName().replace("_id", "") + "(" + field.getFieldName().replace("_id", "") + "_PK); \n");
                    //ALTER TABLE A ADD CONSTRAINT _id  FOREIGN KEY(publisher_id)  REFERENCES publisher (_id)
                    //"ALTER TABLE `table1` ADD CONSTRAINT table1_id_refs FOREIGN KEY (`table2_id`) REFERENCES `table2` (`id`);"
                }
            }
        }

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
