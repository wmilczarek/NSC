package Converter.ConverterMetaDataModels.BaseModel;

import Converter.ConverterMetaDataModels.MongoModel.DocumentRowMetaData;
import Converter.ModelController.Relations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by szef on 2014-09-15.
 */
public abstract class TranslationMetaDataEntity extends TranslationMetaDataObject {


    private Long autoPrimaryKey = Long.valueOf(0);

    public boolean isFromArray() {
        return isFromArray;
    }

    private boolean isFromArray;

    public TranslationMetaDataEntity(String metaDataObjectName) {
        super(metaDataObjectName);
        isFromArray = false;
    }

    private Set<TranslationMetaDataField> entityFields = new HashSet<TranslationMetaDataField>();

    public abstract Set<?> getTranslationMetaDataFieldsSchema();

    public abstract Set<DocumentRowMetaData> getTranslationMetaDataDocuments();

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

    public Set<TranslationMetaDataField> getForeginKeyFields() {

        Set<TranslationMetaDataField> translationMetaDataFields = new HashSet<TranslationMetaDataField>();

        for (TranslationMetaDataField field : entityFields) {

            if (field.getRelationProperties().getRelations() == Relations.ForeginKey) {
                translationMetaDataFields.add(field);
            }
        }

        return translationMetaDataFields;
    }


    public void setFromArray(boolean isFromArray) {
        this.isFromArray = isFromArray;
    }
}
