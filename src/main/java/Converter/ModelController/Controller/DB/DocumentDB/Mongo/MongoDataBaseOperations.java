package Converter.ModelController.Controller.DB.DocumentDB.Mongo;

import Converter.ConverterMetaDataModels.MongoModel.DocumentRowMetaData;
import Converter.ConverterMetaDataModels.MongoModel.MongoDataBase;
import Converter.ConverterMetaDataModels.MongoModel.MongoEntitySchema;
import Converter.ConverterMetaDataModels.MongoModel.MongoFieldSchema;
import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;
import Converter.ModelController.Controller.DB.Translator.DocumentDBToSQL;
import Converter.ModelController.Controller.DB.Translator.IncompatibleFieldTypeConversionException;
import Converter.ModelController.HelperTypes.NullType;
import Converter.ModelController.Relations;
import Converter.ViewModel.NoSQLTypes;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.dataArrayRelationNormalization;
import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;
import static Converter.ModelController.Controller.DB.DocumentDB.Mongo.MongoOperationUtils.changeName;

import static Converter.ModelController.Controller.DB.Relational.Operations.RelationalDataBaseOperations.*;


public class MongoDataBaseOperations extends DocumentDataBaseOperations {

    private static final MongoDataBaseOperations ourInstance = new MongoDataBaseOperations();
    private MongoConnector mongoConnector;
    private MongoDataBase mongoData;

    private MongoDataBaseOperations() {
    }

    public static MongoDataBaseOperations getInstance() {
        return ourInstance;
    }

    @Override
    public NoSQLTypes getNoSqlType() {
        return NoSQLTypes.MongoDB;
    }

    @Override
    public List<String> GetDataBaseNames() {
        return mongoConnector.getInstance().getMongoClient(getNoSqlType()).getDatabaseNames();

    }

    @Override
    public List<String> loadDataBase(String dbName) throws UnknownHostException, SQLException {
        //Set<String> colls = mongoConnector.db.getCollectionNames();

        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        List<String> list = new ArrayList<String>();


        //Load to memory DataBase
        loadIntoMemory(dbName);

        //resolveArraySchemas();

        changeName(mongoData);

        // findRelationAndSetRelations();
        //synchronizeKeys();



        for (MongoEntitySchema name : mongoData.getEntitiesSchema()) {
            list.add(name.getEntityName());
        }

        dataArrayRelationNormalization(mongoData);


        translateFieldsOfAllEnteties();
        createEntitiesSchemaScript(mongoData);
        createSQLReferencesScript(mongoData);
        createSQLInsertScript(mongoData);


        return list;
    }

    private void translateFieldsOfAllEnteties() {

        for (MongoEntitySchema mongoEntitySchema : mongoData.getEntitiesSchema()) {

            for (MongoFieldSchema mongoFieldSchema : mongoEntitySchema.getEntityFields()) {

                try {
                    mongoFieldSchema.setSqlType(DocumentDBToSQL.documentToSqlFieldConversion(mongoFieldSchema.getMetadataType()));
                } catch (IncompatibleFieldTypeConversionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void findRelationAndSetRelations() {

        // Dla każdego meta schematu
        for (MongoEntitySchema mongoEntitySchema : mongoData.getEntitiesSchema()) {

            // Dla każdego potencjalnego klucza obcego
            for (MongoFieldSchema schemaField : mongoEntitySchema.getBindableFields()) {

                // Sprawdz czy wpisuje się w konwencje
                if (schemaField.getFieldName().contains("_id")) {
                    //Sprawdz czy istnieje encja w relacji
                    //TODO:raportowanie bledow
                    String inRelationEntity = schemaField.getFieldName().replace("_id", "");
                    if (mongoData.checkIfEntityExists(inRelationEntity)) {
                        //Przypisz wlasciowsci relacj, pole klucza, typ relacji
                        schemaField.getRelationProperties().setRelations(Relations.ForeginKey);
                        schemaField.getRelationProperties().setFatherNames(inRelationEntity);
                        mongoData.appendEntitySchema(mongoEntitySchema.getEntityName(), schemaField);
                    }
                }
            }
        }
    }


    public List<MongoEntitySchema> resolveGetEntitesWithFieldsObjects(String dbName) throws UnknownHostException {
        DB dataBase = mongoConnector.getInstance().getDB(dbName);

        for (String name : dataBase.getCollectionNames()) {

            if (name.equals("system.indexes")) {
                continue;
            }

            DBCursor result = dataBase.getCollection(name).find();

            for (DBObject current : result) {

                getObjectsDataAndCreateSchema(current, name, null);
            }
        }
        return null;
    }

    // znajdz wszystkie TYPY DANEGO POLA...

    // dictionary - Filed Name, All Types
    @Override
    public List<List<String>> showFields(String dbName, String entityName) throws UnknownHostException {

        if (dbName == null || dbName.equals("") || entityName == null || entityName.equals("")) {
            return null;
        }

        for (MongoEntitySchema ent : mongoData.getEntitiesSchema()) {

            if (ent.getEntityName().equals(entityName)) {

                List<List<String>> tableData = new ArrayList<List<String>>();
                List<String> row;

                for (MongoFieldSchema field : ent.getEntityFields()) {
                    row = new ArrayList<String>();
                    row.add(field.getFieldName());
                    row.add(field.getSqlType().toString());
                    row.add(field.getRelationProperties().getRelations().toString() + " " + field.getRelationProperties().getFatherNames());
                    tableData.add(row);
                }
                return tableData;
            }
        }

        return null;
    }


    public void getObjectsDataAndCreateSchema(DBObject dbObject, String entityName, DocumentRowMetaData existingMetaDataRow) throws UnknownHostException {

        DocumentRowMetaData documentRowMetaData;
        MongoEntitySchema mongoEntitySchema = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(entityName), mongoData.getEntitiesSchema());

        if (existingMetaDataRow != null) {
            documentRowMetaData = existingMetaDataRow;

        } else {
            documentRowMetaData = new DocumentRowMetaData();
        }

        for (String fieldName : dbObject.keySet()) {

            MongoFieldSchema mongoFieldSchema = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(fieldName), mongoEntitySchema.getEntityFields());
            Object value = dbObject.get(fieldName);

            if (value == null) {
                value = new NullType();

            }
            // Dla zagnieżdzonego obiektu
            if (value.getClass() == BasicDBObject.class) {

                mongoFieldSchema.setMetaDataType(BasicDBObject.class);
                String newFieldName = fieldName + "_id";
                MongoFieldSchema newFkField = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(newFieldName), mongoEntitySchema.getEntityFields());
                MongoEntitySchema currentEntityField = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(fieldName), mongoData.getEntitiesSchema());

                //Postaraj się wziąść wartość klucza obcego z id dokumentu zagnieżdżonego
                Object fkValue = this.tryToGetForeginKeyOfSubDocument((DBObject) dbObject.get(fieldName), currentEntityField);
                newFkField.setMetaDataType(fkValue);
                documentRowMetaData.setFieldValue(newFieldName, fkValue);

                resolveObjectForeginKeyRelation(dbObject, fieldName, currentEntityField);
                newFkField.keyChecker();
                continue;

            } else if (value.getClass() == BasicDBList.class) {
                //TODO: A co jesli nie ma ID
                handleArrayAsEntity(fieldName, entityName, dbObject.get("_id"), (BasicDBList) value);
                mongoFieldSchema.setMetaDataType(BasicDBList.class);
                mongoFieldSchema.getRelationProperties().setRelations(Relations.None);

                continue;

            } else if (value.getClass() == DBRef.class) {

                mongoFieldSchema.setMetaDataType(DBRef.class);
                String newFieldName = ((DBRef) value).getRef() + "_id";

                MongoFieldSchema newFkField = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(newFieldName), mongoEntitySchema.getEntityFields());
                newFkField.keyChecker();

                Object fkValue = ((DBRef) value).getId();
                newFkField.setMetaDataType(fkValue);
                documentRowMetaData.setFieldValue(newFieldName, fkValue);
            } else {

                //dodanie danych
                documentRowMetaData.setFieldValue(fieldName, value);
                // Dodanie typu
                mongoFieldSchema.setMetaDataType(value);
                // Dodanie relacji
                mongoFieldSchema.keyChecker();
                mongoFieldSchema.keyChecker();
            }
        }
        mongoEntitySchema.setMongoFieldData(documentRowMetaData);

    }

    private void resolveObjectForeginKeyRelation(DBObject dbObject, String fieldName, MongoEntitySchema currentEntityField) throws UnknownHostException {

        // Jeśli nie ma domyślnej wartości ID, używaj wbudowanego auto inkrementującego się klucz.
        if (!this.hasDocumentAnyId(dbObject)) {

            MongoEntitySchema mongoEntitySchema = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(fieldName), mongoData.getEntitiesSchema());
            MongoFieldSchema mongoFieldSchema = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema("_id"), mongoEntitySchema.getEntityFields());
            mongoFieldSchema.setMetaDataType(Long.class);
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            documentRowMetaData.setFieldValue("_id", currentEntityField.getIncrementAutoPrimaryKey());
            mongoFieldSchema.keyChecker();
            getObjectsDataAndCreateSchema((DBObject) dbObject.get(fieldName), fieldName, documentRowMetaData);


        } else {
            getObjectsDataAndCreateSchema((DBObject) dbObject.get(fieldName), fieldName, null);
        }
    }

    private MongoFieldSchema resolveDBRef(BasicDBObject value) {

        return null;
    }

    private boolean isDBRefField(BasicDBObject value) {

        try {
            return value.get("$ref") != null;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    private Object tryToGetForeginKeyOfSubDocument(DBObject currentField, MongoEntitySchema entity) {

        // jeśli poddokument nie posiada klucza _id stworz go!
        try {
            return currentField.get("_id").toString();
        } catch (Exception e) {
            return entity.getIncrementAutoPrimaryKey();
        }
    }

    private boolean hasDocumentAnyId(DBObject currentField) {

        // jeśli poddokument nie posiada klucza _id stworz go!
        try {
            return currentField.get("_id") != null;
        } catch (Exception e) {
            return false;
        }
    }


    private void handleArrayAsEntity(String typeName, String fatherEntityName, Object fatherId, BasicDBList currentList) {

        String suffix = "_value";
        String newEntityName = typeName;

        if (typeName.endsWith("_id")) {
            suffix = "_id";
            newEntityName = fatherEntityName + "_" + typeName;
        }

        //Przypadek tablicy dokumentów
        if (currentList.get(0).getClass() == BasicDBObject.class) {

            //przypadek tablicy obiektów
            handleArrayOfObjects(fatherEntityName, fatherId, currentList, newEntityName);
            return;
        }



        MongoEntitySchema mongoEntitySchema = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(newEntityName), mongoData.getEntitiesSchema());
        mongoEntitySchema.setFromArray(true);

        MongoFieldSchema mongoFieldSchema = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(typeName + suffix), mongoEntitySchema.getEntityFields());
        MongoFieldSchema mongoIdFieldSchema = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(fatherEntityName + "_id"), mongoEntitySchema.getEntityFields());
        mongoIdFieldSchema.setMetaDataType(fatherId);

        for (Object element : currentList) {
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            mongoFieldSchema.setMetaDataType(element);
            documentRowMetaData.setFieldValue(fatherEntityName + "_id", fatherId);
            documentRowMetaData.setFieldValue(typeName + suffix, element);
            mongoEntitySchema.setMongoFieldData(documentRowMetaData);
        }



    }

    private void handleArrayOfObjects(String fatherEntityName, Object fatherId, BasicDBList currentList, String newEntityName) {

        MongoEntitySchema mongoEntitySchema = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(newEntityName), mongoData.getEntitiesSchema());
        MongoFieldSchema mongoFieldSchema = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(fatherEntityName + "_id"), mongoEntitySchema.getEntityFields());
        mongoFieldSchema.setMetaDataType(fatherId.getClass());
        mongoFieldSchema.keyChecker();
        for (Object element : currentList.toArray()) {
            try {
                DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
                documentRowMetaData.setFieldValue(fatherEntityName + "_id", fatherId);
                getObjectsDataAndCreateSchema((BasicDBObject) element, newEntityName, documentRowMetaData);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadIntoMemory(String dbName) {
        mongoData = new MongoDataBase();
        try {
            DB dataBase = mongoConnector.getInstance().getDB(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mongoData.setDBname(dbName);
        try {
            this.resolveGetEntitesWithFieldsObjects(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
