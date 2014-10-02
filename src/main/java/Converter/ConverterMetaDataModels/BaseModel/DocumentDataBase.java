package Converter.ConverterMetaDataModels.BaseModel;

import Converter.ModelController.Controller.DB.Translator.DocumentDBToSQL;
import Converter.ModelController.Controller.DB.Translator.IncompatibleFieldTypeConversionException;

import java.util.Set;

/**
 * Created by szef on 2014-09-09.
 */
public abstract class DocumentDataBase {

    private String DBname;

    private Set<TranslationMetaDataObject> entitiesSchema;

    public Set<?> getEntitiesSchema() {
        return entitiesSchema;
    }

    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public void translateFieldsOfAllEntetiesToSqlTypes() {

        for (TranslationMetaDataEntity entitySchema : (Set<TranslationMetaDataEntity>) this.getEntitiesSchema()) {

            for (TranslationMetaDataField FieldSchema : (Set<TranslationMetaDataField>) entitySchema.getTranslationMetaDataFieldsSchema()) {

                try {
                    FieldSchema.setSqlType(DocumentDBToSQL.documentToSqlFieldConversion(FieldSchema.getMetadataType()));
                } catch (IncompatibleFieldTypeConversionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public abstract void createIntersectionTable(TranslationMetaDataEntity array);
}
