package Converter.ModelController.Controller.DB.NoSQL.Mongo;

import Converter.ModelController.Controller.DB.NoSQL.NoSQLDataBaseOperations;
import Converter.ModelController.Controller.DB.Translator.IncompatibleFieldTypeConversionException;
import Converter.ModelController.Controller.DB.Translator.MongoToSQL;
import Converter.ModelController.HelperTypes.NullType;
import Converter.ModelController.*;
import Converter.ModelController.MongoModel.*;
import Converter.ViewModel.NoSQLTypes;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Converter.ModelController.Controller.DB.NoSQL.Mongo.MongoOperationUtils.changeName;
import static Converter.ModelController.Controller.DB.NoSQL.Mongo.MongoOperationUtils.createForeginKey;
import static Converter.ModelController.Controller.DB.Relational.Operations.RelationalDataBaseOperations.*;


public class MongoDataBaseOperations extends NoSQLDataBaseOperations {

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

    // 1: wejsc do kazdej kolekcji
    // 2: wziasc z niej roota- najbardziej bogatego -> //fielda ktory jest obietem po encji rozrozniamy
    // 3: dokonac resolvowania !!!!

    @Override
    public List<String> loadDataBase(String dbName) throws UnknownHostException, SQLException {
        //Set<String> colls = mongoConnector.db.getCollectionNames();

        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        List<String> list = new ArrayList<String>();


        //Load to memory DataBase
        loadIntoMemory(dbName);

        resolveArraySchemas();

        changeName(mongoData);

        findRelationAndSetRelations();
        synchronizeKeys();

        translateFieldsOfAllEnteties();

        for (MongoEntitySchema name : mongoData.getEntitiesSchema()) {
            list.add(name.getEntityName());
        }

        createEntitiesSchemaFromMongo(mongoData);
        createSQLReferences(mongoData);
        createSQLInsert(mongoData);


        return list;
    }

    private void translateFieldsOfAllEnteties() {

        for (MongoEntitySchema mongoEntitySchema : mongoData.getEntitiesSchema()) {

            for (MongoFieldSchema mongoFieldSchema : mongoEntitySchema.getEntityFields()) {

                try {
                    mongoFieldSchema.setSqlType(MongoToSQL.mongoToSqlFieldConversion(mongoFieldSchema.getTypesFromMongoApi()));
                } catch (IncompatibleFieldTypeConversionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void resolveArraySchemas() {

        for (MongoArraySchema mongoArraySchema : mongoData.getArraySchema()) {

            if (mongoData.findEntity(mongoArraySchema.getArrayName().replace("_id", "")) == null) {
                mongoData.importSchemaFromArray(mongoArraySchema);
            } else {

                mongoData.createInstersectionEntitie(mongoArraySchema);

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

    private void synchronizeKeys() {

        // Dla każdego meta schematu
        for (MongoEntitySchema mongoEntitySchema : mongoData.getEntitiesSchema()) {

            // Dla każdego potencjalnego klucza obcego
            for (MongoFieldSchema schemaField : mongoEntitySchema.getForeginKeyFields()) {

                //Klucze powinny miec synchronizowane typy
                String primaryKeyOwner = schemaField.getFieldName().replace("_id", "");
                schemaField.setMongoType(mongoData.findEntity(primaryKeyOwner).findCreateField(primaryKeyOwner + "_PK").getMongoType());
                mongoData.findEntity(primaryKeyOwner).findCreateField(primaryKeyOwner + "_PK").addMongoType(schemaField.getMongoType());
            }
        }

    }

    public List<MongoEntitySchema> resolveGetEntitesWithFieldsObjects(String dbName) throws UnknownHostException {
        DB dataBase = mongoConnector.getInstance().getDB(dbName);

        for (String name : dataBase.getCollectionNames()) {
            getObjectsDataAndCreateSchema(dbName, name);
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

    // Iterowanie po Kolekcjach

    public void getObjectsDataAndCreateSchema(String dbName, String entityName) throws UnknownHostException {

        if (entityName.equals("system.indexes")) {
            return;
        }

        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        DBCursor result = dataBase.getCollection(entityName).find();
        MongoEntitySchema entity = mongoData.getEntitySchemaToEdit(entityName);
        MongoEntityData dataEntity = mongoData.getEntityDataToEdit(entityName);

        // ITEROWANIE SIE PO WSZYSTKICH KOLEKCJACH
        for (DBObject current : result) {

            MongoRowData row = new MongoRowData();

            for (String fieldName : current.keySet()) {

                getFieldPropertiesToMemory(entityName, fieldName, current, row);
            }
            //zapis danych
            mongoData.appendEntityData(entityName, row);
        }
    }

    private boolean checkIfFieldIsEntityTypeIsArray(Object fieldType) {

        if (fieldType.getClass() == BasicDBList.class) {
            return true;
        }

        return false;
    }

    private boolean checkIfFieldIsEntityTypeIsObject(Object fieldType) {

        if (fieldType.getClass() == BasicDBObject.class) {
            return true;
        }

        return false;
    }

    private Relations initialPrimaryKeyRelationChecker(MongoFieldSchema field) {

        if (field.getFieldName().equals("_id")) {

            return Relations.PrimaryKey;
        } else {

            return Relations.None;
        }
    }

    // rekurencja
    private void createEntityFromSubDocument(String fieldName, DBObject current) {

        MongoRowData row = new MongoRowData();

        // CZY JEST WYMAGANE GENEROWANIE AUTO KLUCZA
        if (mongoData.getEntitySchemaToEdit(fieldName).getAutoPrimaryKey() > 0) {
            MongoEntitySchema entity = mongoData.getEntitySchemaToEdit(fieldName);
            MongoFieldSchema field = entity.findCreateField("_id");
            field.setTypesFromMongoApi(Long.class);
            field.getRelationProperties().setRelations(initialPrimaryKeyRelationChecker(field));
            //nazwa pola jest tutaj nazwa encji
            mongoData.appendEntitySchema(fieldName, field);

            row.setFieldValue("_id", mongoData.getEntitySchemaToEdit(fieldName).getAutoPrimaryKey());

        }


        //Sprawdzanie po polach nowego obiektu
        for (String objFieldName : current.keySet()) {
            row = getFieldPropertiesToMemory(fieldName, objFieldName, current, row);
        }
        //ZAPISZ WIERSZA DANYCH
        mongoData.appendEntityData(fieldName, row);
    }

    private MongoRowData getFieldPropertiesToMemory(String entityName, String fieldName, DBObject current, MongoRowData row) {

        MongoEntitySchema entity = mongoData.getEntitySchemaToEdit(entityName);
        Object value = current.get(fieldName);
        // Szukanie/tworzenie nowego fielda o podanej nazwie

        MongoFieldSchema field = entity.findCreateField(fieldName);
        if (value == null) {
            value = new NullType(); // TODO PORPAWAWS

        } else if (checkIfFieldIsEntityTypeIsObject(value)) {
            handleObjectFieldOneToOneRelation(entityName, fieldName, current, row, field);
            field.setTypesFromMongoApi(DBObject.class);

        } else if (checkIfFieldIsEntityTypeIsArray(value)) {
            handleArrayField(fieldName, entityName, (BasicDBList) value, current.get("_id"));
            field.setTypesFromMongoApi(BasicDBList.class);
            field.getRelationProperties().setRelations(Relations.None);

            return null;

        } else {
            // To jest dla FK

            //dodanie danych
            row.setFieldValue(fieldName, value);
            // Dodanie typu
            field.setTypesFromMongoApi(value);
            // Dodanie relacji
            field.getRelationProperties().setRelations(initialPrimaryKeyRelationChecker(field));
        }

        //Podamiana pola na nowszą wersje.
        mongoData.appendEntitySchema(entityName, field);

        return row;

    }

    // Sprawdz czy jest to tablica obiektow
    private void handleArrayField(String fieldName, String fatherEntity, BasicDBList current, Object fatherId) {

        if (current.get(0).getClass() == BasicDBObject.class) {
            MongoEntitySchema entity = mongoData.getEntitySchemaToEdit(fieldName);
            entity.appendEntityFields(createForeginKey(fatherEntity));

            // ITEROWANIE SIE PO WSZYSTKICH KOLEKCJACH
            for (Object ArrayObject : current.toArray()) {
                DBObject currentArrayObject = (DBObject) ArrayObject;

                MongoRowData row = new MongoRowData();

                for (String currentObjectField : currentArrayObject.keySet()) {

                    getFieldPropertiesToMemory(fieldName, currentObjectField, currentArrayObject, row);
                    row.setFieldValue(fatherEntity + "_id", fatherId);
                }
                //zapis danych
                mongoData.appendEntityData(fieldName, row);
            }

            return;
        }

        handleArrayFieldIfArray(fieldName, fatherEntity, current, fatherId);
    }

    private void handleArrayFieldIfArray(String fieldName, String fatherEntity, BasicDBList current, Object fatherId) {

        MongoArraySchema array = mongoData.getArraySchema(fieldName);
        MongoFieldSchema mongoFieldSchemaId = createForeginKey(fatherEntity);

        MongoFieldSchema mongoFieldSchemaValue = new MongoFieldSchema();
        mongoFieldSchemaValue.setTypesFromMongoApi(current.get(0));
        mongoFieldSchemaValue.setFieldName(fieldName + "value");

        array.appendArrayFields(mongoFieldSchemaId);
        array.appendArrayFields(mongoFieldSchemaValue);

        mongoData.appendIfNotExistsArraySchema(array);


        for (Object value : current) {
            MongoRowData arrayRow = new MongoRowData();
            arrayRow.setFieldValue(fieldName + "value", value);
            arrayRow.setFieldValue(fatherEntity + "_id", fatherId);
            mongoData.appendArrayRowData(fieldName, arrayRow);
        }


        //createEntityFromSubDocument(dbName, fieldName, (DBObject) current.get(fieldName));


    }

    private void handleObjectFieldOneToOneRelation(String entityName, String fieldName, DBObject current, MongoRowData row, MongoFieldSchema field) {

        DBObject children = ((DBObject) current.get(fieldName));

        if (children.get("_id") == null) {
            field.setTypesFromMongoApi(Long.class);
            MongoEntitySchema subEntity = mongoData.getEntitySchemaToEdit(fieldName);
            mongoData.appendIfNotExistsEntitySchema(subEntity);
            row.setFieldValue(fieldName + "_id", subEntity.incrementAutoPrimaryKey());

        } else {
            //get sub document
            DBObject subDocumentIdField = (DBObject) children.get("_id");
            // Zapisz typ ID pod dokumentu.
            field.setTypesFromMongoApi(subDocumentIdField);
            // Zapisz wartość PK pod dokumentu do pola tej encji (FK).
            row.setFieldValue(fieldName, subDocumentIdField.get("_id"));
            // Ustaw relacje.

        }

        //Tworzenie FK dla obiektu nadrzednego
        field.setFieldName(fieldName + "_id");
        field.getRelationProperties().setFatherNames(entityName);
        field.getRelationProperties().setRelations(Relations.ForeginKey);

        createEntityFromSubDocument(fieldName, (DBObject) current.get(fieldName));
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
