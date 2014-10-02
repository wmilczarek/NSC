package Converter.ConverterMetaDataModels.CauchModel;


import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;
import Converter.ConverterMetaDataModels.MongoModel.DocumentRowMetaData;

import java.util.HashSet;
import java.util.Set;

public class CouchEntitySchema extends TranslationMetaDataEntity {

    private Set<CauchFieldSchema> entityFields = new HashSet<CauchFieldSchema>();

    public Set<DocumentRowMetaData> getEntityData() {
        return entityData;
    }

    public void setEntityData(Set<DocumentRowMetaData> entityData) {
        this.entityData = entityData;
    }

    public void setEntityData(DocumentRowMetaData entityData) {
        this.entityData.add(entityData);
    }

    Set<DocumentRowMetaData> entityData = new HashSet<DocumentRowMetaData>();

    //private String entityName;
    private Long autoPrimaryKey = Long.valueOf(0);

    public CouchEntitySchema(String schemaName) {
        super(schemaName);
    }


    public Set<CauchFieldSchema> getEntityFields() {
        return entityFields;
    }

    public void setEntityFields(Set<CauchFieldSchema> entityFields) {
        this.entityFields = entityFields;
    }

    public String getEntityName() {
        return super.getMetaDataObjectName();
    }

    public void setEntityName(String entityName) {
        super.setMetaDataObjectName(entityName);
    }

    public Long getAutoPrimaryKey() {
        return autoPrimaryKey;
    }

    public void setAutoPrimaryKey(Long autoPrimaryKey) {
        this.autoPrimaryKey = autoPrimaryKey;
    }

    public Long incrementAutoPriamryKey() {
        return this.autoPrimaryKey++;
    }


    public boolean appendEntityFields(CauchFieldSchema newField) {

        for (CauchFieldSchema field : entityFields) {
            if (field.getFieldName().equals(newField.getFieldName())) {

                entityFields.remove(field);
                entityFields.add(newField);
                return true;
            }

        }

        return entityFields.add(newField);
    }

    @Override
    public Set<CauchFieldSchema> getTranslationMetaDataFieldsSchema(){
        return entityFields;

    }

    @Override
    public Set<DocumentRowMetaData> getTranslationMetaDataDocuments() {
        return entityData;
    }
}
