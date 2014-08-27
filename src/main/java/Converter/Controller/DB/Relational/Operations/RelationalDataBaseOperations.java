package Converter.Controller.DB.Relational.Operations;

import Converter.ViewModel.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by szef on 2014-05-15.
 */
public class RelationalDataBaseOperations {

    private static Connection connection;


    private RelationalDataBaseOperations() {
    }

    public static void createDataBase(String name) {
        try {
            connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/?user=root&password=root");

            Statement s = connection.createStatement();
            int Result = s.executeUpdate("CREATE DATABASE " + name);


            s.close();
        } catch (SQLException e) {


        }
    }

    public static void createEntitiesSchemaFromMongo(MongoDataBase mongoDataBase) throws SQLException {


        StringBuilder entityQuery = new StringBuilder("use test;");

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (MongoEntitySchema entity : mongoDataBase.getEntitiesSchema()) {

            entityQuery.append("CREATE TABLE " + entity.getEntityName() + " ( ");
            for (MongoFieldSchema field : entity.getEntityFields()) {


                //
                entityQuery.append(field.getFieldName() + " " + field.getSqlType().toString() + " ,");

                if (field.getRelations() == Relations.PrimaryKey) {

                    entityQuery.append(Relations.PrimaryKey.toString() + "(" + field.getFieldName() + "), ");

                } /*else if (field.getRelations() == Relations.ForeginKey) {

                    entityQuery.append(Relations.ForeginKey.toString() + "(" + field.getFieldName()
                            + "), REFERENCES " + field.getRelations().getInRelationEntity()
                            + "(" + field.getRelations().getInRealtaionField() + "),");
                }*/
            }

            entityQuery.deleteCharAt(entityQuery.length() - 1);
            entityQuery.append(" ) \n");

            //Statement s = connection.createStatement();

            //s.executeUpdate(entityQuery.toString());
        }

        wrtiteToSqlFile("schema.sql", entityQuery.toString());

    }


    public static void createSQLInsert(MongoDataBase mongoDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (MongoEntityData entity : mongoDataBase.getEntitiesData()) {


            for (MongoRowData row : entity.getMongoFieldData()) {

                entityQuery.append("INSERET INTO " + entity.getEntityName() + "( ");
                StringBuilder fieldId = new StringBuilder();
                StringBuilder fieldValue = new StringBuilder("VALUES (");

                for (Map.Entry entry : row.getFieldValue().entrySet()) {

                    fieldId.append(entry.getKey() + ",");
                    fieldValue.append("'" + entry.getValue().toString() + "',");
                }

                fieldId.deleteCharAt(fieldId.length() - 1);
                fieldValue.deleteCharAt(fieldValue.length() - 1);
                fieldId.append(") ");
                fieldValue.append(");");
                entityQuery.append(fieldId.toString() + fieldValue.toString() + "\n");
            }

            //Statement s = connection.createStatement();

            //s.executeUpdate(entityQuery.toString());
        }

        wrtiteToSqlFile("data.sql", entityQuery.toString());

    }


    public static void createSQLReferences(MongoDataBase mongoDataBase) {

        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for (MongoEntitySchema entity : mongoDataBase.getEntitiesSchema()) {

            for (MongoFieldSchema field : entity.getEntityFields()) {

                if (field.getRelations() == Relations.ForeginKey) {

                    entityQuery.append("ALTER TABLE " + field.getRelations().getInRelationEntity() + "ADD CONSTRAINT _id FOREGIN KEY('_id')( REFERENCES '" + entity.getEntityName() + "'\n");

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
