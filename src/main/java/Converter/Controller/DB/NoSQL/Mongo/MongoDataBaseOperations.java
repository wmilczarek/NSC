package Converter.Controller.DB.NoSQL.Mongo;

import Converter.Controller.DB.NoSQL.NoSQLDataBaseOperations;
import Converter.Controller.DB.Translator.MongoToSQL;
import Converter.ViewModel.*;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Converter.Controller.DB.Relational.Operations.RelationalDataBaseOperations.createEntitiesSchemaFromMongo;
import static Converter.Controller.DB.Relational.Operations.RelationalDataBaseOperations.createSQLInsert;


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

        translateFieldsOfAllEnteties();

        findRelationAndSetRelations();

        for (MongoEntitySchema name : mongoData.getEntitiesSchema()) {
            list.add(name.getEntityName());
        }

        createEntitiesSchemaFromMongo(mongoData);
        createSQLInsert(mongoData);

        return list;
    }

    private void translateFieldsOfAllEnteties() {

        for (MongoEntitySchema mongoEntitySchema : mongoData.getEntitiesSchema()) {

            for (MongoFieldSchema mongoFieldSchema : mongoEntitySchema.getEntityFields()) {

                mongoFieldSchema.setSqlType(MongoToSQL.mongoToSqlFieldConversion(mongoFieldSchema.getTypesFromMongoApi()));
                //mongoEntitySchema.appendEntityFields(mongoFieldSchema);
            }

           // mongoData.appendIfExistsEntitySchema(mongoEntitySchema);

        }

        //TODO: translate fields and create
    }

    private void resolveArraySchemas() {

        for (MongoArraySchema mongoArraySchema : mongoData.getArraySchema()) {

            if (mongoData.findEntity(mongoArraySchema.getArrayName().replace("_id", "")) == null) {
                mongoData.importSchemaFromArray(mongoArraySchema);
            } else {

                mongoData.createInstersectionEntitie(mongoArraySchema);
            }
        }
        //TODO: arra_id -> jako relacja, arra -> jako osobna encja.
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
                        //TODO:co jesli nie ma klcza?
                        Relations relation = Relations.ForeginKey;
                        relation.setInRealtaionField(inRelationEntity);
                        schemaField.setRelations(relation);
                        mongoData.appendEntitySchema(mongoEntitySchema.getEntityName(), schemaField);

                        //Klucze powinny miec synchronizowane typy
                        schemaField.setMongoType(mongoData.findEntity(inRelationEntity).findCreateField("_id").getMongoType());
                        mongoData.findEntity(inRelationEntity).findCreateField("_id").addMongoType(schemaField.getMongoType());

                    }

                }
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
                    row.add("tutaj no sql type");
                    //TODO: null pointer exception
                    row.add(field.getRelations().toString());
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

        // List<MongoRowData> mongoRowData = new ArrayList<MongoRowData>();

        for (DBObject current : result) {

            MongoRowData row = new MongoRowData();

            for (String fieldName : current.keySet()) {

                getFieldPropertiesToMemory(dbName, entityName, fieldName, current, row);
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
    private void createEntityFromSubDocument(String dbName, String fieldName, DBObject current) {

        MongoRowData row = new MongoRowData();

        // CZY JEST WYMAGANE GENEROWANIE AUTO KLUCZA
        if (mongoData.getEntitySchemaToEdit(fieldName).getAutoPrimaryKey() > 0) {
            MongoEntitySchema entity = mongoData.getEntitySchemaToEdit(fieldName);
            MongoFieldSchema field = entity.findCreateField("_id");
            field.setTypesFromMongoApi(Long.class);
            field.setRelations(initialPrimaryKeyRelationChecker(field));
            //nazwa pola jest tutaj nazwa encji
            mongoData.appendEntitySchema(fieldName, field);

            row.setFieldValue("_id", mongoData.getEntitySchemaToEdit(fieldName).getAutoPrimaryKey());

        }


        //Sprawdzanie po polach nowego obiektu
        for (String objFieldName : current.keySet()) {
            row = getFieldPropertiesToMemory(dbName, fieldName, objFieldName, current, row);
        }
        //ZAPISZ WIERSZA DANYCH
        mongoData.appendEntityData(fieldName, row);
    }

    private MongoRowData getFieldPropertiesToMemory(String dbName, String entityName, String fieldName, DBObject current, MongoRowData row) {

        MongoEntitySchema entity = mongoData.getEntitySchemaToEdit(entityName);
        // Szukanie/tworzenie nowego fielda o podanej nazwie

        MongoFieldSchema field = entity.findCreateField(fieldName);

        if (checkIfFieldIsEntityTypeIsObject(current.get(fieldName))) {

            handleObjectField(dbName, entityName, fieldName, current, row, field);
            field.setTypesFromMongoApi(DBObject.class);

        } else if (checkIfFieldIsEntityTypeIsArray(current.get(fieldName))) {
            handleArrayField(fieldName, entityName, (BasicDBList) current.get(fieldName), current.get("_id"));
            field.setTypesFromMongoApi(BasicDBList.class);
            field.setRelations(Relations.None);
            return null;
        } else {
            //dodanie danych
            row.setFieldValue(fieldName, current.get(fieldName));
            // Dodanie typu
            field.setTypesFromMongoApi(current.get(fieldName));
            // Dodanie relacji
            field.setRelations(initialPrimaryKeyRelationChecker(field));
        }

        //Podamiana pola na nowszą wersje.
        mongoData.appendEntitySchema(entityName, field);

        return row;

    }

    private void handleArrayField(String fieldName, String fatherEntity, BasicDBList current, Object fatherId) {


        MongoArraySchema array = mongoData.getArraySchema(fieldName);
        array.setFatherName(fatherEntity);
        array.setValueType(current.get(0));
        array.setFatherIdValueType(fatherId);
        mongoData.appendIfNotExistsArraySchema(array);


        for (Object value : current) {
            MongoRowData arrayRow = new MongoRowData();
            arrayRow.setFieldValue("value", value);
            arrayRow.setFieldValue(fatherEntity + "_id", fatherId);
            mongoData.appendArrayRowData(fieldName, arrayRow);
        }


        //createEntityFromSubDocument(dbName, fieldName, (DBObject) current.get(fieldName));


    }

    private void handleObjectField(String dbName, String entityName, String fieldName, DBObject current, MongoRowData row, MongoFieldSchema field) {

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
        field.setFieldName(fieldName + "_id");
        Relations foreginRelations = Relations.ForeginKey;
        foreginRelations.setInRelationEntity(entityName);
        field.setRelations(Relations.ForeginKey);

        createEntityFromSubDocument(dbName, fieldName, (DBObject) current.get(fieldName));
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
