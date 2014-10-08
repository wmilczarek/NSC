package Converter.ModelController.Controller.DB.DocumentDB.Mongo;

import Converter.ConverterMetaDataModels.MongoModel.DocumentRowMetaData;
import Converter.ConverterMetaDataModels.MongoModel.TranslationEntitySchema;
import Converter.ConverterMetaDataModels.MongoModel.TranslationDataBase;
import Converter.ConverterMetaDataModels.MongoModel.TranslationFieldSchema;
import Converter.ModelController.Controller.DB.DocumentDB.DocumentDataBaseOperations;
import Converter.ModelController.Controller.DB.Translator.DocumentDBToSQL;
import Converter.ModelController.Controller.DB.Translator.IncompatibleFieldTypeConversionException;
import Converter.ModelController.HelperTypes.NullType;
import Converter.ModelController.Relations;
import Converter.ViewModel.DocumentTypesDB;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.changeName;
import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.dataArrayRelationNormalization;
import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;

public class MongoDataBaseOperations extends DocumentDataBaseOperations {

    private static final MongoDataBaseOperations ourInstance = new MongoDataBaseOperations();
    private MongoConnector mongoConnector;

    private MongoDataBaseOperations() {
    }

    public static MongoDataBaseOperations getInstance() {
        return ourInstance;
    }

    @Override
    public DocumentTypesDB getNoSqlType() {
        return DocumentTypesDB.MongoDB;
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


        //Load to memory dataBase
        loadIntoMemory(dbName);

        //resolveArraySchemas();

        changeName(this.dataBase);

        // findRelationAndSetRelations();
        //synchronizeKeys();



        for (TranslationEntitySchema name : this.dataBase.getEntitiesSchema()) {
            list.add(name.getEntityName());
        }

        dataArrayRelationNormalization(this.dataBase);


        translateFieldsOfAllEnteties();
        printMetaDataToSQL();

        return list;
    }

    private void translateFieldsOfAllEnteties() {

        for (TranslationEntitySchema translationEntitySchema : dataBase.getEntitiesSchema()) {

            for (TranslationFieldSchema translationFieldSchema : translationEntitySchema.getEntityFields()) {

                try {
                    translationFieldSchema.setSqlType(DocumentDBToSQL.documentToSqlFieldConversion(translationFieldSchema.getMetadataType()));
                } catch (IncompatibleFieldTypeConversionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void findRelationAndSetRelations() {

        // Dla każdego meta schematu
        for (TranslationEntitySchema translationEntitySchema : dataBase.getEntitiesSchema()) {

            // Dla każdego potencjalnego klucza obcego
            for (TranslationFieldSchema schemaField : translationEntitySchema.getBindableFields()) {

                // Sprawdz czy wpisuje się w konwencje
                if (schemaField.getFieldName().contains("_id")) {
                    //Sprawdz czy istnieje encja w relacji
                    //TODO:raportowanie bledow
                    String inRelationEntity = schemaField.getFieldName().replace("_id", "");
                    if (dataBase.checkIfEntityExists(inRelationEntity)) {
                        //Przypisz wlasciowsci relacj, pole klucza, typ relacji
                        schemaField.getRelationProperties().setRelations(Relations.ForeginKey);
                        schemaField.getRelationProperties().setFatherNames(inRelationEntity);
                        dataBase.appendEntitySchema(translationEntitySchema.getEntityName(), schemaField);
                    }
                }
            }
        }
    }


    public List<TranslationEntitySchema> resolveGetEntitesWithFieldsObjects(String dbName) throws UnknownHostException {
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

        for (TranslationEntitySchema ent : dataBase.getEntitiesSchema()) {

            if (ent.getEntityName().equals(entityName)) {

                List<List<String>> tableData = new ArrayList<List<String>>();
                List<String> row;

                for (TranslationFieldSchema field : ent.getEntityFields()) {
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
        TranslationEntitySchema translationEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(entityName), dataBase.getEntitiesSchema());

        if (existingMetaDataRow != null) {
            documentRowMetaData = existingMetaDataRow;

        } else {
            documentRowMetaData = new DocumentRowMetaData();
        }

        for (String fieldName : dbObject.keySet()) {

            TranslationFieldSchema translationFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(fieldName), translationEntitySchema.getEntityFields());
            translationFieldSchema.keyChecker();
            Object value = dbObject.get(fieldName);

            if (value == null) {
                value = new NullType();

            }
            // Dla zagnieżdzonego obiektu
            if (value.getClass() == BasicDBObject.class) {

                translationFieldSchema.setMetaDataType(BasicDBObject.class);
                String newFieldName = fieldName + "_id";
                TranslationFieldSchema newFkField = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(newFieldName), translationEntitySchema.getEntityFields());
                TranslationEntitySchema currentEntityField = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(fieldName), dataBase.getEntitiesSchema());

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
                translationFieldSchema.setMetaDataType(BasicDBList.class);
                translationFieldSchema.getRelationProperties().setRelations(Relations.None);

                continue;

            } else if (value.getClass() == DBRef.class) {

                translationFieldSchema.setMetaDataType(DBRef.class);
                String newFieldName = ((DBRef) value).getRef() + "_id";

                TranslationFieldSchema newFkField = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(newFieldName), translationEntitySchema.getEntityFields());
                newFkField.keyChecker();

                Object fkValue = ((DBRef) value).getId();
                newFkField.setMetaDataType(fkValue);
                documentRowMetaData.setFieldValue(newFieldName, fkValue);
            } else {

                //dodanie danych
                documentRowMetaData.setFieldValue(fieldName, value);
                // Dodanie typu
                translationFieldSchema.setMetaDataType(value);
            }
        }
        translationEntitySchema.setFieldData(documentRowMetaData);

    }

    private void resolveObjectForeginKeyRelation(DBObject dbObject, String fieldName, TranslationEntitySchema currentEntityField) throws UnknownHostException {

        // Jeśli nie ma domyślnej wartości ID, używaj wbudowanego auto inkrementującego się klucz.
        if (!this.hasDocumentAnyId(dbObject)) {

            TranslationEntitySchema translationEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(fieldName), dataBase.getEntitiesSchema());
            TranslationFieldSchema translationFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema("_id"), translationEntitySchema.getEntityFields());
            translationFieldSchema.setMetaDataType(Long.class);
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            documentRowMetaData.setFieldValue("_id", currentEntityField.getIncrementAutoPrimaryKey());
            translationFieldSchema.keyChecker();
            getObjectsDataAndCreateSchema((DBObject) dbObject.get(fieldName), fieldName, documentRowMetaData);


        } else {
            getObjectsDataAndCreateSchema((DBObject) dbObject.get(fieldName), fieldName, null);
        }
    }

    private TranslationFieldSchema resolveDBRef(BasicDBObject value) {

        return null;
    }

    private boolean isDBRefField(BasicDBObject value) {

        try {
            return value.get("$ref") != null;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    private Object tryToGetForeginKeyOfSubDocument(DBObject currentField, TranslationEntitySchema entity) {

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



        TranslationEntitySchema translationEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(newEntityName), dataBase.getEntitiesSchema());
        translationEntitySchema.setFromArray(true);

        TranslationFieldSchema translationFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(typeName + suffix), translationEntitySchema.getEntityFields());
        TranslationFieldSchema mongoIdFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(fatherEntityName + "_id"), translationEntitySchema.getEntityFields());
        mongoIdFieldSchema.setMetaDataType(fatherId);
        mongoIdFieldSchema.keyChecker();
        translationFieldSchema.keyChecker();

        for (Object element : currentList) {
            DocumentRowMetaData documentRowMetaData = new DocumentRowMetaData();
            translationFieldSchema.setMetaDataType(element);
            documentRowMetaData.setFieldValue(fatherEntityName + "_id", fatherId);
            documentRowMetaData.setFieldValue(typeName + suffix, element);
            translationEntitySchema.setFieldData(documentRowMetaData);
        }



    }

    private void handleArrayOfObjects(String fatherEntityName, Object fatherId, BasicDBList currentList, String newEntityName) {

        TranslationEntitySchema translationEntitySchema = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(newEntityName), dataBase.getEntitiesSchema());
        TranslationFieldSchema translationFieldSchema = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(fatherEntityName + "_id"), translationEntitySchema.getEntityFields());
        translationFieldSchema.setMetaDataType(fatherId.getClass());
        translationFieldSchema.keyChecker();
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
        dataBase = new TranslationDataBase();
        try {
            DB dataBase = mongoConnector.getInstance().getDB(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        dataBase.setDBname(dbName);
        try {
            this.resolveGetEntitesWithFieldsObjects(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
