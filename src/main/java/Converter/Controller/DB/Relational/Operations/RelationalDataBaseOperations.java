package Converter.Controller.DB.Relational.Operations;
import Converter.ViewModel.MongoDataBase;
import Converter.ViewModel.MongoEntity;
import Converter.ViewModel.MongoField;
import Converter.ViewModel.Relations;

import java.sql.*;

/**
 * Created by szef on 2014-05-15.
 */
public class RelationalDataBaseOperations {

    private static Connection connection;

    private RelationalDataBaseOperations() { }

    public static void createDataBase(String name)
    {
        try {
            connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3306/?user=root&password=root");

        Statement s=connection.createStatement();
        int Result=s.executeUpdate("CREATE DATABASE " + name);


            s.close();
        } catch (SQLException e) {


        }
    }

    public static void createEntitiesFromMongo(MongoDataBase mongoDataBase) throws SQLException {


        StringBuilder entityQuery = new StringBuilder();

        //create Entities for X Entities get Y FIeld and assembly SQL Queary
        for(MongoEntity entity:mongoDataBase.getEntities()){

            entityQuery.append("CREATE TABLE " + entity.getEntityName() + " ( ");
            for(MongoField field:entity.getEntityFields().keySet()) {


                //
                entityQuery.append(field.getFieldName() + " " + entity.getEntityFields().get(field).toString() + " ,");

                if (field.getRelations() == Relations.PrimaryKey) {

                    entityQuery.append(Relations.PrimaryKey.toString() + "(" + field.getFieldName() + "), ");

                } else if (field.getRelations() == Relations.ForeginKey) {

                    entityQuery.append(Relations.ForeginKey.toString() + "(" + field.getFieldName()
                            + "), REFERENCES " + field.getRelations().getInRelationEntity()
                            + "("+ field.getRelations().getInRealtaionField() +"),");
                }
            }

            entityQuery.deleteCharAt(entityQuery.length()-1);
            entityQuery.append(" )");

            Statement s=connection.createStatement();

            s.executeUpdate(entityQuery.toString());
        }

        //create Relations

    }


}
