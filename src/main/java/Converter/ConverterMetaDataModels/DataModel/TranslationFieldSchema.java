package Converter.ConverterMetaDataModels.DataModel;
    
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataObject;
import Converter.ModelController.HelperTypes.LongString;
import Converter.ModelController.HelperTypes.ShortString;
import Converter.ModelController.RelationProperties;
import Converter.ModelController.Relations;
import Converter.ModelController.SqlFieldType;

import java.util.HashSet;
import java.util.Set;


public class TranslationFieldSchema extends TranslationMetaDataObject {

    protected RelationProperties relations = new RelationProperties();
    protected SqlFieldType sqlType;
    protected Set<Class<?>> metaDataType = new HashSet<Class<?>>();

    public TranslationFieldSchema(String name) {

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

    public void keyChecker() {

        RelationProperties props = new RelationProperties();

        if (getMetaDataObjectName().equals("_id")) {


            props.setRelations(Relations.PrimaryKey);
            relations = props;
            return;

        } else if (getMetaDataObjectName().endsWith("_id") && getMetaDataObjectName().length() > 3){

            props.setRelations(Relations.ForeginKey);
            props.setFatherNames(getMetaDataObjectName().replace("_id", ""));
            relations = props;
            return;
        }

        props.setRelations(Relations.None);
        relations = props;
    }

    public SqlFieldType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlFieldType sqlType) {
        this.sqlType = sqlType;
    }

    public Set<Class<?>> getMetadataType() {
        return metaDataType;
    }

    public void setMetaDataType(Object metaDataType) {

        if(metaDataType == null){
            return;
        }

        if (metaDataType.getClass() == String.class) {
            String stringType = (String) metaDataType;

            if(stringType.length() <= 24){
                metaDataType = new ShortString();
            } else if( stringType.length() <= 120){
                metaDataType = new LongString();
            }

        }

        this.metaDataType.add(metaDataType.getClass());
    }

    public void setMetaDataType(Class<?> metaDataType) {
        this.metaDataType.add(metaDataType);
    }
}
