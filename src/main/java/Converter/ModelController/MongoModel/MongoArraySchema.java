package Converter.ModelController.MongoModel;

import Converter.ModelController.Relations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szef on 2014-08-25.
 */
public class MongoArraySchema {

    private String arrayName;
    private List<MongoFieldSchema> arrayFields = new ArrayList<MongoFieldSchema>();
    private boolean manyToMany;
    private String fatherName;

    public MongoArraySchema(String name) {
        this.arrayName = name;
    }

    public boolean isManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(boolean manyToMany) {
        this.manyToMany = manyToMany;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public List<MongoFieldSchema> getArrayFields() {
        return arrayFields;
    }

    public void setArrayFields(List<MongoFieldSchema> arrayFields) {
        this.arrayFields = arrayFields;
    }

    public void setEntityFields(MongoFieldSchema entityFields) {
        this.arrayFields.add(entityFields);
    }

    public void setArrayFieldsFields(List<MongoFieldSchema> entityFields) {
        this.arrayFields = entityFields;
    }

    public MongoFieldSchema findCreateField(String name) {

        for (MongoFieldSchema field : arrayFields) {
            if (field.getFieldName().equals(name))
                return field;
        }

        return new MongoFieldSchema(name);
    }

    public boolean appendArrayFields(MongoFieldSchema newField) {

        for (MongoFieldSchema field : arrayFields) {
            if (field.getFieldName().equals(newField.getFieldName())) {

                arrayFields.remove(field);
                arrayFields.add(newField);
                return true;
            }

        }

        return arrayFields.add(newField);
    }


    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public List<MongoFieldSchema> getForeginKeys() {

        List<MongoFieldSchema> bindableField = new ArrayList<MongoFieldSchema>();
        for (MongoFieldSchema field : arrayFields) {
            if (field.getRelationProperties().getRelations() == Relations.ForeginKey) {
                bindableField.add(field);
            }

        }

        return bindableField;

    }
}
