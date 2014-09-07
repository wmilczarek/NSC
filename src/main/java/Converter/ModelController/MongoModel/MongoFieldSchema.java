package Converter.ModelController.MongoModel;

import Converter.ModelController.HelperTypes.LongString;
import Converter.ModelController.HelperTypes.ShortString;
import Converter.ModelController.RelationProperties;
import Converter.ModelController.SqlFieldType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by szef on 2014-08-14.
 */
public class MongoFieldSchema {

    private Set<Class<?>> typesFromMongoApi = new HashSet<Class<?>>();
    private Object value;
    private RelationProperties relations = new RelationProperties();
    private String fieldName;
    private SqlFieldType sqlType;

    public MongoFieldSchema(String name) {
        this.fieldName = name;
    }

    public MongoFieldSchema() {

    }

    public Set<Class<?>> getTypesFromMongoApi() {
        return typesFromMongoApi;
    }

    public void setTypesFromMongoApi(Object typesFromMongoApi) {

        if (typesFromMongoApi.getClass() == String.class) {
            String stringType = (String) typesFromMongoApi;

            if(stringType.length() <= 24){
                typesFromMongoApi = new ShortString();
            } else if( stringType.length() <= 120){
                typesFromMongoApi = new LongString();
            }

        }

        this.typesFromMongoApi.add(typesFromMongoApi.getClass());
    }

    public void setTypesFromMongoApi(Class<?> typesFromMongoApi) {
        this.typesFromMongoApi.add(typesFromMongoApi);
    }

    public SqlFieldType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlFieldType sqlType) {
        this.sqlType = sqlType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public RelationProperties getRelationProperties() {
        return relations;
    }

    public void setRelationsProperties(RelationProperties relations) {
        this.relations = relations;
    }

    public Set<Class<?>> getMongoType() {
        return typesFromMongoApi;
    }

    public void setMongoType(Set<Class<?>> mongoType) {
        this.typesFromMongoApi = mongoType;
    }

    public void addMongoType(Set<Class<?>> mongoType) {
        this.typesFromMongoApi.addAll(mongoType);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean IsInRelation(MongoFieldSchema fieldToCompare) {
        return false;
    }

}
