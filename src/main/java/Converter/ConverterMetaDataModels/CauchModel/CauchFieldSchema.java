package Converter.ConverterMetaDataModels.CauchModel;

import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataField;

import java.util.Set;


public class CauchFieldSchema extends TranslationMetaDataField {

    private Object value;


    public CauchFieldSchema(String name) {
        super(name);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }



    public Set<Class<?>> getMongoType() {
        return metaDataType;
    }

    public void setMongoType(Set<Class<?>> mongoType) {
        this.metaDataType = mongoType;
    }

    public void addMongoType(Set<Class<?>> mongoType) {
        this.metaDataType.addAll(mongoType);
    }

    public String getFieldName() {
        return this.getMetaDataObjectName();
    }

    public void setFieldName(String fieldName) {
        this.setMetaDataObjectName(fieldName);
    }

    public boolean IsInRelation(CauchFieldSchema fieldToCompare) {
        return false;
    }

}
