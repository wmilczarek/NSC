package Converter.ConverterMetaDataModels.MongoModel;

import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;
import Converter.ModelController.Relations;
import Converter.ModelController.SqlFieldType;

import java.util.*;

public class MongoEntitySchema extends TranslationMetaDataEntity {

    private Set<MongoFieldSchema> entityFields = new HashSet<MongoFieldSchema>();
    private String entityName;

    private Set<DocumentRowMetaData> mongoFieldData = new HashSet<DocumentRowMetaData>();

    public MongoEntitySchema(String name) {
        super(name);
    }

    public Set<DocumentRowMetaData> getMongoFieldData() {
        return mongoFieldData;
    }

    public void setMongoFieldData(Set<DocumentRowMetaData> mongoFieldData) {
        this.mongoFieldData = mongoFieldData;
    }

    public void setMongoFieldData(DocumentRowMetaData mongoFieldData) {
        this.mongoFieldData.add(mongoFieldData);
    }

    public Set<MongoFieldSchema> getEntityFields() {
        return entityFields;
    }

    public void setEntityFields(MongoFieldSchema entityFields) {
        this.entityFields.add(entityFields);
    }

    public void setEntityFields(Set<MongoFieldSchema> entityFields) {
        this.entityFields = entityFields;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public Set<?> getTranslationMetaDataFieldsSchema(){
        return entityFields;
    }

    @Override
    public Set<DocumentRowMetaData> getTranslationMetaDataDocuments() {
        return mongoFieldData;
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

    public List<MongoFieldSchema> getBindableFields() {

        List<MongoFieldSchema> mongoFieldSchemas = new ArrayList<MongoFieldSchema>();
        Map<String, MongoFieldSchema> bindableRelation = new HashMap<String, MongoFieldSchema>();

        for (MongoFieldSchema field : entityFields) {

            if (field.getSqlType() == SqlFieldType.DateTime ||
                    field.getSqlType() == SqlFieldType.Binary ||
                    field.getSqlType() == SqlFieldType.DoublePrecision ||
                    field.getSqlType() == SqlFieldType.Text ||
                    field.getSqlType() == SqlFieldType.Bool ||
                    field.getRelationProperties().getRelations() != Relations.None) {
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
