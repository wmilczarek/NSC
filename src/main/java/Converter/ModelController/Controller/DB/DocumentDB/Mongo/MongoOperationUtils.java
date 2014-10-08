package Converter.ModelController.Controller.DB.DocumentDB.Mongo;

import Converter.ConverterMetaDataModels.MongoModel.*;
import Converter.ModelController.Relations;

import java.util.Set;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;


public class MongoOperationUtils {



    public boolean equals2(Object object2, Object object) {
        return object.getClass() == object2.getClass() && object.equals(object2);
    }

    private void synchronizeKeys(TranslationDataBase translationDataBase) {

        // Dla każdego meta schematu
        for (TranslationEntitySchema translationMetaDataEntity : translationDataBase.getEntitiesSchema()) {

            // Dla każdego potencjalnego klucza obcego
            for (TranslationFieldSchema schemaField : translationMetaDataEntity.getForeginKeyFields()) {

                //Klucze powinny miec synchronizowane typy
                String primaryKeyOwner = schemaField.getMetaDataObjectName().replace("_id", "");

                TranslationEntitySchema pkOwner= (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(primaryKeyOwner), translationDataBase.getEntitiesSchema());
                TranslationFieldSchema pkOwnerField = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(primaryKeyOwner + "_PK"), pkOwner.getEntityFields());
                schemaField.setMetaDataType(pkOwnerField.getMetadataType());
                pkOwnerField.setMetaDataType(schemaField.getMetadataType());



            }
        }

    }


}
