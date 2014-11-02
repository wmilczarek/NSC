package Converter.ModelController.Controller.DB.DocumentDB.Cauch;


import Converter.ConverterMetaDataModels.DataModel.*;
import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;
import Converter.ModelController.DocumentTypesDB;
import Converter.ModelController.Relations;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Converter.ModelController.Controller.DB.DocumentDB.Cauch.CauchUtils.resolvePreMetaDataTypes;
import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.*;


public class CouchDataBaseOperations extends DocumentDataBaseOperations {

    private static final CouchDataBaseOperations ourInstance = new CouchDataBaseOperations();

    public static CouchDataBaseOperations getInstance() {
        return ourInstance;
    }

    @Override
    public List<String> GetDataBaseNames() {

        List<String> test = new ArrayList<String>();


        return test;
    }

    @Override
    public void loadDataBase(DocumentTypesDB documentTypesDB, String dbName) {

        dataBase = new TranslationDataBase(dbName);
        loadIntoMemory(documentTypesDB, dbName);
        dataArrayRelationNormalization(dataBase);
        referenceArrayRelationNormalization(this.dataBase);
        dataBase.translateFieldsOfAllEntetiesToSqlTypes();
        changeName(dataBase);

        try {
            printMetaDataToSQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadDataBase(String dbName) {

    }

    @Override
    public List<List<String>> showFields(String dbName, String EntityName) throws UnknownHostException {
        return null;
    }

    @Override
    public void loadIntoMemory(String dbName) {

    }

    @Override
    public void loadIntoMemory(DocumentTypesDB documentTypesDB, String dbName) {

        List<JsonObject> list = null;
        try {
            list = CouchDBConnector.getInstance().getCauchClient(documentTypesDB, dbName).view("type/type").query(JsonObject.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (JsonObject object : list) {

            JsonObject values = object.get("value").getAsJsonObject();
            getObjectsDataAndCreateSchema(values, object.get("key").getAsString(), null);
        }
    }

    private void getObjectsDataAndCreateSchema(JsonObject jsonObject, String entityName, DocumentRowMetaData existingMetaDataRow) {

        DocumentRowMetaData documentRowMetaData;
        TranslationEntitySchema translationEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(entityName), dataBase.getEntitiesSchema());

        if (existingMetaDataRow != null) {
            documentRowMetaData = existingMetaDataRow;

        } else {
            documentRowMetaData = new DocumentRowMetaData();
        }

        for (Map.Entry<String, JsonElement> currentField : jsonObject.entrySet()) {

            if (currentField.getKey().equals("type")) {
                continue;
            }

            TranslationFieldSchema translationFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(currentField.getKey()), translationEntitySchema.getEntityFields());
            //translationEntitySchema.appendEntityFields(fields);

            if (currentField.getValue().isJsonObject()) {

                translationFieldSchema.setMetaDataType(JsonObject.class);
                // Stwórz nowe pole <nazwa>_id
                String typeName = currentField.getKey();
                String newFieldName = typeName + "_id";

                //Przykład wywołania metody findOrCreateMetaData dla obiektu //TranslationFieldSchema
                TranslationFieldSchema newFkField = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(newFieldName), translationEntitySchema.getEntityFields());
                newFkField.keyChecker();
                // pobierz lub stworz wartość ID dla pola które jest FK. Z pod encji.
                TranslationEntitySchema currentEntityField = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(typeName), dataBase.getEntitiesSchema());
                Object fkValue = this.tryToGetForeginKeyOfSubDocument(currentField);
                newFkField.setMetaDataType(fkValue);
                documentRowMetaData.setFieldValue(newFieldName, fkValue);

                try {
                    resolveObjectForeginKeyRelation(currentField.getValue().getAsJsonObject(), typeName, currentEntityField);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                continue;
            } else if (currentField.getValue().isJsonArray()) {
                this.handleArrayAsEntity(currentField.getKey(), entityName, jsonObject.get("_id"), currentField.getValue().getAsJsonArray());
                translationFieldSchema.setMetaDataType(JsonArray.class);
                translationFieldSchema.getRelationProperties().setRelations(Relations.None);
                continue;
            }

            documentRowMetaData.setFieldValue(currentField.getKey(), resolvePreMetaDataTypes(currentField.getValue()));
            translationFieldSchema.setMetaDataType(resolvePreMetaDataTypes(currentField.getValue()));
            translationFieldSchema.keyChecker();

        }
        translationEntitySchema.setFieldData(documentRowMetaData);
    }


    private void resolveObjectForeginKeyRelation(JsonObject dbObject, String fieldName, TranslationEntitySchema currentEntityField) throws UnknownHostException {

        // Jeśli nie ma domyślnej wartości ID, używaj wbudowanego auto inkrementującego się klucz.
        if (!this.hasDocumentAnyId(dbObject)) {

            TranslationEntitySchema mongoEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(fieldName), dataBase.getEntitiesSchema());
            TranslationFieldSchema mongoFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema("_id"), mongoEntitySchema.getEntityFields());
            mongoFieldSchema.setMetaDataType(Long.class);
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            documentRowMetaData.setFieldValue("_id", currentEntityField.getIncrementAutoPrimaryKey());
            mongoFieldSchema.keyChecker();
            getObjectsDataAndCreateSchema(dbObject, fieldName, documentRowMetaData);


        } else {
            getObjectsDataAndCreateSchema((JsonObject) dbObject, fieldName, null);
        }
    }

    private boolean hasDocumentAnyId(JsonObject currentField) {

        // jeśli poddokument nie posiada klucza _id stworz go!
        try {
            return currentField.get("_id") != null;
        } catch (Exception e) {
            return false;
        }
    }


    private Object tryToGetForeginKeyOfSubDocument(Map.Entry<String, JsonElement> currentField) {

        try {
            return currentField.getValue().getAsJsonObject().get("_id").getAsString();
        } catch (Exception e) {
            TranslationEntitySchema entitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(currentField.getKey()), dataBase.getEntitiesSchema());
            return entitySchema.incrementAutoPriamryKey();
        }
    }

    //3 mozliwosci
    private void handleArrayAsEntity(String typeName, String fatherEntityName, JsonElement fatherId, JsonArray jsonArray) {

        IsArray arrayType = IsArray.DataArry;
        String suffix = "_value";
        String newEntityName = typeName;

        if (typeName.endsWith("_id")) {
            arrayType = IsArray.ReferenceArray;
            suffix = "_id";
            typeName = typeName.replace("_id","");
            newEntityName = fatherEntityName + "_" + typeName;
        }


        //Spojne Typy
        if (jsonArray.size() > 0 && jsonArray.get(0).isJsonObject()) {


            handleArrayOfObjects(fatherEntityName, fatherId, jsonArray, newEntityName);
            return;
        }

        TranslationEntitySchema couchEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(newEntityName), dataBase.getEntitiesSchema());
        TranslationFieldSchema cauchFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(typeName + suffix), couchEntitySchema.getEntityFields());
        TranslationFieldSchema cauchIdFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(fatherEntityName + "_id"), couchEntitySchema.getEntityFields());
        cauchIdFieldSchema.setMetaDataType(resolvePreMetaDataTypes(fatherId));
        cauchFieldSchema.keyChecker();
        cauchIdFieldSchema.keyChecker();
        arrayType.setFrom(fatherEntityName);
        arrayType.setDestiny(typeName);
        couchEntitySchema.setFromArray(arrayType);

        for (JsonElement element : jsonArray) {
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            cauchFieldSchema.setMetaDataType(resolvePreMetaDataTypes(element));
            documentRowMetaData.setFieldValue(fatherEntityName + "_id", resolvePreMetaDataTypes(fatherId));
            documentRowMetaData.setFieldValue(typeName + suffix, resolvePreMetaDataTypes(element));
            couchEntitySchema.setFieldData(documentRowMetaData);
        }


    }

    private void handleArrayOfObjects(String fatherEntityName, JsonElement fatherId, JsonArray jsonArray, String newEntityName) {

        TranslationEntitySchema couchEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(newEntityName), dataBase.getEntitiesSchema());
        TranslationFieldSchema cauchFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(fatherEntityName + "_id"), couchEntitySchema.getEntityFields());
        cauchFieldSchema.setMetaDataType(resolvePreMetaDataTypes(fatherId));
        cauchFieldSchema.keyChecker();
        for (JsonElement element : jsonArray) {
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            documentRowMetaData.setFieldValue(fatherEntityName + "_id", resolvePreMetaDataTypes(fatherId));
            getObjectsDataAndCreateSchema(element.getAsJsonObject(), newEntityName, documentRowMetaData);
        }
    }


}
