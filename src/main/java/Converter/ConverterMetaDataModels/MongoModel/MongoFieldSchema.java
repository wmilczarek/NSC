package Converter.ConverterMetaDataModels.MongoModel;

import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataField;
import Converter.ModelController.RelationProperties;


public class MongoFieldSchema extends TranslationMetaDataField {

    public MongoFieldSchema(String name) {

        super(name);
    }

    public RelationProperties getRelationProperties() {
        return relations;
    }

    public void setRelationsProperties(RelationProperties relations) {
        this.relations = relations;
    }


    public String getFieldName() {

        return this.metaDataObjectName;
    }

    public void setFieldName(String fieldName) {
        this.metaDataObjectName = fieldName;
    }

}
