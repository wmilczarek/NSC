package Converter.ConverterMetaDataModels.DataModel;

import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataObject;
import Converter.ModelController.Relations;
import Converter.ModelController.SqlFieldType;

import java.util.*;
import java.util.stream.Collectors;

public class TranslationEntitySchema extends TranslationMetaDataObject {


    private Set<DocumentRowMetaData> mongoFieldData = new HashSet<DocumentRowMetaData>();
    private Long autoPrimaryKey = Long.valueOf(0);
    private IsArray isFromArray;
    private Set<TranslationFieldSchema> entityFields = new HashSet<TranslationFieldSchema>();

    public TranslationEntitySchema(String name) {

        super(name);
        isFromArray = IsArray.NO;
    }

    public Set<DocumentRowMetaData> getMongoFieldData() {
        return mongoFieldData;
    }

    public void setFieldData(DocumentRowMetaData mongoFieldData) {
        this.mongoFieldData.add(mongoFieldData);
    }

    public Set<TranslationFieldSchema> getEntityFields() {
        return entityFields;
    }

    public void setEntityFields(TranslationFieldSchema entityFields) {
        this.entityFields.add(entityFields);
    }

    public void setEntityFields(Set<TranslationFieldSchema> entityFields) {
        this.entityFields = entityFields;
    }

    public String getEntityName() {
        return metaDataObjectName;
    }

    public void setEntityName(String entityName) {
        this.metaDataObjectName = entityName;
    }

    public Set<TranslationFieldSchema> getTranslationMetaDataFieldsSchema(){
        return entityFields;
    }

    public Set<DocumentRowMetaData> getTranslationMetaDataDocuments() {
        return mongoFieldData;
    }

    public boolean appendEntityFields(TranslationFieldSchema newField) {

        for (TranslationFieldSchema field : entityFields) {
            if (field.getFieldName().equals(newField.getFieldName())) {

                entityFields.remove(field);
                entityFields.add(newField);
                return true;
            }

        }

        return entityFields.add(newField);
    }

    public List<TranslationFieldSchema> getBindableFields() {

        List<TranslationFieldSchema> translationFieldSchemas = new ArrayList<TranslationFieldSchema>();
        Map<String, TranslationFieldSchema> bindableRelation = new HashMap<String, TranslationFieldSchema>();

        for (TranslationFieldSchema field : entityFields) {

            if (field.getSqlType() == SqlFieldType.DateTime ||
                    field.getSqlType() == SqlFieldType.Binary ||
                    field.getSqlType() == SqlFieldType.DoublePrecision ||
                    field.getSqlType() == SqlFieldType.Text ||
                    field.getSqlType() == SqlFieldType.Bool ||
                    field.getRelationProperties().getRelations() != Relations.None) {
                continue;
            } else if (translationFieldSchemas.contains("_id")) {

                translationFieldSchemas.add(field);
            }
        }

        return translationFieldSchemas;
    }



    public TranslationFieldSchema getPrimaryKey() {

        for (TranslationFieldSchema field : entityFields) {
            if (field.getFieldName().equals("_id"))
                return field;
        }

        return null;
    }

    public IsArray isFromArray() {
        return isFromArray;
    }

    public Long getAutoPrimaryKey() {
        return autoPrimaryKey;
    }

    public void setAutoPrimaryKey(Long autoPrimaryKey) {
        this.autoPrimaryKey = autoPrimaryKey;
    }

    public Long getIncrementAutoPrimaryKey() {
        return this.autoPrimaryKey;
    }

    public Long incrementAutoPriamryKey() {
        ++this.autoPrimaryKey;
        return this.autoPrimaryKey;
    }

    public Set<TranslationFieldSchema> getForeginKeyFields() {

        Set<TranslationFieldSchema> translationMetaDataFields = new HashSet<TranslationFieldSchema>();

        for (TranslationFieldSchema field : entityFields) {

            if (field.getRelationProperties().getRelations() == Relations.ForeginKey) {
                translationMetaDataFields.add(field);
            }
        }

        return translationMetaDataFields;
    }

    public List<String> getNonKeyFields(){
         return entityFields.stream().filter(f -> f.getRelationProperties().getRelations() == Relations.None).map(TranslationFieldSchema::getFieldName).collect(Collectors.toList());

    }

    public List<String> getNonPrimaryKeyFields(){
        return entityFields.stream().filter(f -> f.getRelationProperties().getRelations() != Relations.PrimaryKey).map(TranslationFieldSchema::getFieldName).collect(Collectors.toList());

    }

    public void setFromArray(IsArray isFromArray) {
        this.isFromArray = isFromArray;
    }
}
