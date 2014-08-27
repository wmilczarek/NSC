package Converter.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by szef on 2014-08-12.
 */
public class MongoEntitySchema {

    private List<MongoFieldSchema> entityFields = new ArrayList<MongoFieldSchema>();
    private String entityName;
    private Long autoPrimaryKey = Long.valueOf(0);

    public MongoEntitySchema() {
    }

    public MongoEntitySchema(String name) {
        this.entityName = name;
    }

    public List<MongoFieldSchema> getEntityFields() {
        return entityFields;
    }

    public void setEntityFields(List<MongoFieldSchema> entityFields) {
        this.entityFields = entityFields;
    }

    public void setEntityFields(MongoFieldSchema entityFields) {
        this.entityFields.add(entityFields);
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public MongoFieldSchema findCreateField(String name) {

        for (MongoFieldSchema field : entityFields) {
            if (field.getFieldName().equals(name))
                return field;
        }

        return new MongoFieldSchema(name);
    }

    public boolean appendEntityFields(MongoFieldSchema newField) {

        for (MongoFieldSchema field : entityFields) {
            if (field.getFieldName().equals(newField.getFieldName())) {

                entityFields.remove(field);
                entityFields.add(newField);
                return true;
            }

        }

        return entityFields.add(newField);
    }

    public Long getAutoPrimaryKey() {
        return autoPrimaryKey;
    }

    public void setAutoPrimaryKey(Long autoPrimaryKey) {
        this.autoPrimaryKey = autoPrimaryKey;
    }

    public Long incrementAutoPrimaryKey() {
        return ++this.autoPrimaryKey;
    }

    public List<MongoFieldSchema> getBindableFields() {

        List<MongoFieldSchema> mongoFieldSchemas = new ArrayList<MongoFieldSchema>();
        Map<String, MongoFieldSchema> bindableRelation = new HashMap<String, MongoFieldSchema>();

        for (MongoFieldSchema field : entityFields) {

            if (field.getSqlType() == SqlFieldType.DateTime ||
                    field.getSqlType() == SqlFieldType.Binary ||
                    field.getSqlType() == SqlFieldType.DoublePrecision ||
                    field.getSqlType() == SqlFieldType.Text ||
                    field.getSqlType() == SqlFieldType.Bool ||
                    field.getRelations() != Relations.None) {
                continue;
            } else if (mongoFieldSchemas.contains("_id")) {


                mongoFieldSchemas.add(field);

            }
        }

        return mongoFieldSchemas;
    }

    public MongoFieldSchema getPrimaryKey() {

        for (MongoFieldSchema field : entityFields) {
            if (field.getFieldName().equals("_id"))
                return field;
        }

        return null;
    }

}
