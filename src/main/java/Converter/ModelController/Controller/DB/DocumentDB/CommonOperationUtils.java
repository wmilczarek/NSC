package Converter.ModelController.Controller.DB.DocumentDB;


import Converter.ConverterMetaDataModels.BaseModel.DocumentDataBase;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataObject;
import Converter.ConverterMetaDataModels.MongoModel.DocumentRowMetaData;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class CommonOperationUtils {



    //związki wiele do wielu dla tablic
    public static void dataArrayRelationNormalization(DocumentDataBase documentDataBase){


        List<TranslationMetaDataEntity> arrays = ((Set<TranslationMetaDataEntity>) documentDataBase.getEntitiesSchema()).stream().filter(e -> e.isFromArray()).collect(Collectors.toList());


        for(TranslationMetaDataEntity array:arrays){


            Set<Object> test = new HashSet<Object>();

            // dla poszczegolnej tablicy nalezy zbadac czy nie jest ona w zwiazku wiele do wielu. w tym celu nalezy zbadac czy przetrzymywane wartosci sa rozne.
            for(DocumentRowMetaData row: array.getTranslationMetaDataDocuments()){

                //pobierz wartość
                Object value = row.getFieldValue().get(array.getMetaDataObjectName() + "_value");

                if( test.contains(value))
                {


                    documentDataBase.createIntersectionTable(array);

                    break;

                } else {
                    test.add(value);
                }

            }

        }

    }



    public static <T extends TranslationMetaDataObject> TranslationMetaDataObject findOrCreateMetaData(T newObject, Set<T> metaData) {

        if (!metaData.contains(newObject)) {
            metaData.add(newObject);
            return newObject;
        }

        TranslationMetaDataObject entity = metaData.stream().filter(e -> e.getMetaDataObjectName().equals(newObject.getMetaDataObjectName())).findFirst().get();
        return entity;
    }

    public static <T extends TranslationMetaDataObject> boolean findAndRemoveMetaData(T objectToRemove, Set<T> metaData) {

        if (metaData.contains(objectToRemove)) {
            metaData.remove(objectToRemove);
            return true;
        }

        return false;
    }


}
