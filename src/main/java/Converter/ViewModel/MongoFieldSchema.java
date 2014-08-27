package Converter.ViewModel;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by szef on 2014-08-14.
 */
public class MongoFieldSchema {

    private Set<Class<?>> typesFromMongoApi = new HashSet<Class<?>>();
    private Object value;
    private Relations relations;
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

    public Relations getRelations() {
        return relations;
    }

    public void setRelations(Relations relations) {
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
