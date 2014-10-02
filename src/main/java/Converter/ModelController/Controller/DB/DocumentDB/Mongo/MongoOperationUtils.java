package Converter.ModelController.Controller.DB.DocumentDB.Mongo;

import Converter.ConverterMetaDataModels.BaseModel.DocumentDataBase;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataField;
import Converter.ConverterMetaDataModels.MongoModel.*;
import Converter.ModelController.Relations;

import java.util.Set;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;


public class MongoOperationUtils {

   public static void changeName(DocumentDataBase dataBase) {


        for (TranslationMetaDataEntity translationMetaDataEntity : (Set<TranslationMetaDataEntity>)dataBase.getEntitiesSchema()) {
            for (TranslationMetaDataField translationMetaDataField : (Set<TranslationMetaDataField>)translationMetaDataEntity.getTranslationMetaDataFieldsSchema()) {

                if (translationMetaDataField.getRelationProperties().getRelations() == Relations.PrimaryKey) {
                    translationMetaDataField.setMetaDataObjectName(translationMetaDataEntity.getMetaDataObjectName() + "_PK");
                }
            }

        }


        for (TranslationMetaDataEntity translationMetaDataEntity : (Set<TranslationMetaDataEntity>) dataBase.getEntitiesSchema()) {
            for (DocumentRowMetaData documentRowMetaData : (Set<DocumentRowMetaData>) translationMetaDataEntity.getTranslationMetaDataDocuments()) {

                Object obj = documentRowMetaData.getFieldValue().get("_id");
                if (obj != null) {
                    documentRowMetaData.getFieldValue().remove("_id");
                    documentRowMetaData.getFieldValue().put(translationMetaDataEntity.getMetaDataObjectName() + "_PK", obj);
                }
            }

        }
    }

    public boolean equals2(Object object2, Object object) {
        return object.getClass() == object2.getClass() && object.equals(object2);
    }

    private void synchronizeKeys(MongoDataBase mongoDataBase) {

        // Dla każdego meta schematu
        for (MongoEntitySchema translationMetaDataEntity : mongoDataBase.getEntitiesSchema()) {

            // Dla każdego potencjalnego klucza obcego
            for (TranslationMetaDataField schemaField : translationMetaDataEntity.getForeginKeyFields()) {

                //Klucze powinny miec synchronizowane typy
                String primaryKeyOwner = schemaField.getMetaDataObjectName().replace("_id", "");

                MongoEntitySchema pkOwner= (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(primaryKeyOwner), mongoDataBase.getEntitiesSchema());
                MongoFieldSchema pkOwnerField = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(primaryKeyOwner + "_PK"), pkOwner.getEntityFields());
                schemaField.setMetaDataType(pkOwnerField.getMetadataType());
                pkOwnerField.setMetaDataType(schemaField.getMetadataType());



            }
        }

    }


}
