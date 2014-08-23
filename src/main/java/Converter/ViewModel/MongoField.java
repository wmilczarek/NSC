package Converter.ViewModel;

import java.util.Set;

/**
 * Created by szef on 2014-08-14.
 */
public class MongoField {

    private Set<Class<?>> typesFromMongoApi;
    private Object value;
    private Relations relations;
    private String fieldName;

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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean IsInRelation(MongoField fieldToCompare) {
        return false;
    }

}
