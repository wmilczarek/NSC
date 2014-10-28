package Converter.ModelController.Controller.DB.DocumentDB;


import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataObject;
import Converter.ConverterMetaDataModels.DataModel.*;
import Converter.ModelController.Relations;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class CommonOperationUtils {


    //związki wiele do wielu dla tablic danych
    public static void dataArrayRelationNormalization(TranslationDataBase documentDataBase) {

        List<TranslationEntitySchema> arrays = ((Set<TranslationEntitySchema>) documentDataBase.getEntitiesSchema()).stream().filter(e -> e.isFromArray() == IsArray.DataArry).collect(Collectors.toList());

        for (TranslationEntitySchema array : arrays) {

            Set<Object> compare = new HashSet<Object>();
            // dla poszczegolnej tablicy nalezy zbadac czy nie jest ona w zwiazku wiele do wielu. w tym celu nalezy zbadac czy przetrzymywane wartosci sa rozne.
            for (DocumentRowMetaData row : array.getTranslationMetaDataDocuments()) {

                //pobierz wartość
                Object value = row.getFieldValue().get(array.getMetaDataObjectName() + array.isFromArray().toString());

                if (compare.contains(value)) {
                    documentDataBase.createIntersectionTableFromDenormalizedData(array);

                    break;

                } else {
                    compare.add(value);
                }

            }

        }

    }

    //związki wiele do wielu dla tablic danych
    public static void searchForDataRedundancy(TranslationDataBase documentDataBase) {

        List<TranslationEntitySchema> schemas = ((Set<TranslationEntitySchema>) documentDataBase.getEntitiesSchema()).stream().filter(e -> e.isFromArray() == IsArray.NO).collect(Collectors.toList());

        for (TranslationEntitySchema schema : schemas) {


            Object[] rows = schema.getTranslationMetaDataDocuments().toArray();
/*            Set<DocumentRowMetaData> sameData = new HashSet<>();*/

            for (int i = 0; i < schema.getTranslationMetaDataDocuments().size(); i++) {

                for (int j = i + 1; j < schema.getTranslationMetaDataDocuments().size(); j++) {


                    if (((DocumentRowMetaData) rows[i]).equalsNormalization(rows[j], schema.getNonKeyFields())) {
                        replaceId(((DocumentRowMetaData) rows[i]).getFieldValue().get("_id"), ((DocumentRowMetaData) rows[j]).getFieldValue().get("_id"), documentDataBase);
                    }

                }

            }

        }

    }

    private static void replaceId(Object id, Object id1, TranslationDataBase documentDataBase) {

        List<Set<DocumentRowMetaData>> test = documentDataBase.getEntitiesSchema().stream().map(TranslationEntitySchema::getTranslationMetaDataDocuments).collect(Collectors.toList());


    }


    public static void referenceArrayRelationNormalization(TranslationDataBase documentDataBase) {
        List<TranslationEntitySchema> arrays = ((Set<TranslationEntitySchema>) documentDataBase.getEntitiesSchema()).stream().filter(e -> e.isFromArray() == IsArray.ReferenceArray).collect(Collectors.toList());
        for (TranslationEntitySchema array : arrays) {


            List<TranslationEntitySchema> selfRefArray = arrays.stream().filter(e -> (array.isFromArray().getDestiny() + "_" + array.isFromArray().getFrom()).equals(e.getMetaDataObjectName())).collect(Collectors.toList());

            if (selfRefArray.size() > 0) {
                documentDataBase.joinReferenceArrays(array, selfRefArray.get(0));
                findAndRemoveMetaData(selfRefArray.get(0), documentDataBase.getEntitiesSchema());
                continue;
            }
   /*     }
        //TODO:TESTY i usuwanie
        for (TranslationEntitySchema array : arrays) {
*/
            TranslationEntitySchema translationEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(array.isFromArray().getDestiny()), documentDataBase.getEntitiesSchema());
            TranslationFieldSchema translationFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(array.isFromArray().getFrom() + "_id"), translationEntitySchema.getEntityFields());


            for (DocumentRowMetaData row : array.getTranslationMetaDataDocuments()) {

                DocumentRowMetaData document = translationEntitySchema.getTranslationMetaDataDocuments().stream().filter(e->e.getFieldValue().get("_id").equals(row.getFieldValue().get(array.isFromArray().getDestiny() + "_id"))).collect(Collectors.toList()).get(0);
                document.setFieldValue( array.isFromArray().getFrom() + "_id",  row.getFieldValue().get(array.isFromArray().getFrom() + "_id"));

                translationFieldSchema.setMetaDataType(row.getFieldValue().get(array.isFromArray().getFrom() + "_id"));
                translationFieldSchema.keyChecker();
            }

            findAndRemoveMetaData(array, documentDataBase.getEntitiesSchema());
        }
    }

    public static void changeName(TranslationDataBase dataBase) {


        for (TranslationEntitySchema translationMetaDataEntity : dataBase.getEntitiesSchema()) {
            for (TranslationFieldSchema translationMetaDataField : translationMetaDataEntity.getTranslationMetaDataFieldsSchema()) {

                if (translationMetaDataField.getRelationProperties().getRelations() == Relations.PrimaryKey) {
                    translationMetaDataField.setMetaDataObjectName(translationMetaDataEntity.getMetaDataObjectName() + "_PK");
                }
            }

        }

        for (TranslationEntitySchema translationMetaDataEntity : dataBase.getEntitiesSchema()) {
            for (DocumentRowMetaData documentRowMetaData : translationMetaDataEntity.getTranslationMetaDataDocuments()) {

                Object obj = documentRowMetaData.getFieldValue().get("_id");
                if (obj != null) {
                    documentRowMetaData.getFieldValue().remove("_id");
                    documentRowMetaData.getFieldValue().put(translationMetaDataEntity.getMetaDataObjectName() + "_PK", obj);
                }
            }

        }
    }


    public static Object checkForDuplication(String schemaName, DocumentRowMetaData documentRowMetaData, TranslationDataBase translationDataBase) {

        TranslationEntitySchema translationEntitySchema = translationDataBase.getEntitiesSchema().stream().filter(s -> s.getMetaDataObjectName().equals(schemaName)).collect(Collectors.toList()).get(0);

        List<String> fieldsKey = translationEntitySchema.getNonPrimaryKeyFields();

        for (DocumentRowMetaData doc : translationEntitySchema.getTranslationMetaDataDocuments().stream().filter(d -> d.getFieldValue().get("_id").equals(documentRowMetaData.getFieldValue().get("_id")) == false).collect(Collectors.toList())) {

            if (doc.equalsNormalization(documentRowMetaData, fieldsKey)) {

                translationEntitySchema.getTranslationMetaDataDocuments().remove(documentRowMetaData);
                return doc.getFieldValue().get("_id");
            }
        }
        return null;
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
