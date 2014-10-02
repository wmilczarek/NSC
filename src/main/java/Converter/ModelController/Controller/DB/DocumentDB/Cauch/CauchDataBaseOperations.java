package Converter.ModelController.Controller.DB.DocumentDB.Cauch;


import Converter.ConverterMetaDataModels.CauchModel.CauchDataBase;
import Converter.ConverterMetaDataModels.CauchModel.CauchFieldSchema;
import Converter.ConverterMetaDataModels.CauchModel.CouchEntitySchema;
import Converter.ConverterMetaDataModels.MongoModel.DocumentRowMetaData;
import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;
import Converter.ModelController.Relations;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.codehaus.jettison.json.JSONArray;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Converter.ModelController.Controller.DB.DocumentDB.Cauch.CauchUtils.resolvePreMetaDataTypes;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;
import static Converter.ModelController.Controller.DB.Relational.Operations.RelationalDataBaseOperations.*;


public class CauchDataBaseOperations extends DocumentDataBaseOperations {

    private static final CauchDataBaseOperations ourInstance = new CauchDataBaseOperations();
    private CauchDataBase cauchDataBase;

    public static CauchDataBaseOperations getInstance() {
        return ourInstance;
    }

    @Override
    public List<String> GetDataBaseNames() {

        List<String> test = new ArrayList<String>();

        test.add("objarray");

        return test;
    }

    @Override
    public List<String> loadDataBase(String dbName) throws SQLException {

        cauchDataBase = new CauchDataBase();
        loadIntoMemory(dbName);


        cauchDataBase.translateFieldsOfAllEntetiesToSqlTypes();
        createEntitiesSchemaScript(cauchDataBase);
        createSQLReferencesScript(cauchDataBase);
        createSQLInsertScript(cauchDataBase);


        return null;
    }

    @Override
    public List<List<String>> showFields(String dbName, String EntityName) throws UnknownHostException {
        return null;
    }

    @Override
    public void loadIntoMemory(String dbName) {

        List<JsonObject> list = null;
        try {
            list = CauchConnector.getCauchClient(null).view("arrayobjtype/arrayobjtype").query(JsonObject.class);
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
        CouchEntitySchema entitySchema = (CouchEntitySchema) findOrCreateMetaData(new CouchEntitySchema(entityName), cauchDataBase.getEntitiesSchema());

        if (existingMetaDataRow != null) {
            documentRowMetaData = existingMetaDataRow;

        } else {
            documentRowMetaData = new DocumentRowMetaData();
        }

        for (Map.Entry<String, JsonElement> currentField : jsonObject.entrySet()) {

            if (currentField.getKey().equals("type")) {
                continue;
            }

            CauchFieldSchema cauchFieldSchema = (CauchFieldSchema) findOrCreateMetaData(new CauchFieldSchema(currentField.getKey()), entitySchema.getEntityFields());
            //entitySchema.appendEntityFields(fields);

            if (currentField.getValue().isJsonObject()) {

                cauchFieldSchema.setMetaDataType(JsonObject.class);

                // Stwórz nowe pole <nazwa>_id
                String typeName = currentField.getValue().getAsJsonObject().get("type").getAsString();
                String newFieldName = typeName + "_id";


                CauchFieldSchema newFkField = (CauchFieldSchema) findOrCreateMetaData(new CauchFieldSchema(newFieldName), entitySchema.getEntityFields());

                newFkField.keyChecker();

                //=======================================


                // pobierz lub stworz wartość ID dla pola które jest FK. Z pod encji.
                CouchEntitySchema currentEntityField = (CouchEntitySchema) findOrCreateMetaData(new CouchEntitySchema(typeName), cauchDataBase.getEntitiesSchema());
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
                cauchFieldSchema.setMetaDataType(JsonArray.class);
                cauchFieldSchema.getRelationProperties().setRelations(Relations.None);
                continue;
            }

            documentRowMetaData.setFieldValue(currentField.getKey(), resolvePreMetaDataTypes(currentField.getValue()));
            cauchFieldSchema.setMetaDataType(resolvePreMetaDataTypes(currentField.getValue()));
            cauchFieldSchema.keyChecker();

        }
        entitySchema.setEntityData(documentRowMetaData);

    }


    private void resolveObjectForeginKeyRelation(JsonObject dbObject, String fieldName, CouchEntitySchema currentEntityField) throws UnknownHostException {

        // Jeśli nie ma domyślnej wartości ID, używaj wbudowanego auto inkrementującego się klucz.
        if (!this.hasDocumentAnyId(dbObject)) {

            CouchEntitySchema mongoEntitySchema = (CouchEntitySchema) findOrCreateMetaData(new CouchEntitySchema(fieldName), cauchDataBase.getEntitiesSchema());
            CauchFieldSchema mongoFieldSchema = (CauchFieldSchema) findOrCreateMetaData(new CauchFieldSchema("_id"), mongoEntitySchema.getEntityFields());
            mongoFieldSchema.setMetaDataType(Long.class);
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            documentRowMetaData.setFieldValue("_id", currentEntityField.getIncrementAutoPrimaryKey());
            mongoFieldSchema.keyChecker();
            getObjectsDataAndCreateSchema( dbObject.get(fieldName).getAsJsonObject(), fieldName, documentRowMetaData);


        } else {
            getObjectsDataAndCreateSchema((JsonObject) dbObject.get(fieldName), fieldName, null);
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
            CouchEntitySchema entitySchema = (CouchEntitySchema) findOrCreateMetaData(new CouchEntitySchema(currentField.getValue().getAsJsonObject().get("type").getAsString()), cauchDataBase.getEntitiesSchema());
            return entitySchema.incrementAutoPriamryKey();
        }
    }

    //3 mozliwosci
    private void handleArrayAsEntity(String typeName, String fatherEntityName, JsonElement fatherId, JsonArray jsonArray) {

        String suffix = "_value";
        String newEntityName = typeName;

        if (typeName.endsWith("_id")) {
            suffix = "_id";
            newEntityName = fatherEntityName + "_" + typeName;
        }

        //Spojne Typy
        if (jsonArray.get(0).isJsonObject()) {


            handleArrayOfObjects(fatherEntityName, fatherId, jsonArray, newEntityName);
            return;
        }

        DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();


        CouchEntitySchema couchEntitySchema = (CouchEntitySchema) findOrCreateMetaData(new CouchEntitySchema(newEntityName), cauchDataBase.getEntitiesSchema());
        CauchFieldSchema cauchFieldSchema = (CauchFieldSchema) findOrCreateMetaData(new CauchFieldSchema(typeName + suffix), couchEntitySchema.getEntityFields());
        CauchFieldSchema cauchIdFieldSchema = (CauchFieldSchema) findOrCreateMetaData(new CauchFieldSchema(fatherEntityName + "_id"), couchEntitySchema.getEntityFields());
        cauchIdFieldSchema.setMetaDataType(resolvePreMetaDataTypes(fatherId));
        couchEntitySchema.setFromArray(true);

        for (JsonElement element : jsonArray) {

            cauchFieldSchema.setMetaDataType(resolvePreMetaDataTypes(element));
            documentRowMetaData.setFieldValue(fatherEntityName + "_id", resolvePreMetaDataTypes(fatherId));
            documentRowMetaData.setFieldValue(typeName + suffix, resolvePreMetaDataTypes(element));
        }
    }

    private void handleArrayOfObjects(String fatherEntityName, JsonElement fatherId, JsonArray jsonArray, String newEntityName) {

        CouchEntitySchema couchEntitySchema = (CouchEntitySchema) findOrCreateMetaData(new CouchEntitySchema(newEntityName), cauchDataBase.getEntitiesSchema());
        CauchFieldSchema cauchFieldSchema = (CauchFieldSchema) findOrCreateMetaData(new CauchFieldSchema(fatherEntityName + "_id"), couchEntitySchema.getEntityFields());
        cauchFieldSchema.setMetaDataType(resolvePreMetaDataTypes(fatherId));
        cauchFieldSchema.keyChecker();
        for (JsonElement element : jsonArray) {
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            documentRowMetaData.setFieldValue(fatherEntityName + "_id", resolvePreMetaDataTypes(fatherId));
            getObjectsDataAndCreateSchema(element.getAsJsonObject(), newEntityName, documentRowMetaData);
        }
    }


}
