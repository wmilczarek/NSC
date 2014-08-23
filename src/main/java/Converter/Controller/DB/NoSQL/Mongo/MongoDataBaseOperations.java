package Converter.Controller.DB.NoSQL.Mongo;

import Converter.Controller.DB.NoSQL.NoSQLDataBaseOperations;
import Converter.Controller.DB.Translator.MongoToSQL;
import Converter.ViewModel.*;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;


public class MongoDataBaseOperations extends NoSQLDataBaseOperations{

    private static final MongoDataBaseOperations ourInstance = new MongoDataBaseOperations();

    public static MongoDataBaseOperations getInstance() {
        return ourInstance;
    }

    @Override
    public NoSQLTypes getNoSqlType(){
        return NoSQLTypes.MongoDB;
    }

    private MongoConnector mongoConnector;

    private MongoDataBase mongoData;

    private MongoEntity mongoTempEntity;

    private Set<Set<BasicDBObject>> subDocuments = new HashSet<Set<BasicDBObject>>();

    private Set<Set<BasicDBList>> dbList = new HashSet<Set<BasicDBList>>();

    private MongoDataBaseOperations() {}


    @Override
    public List<String> GetDataBaseNames()
    {
        return mongoConnector.getInstance().getMongoClient(getNoSqlType()).getDatabaseNames();

    }

    // 1: wejsc do kazdej kolekcji
    // 2: wziasc z niej roota- najbardziej bogatego -> //fielda ktory jest obietem po encji rozrozniamy
    // 3: dokonac resolvowania !!!!

    @Override
    public List<String> loadDataBase(String dbName) throws UnknownHostException {
        //Set<String> colls = mongoConnector.db.getCollectionNames();

        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        List<String> list = new ArrayList<String>();
        list.addAll(dataBase.getCollectionNames());

        //Load to memory DataBase
        loadIntoMemory(dbName);
       // TranslateFieldsOfAllEntieries(mongoData);
        FindRelations();
        

   /*     try {
            //createEntitiesFromMongo(mongoData);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        return list;
    }

    private void translateFieldsOfAllEntieries(MongoDataBase mongoDataBase) {

        for(MongoEntity mongoEntity:mongoDataBase.getEntities()){

            for (MongoField field:mongoEntity.getEntityFields().keySet()){

                Map<MongoField, SqlFieldType> test = mongoEntity.getEntityFields();

                test.put(field, MongoToSQL.mongoToSqlFieldConversion(field.getMongoType()));

                mongoEntity.setEntityFields(test);
            }

        }

    }

    private void FindRelations() {
    }

    public List<MongoEntity> ResolveGetEntitesWithFieldsObjects(String dbName) throws UnknownHostException {
        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        List<MongoEntity> list = new ArrayList<MongoEntity>();

        MongoEntity entity;
        for(String name:dataBase.getCollectionNames()){

            entity = new MongoEntity();
            entity.setEntityName(name);
            entity.setEntityFields(ResolveAndGetFieldObjects(dbName, name));
            list.add(entity);
        }

        return list;
    }

    // znajdz wszystkie TYPY DANEGO POLA...

    // dictionary - Filed Name, All Types
    @Override
    public List<List<String>> showFields(String dbName, String entityName) throws UnknownHostException {

        if(dbName == null || dbName.equals("") || entityName == null || entityName.equals("")){
            return null;
        }

        for(MongoEntity ent:mongoData.getEntities()){

            if(ent.getEntityName().equals(entityName)){

                List<List<String>> tableData = new ArrayList<List<String>>();
                List<String> row;

                for(Map.Entry<MongoField,SqlFieldType> field:ent.getEntityFields().entrySet())
                {
                    row = new ArrayList<String>();
                    row.add(field.getKey().getFieldName());
                    row.add(field.getValue().toString());
                    row.add(field.getKey().getRelations().toString());
                    tableData.add(row);
                }
                return tableData;
            }
        }

        return null;
    }

    public List<String> showTypes(String entityName, String fieldName){
        return null;
    }

    public Map<MongoField,SqlFieldType> ResolveAndGetFieldObjects(String dbName, String EntityName) throws UnknownHostException {

        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        DBCollection targetCollection = dataBase.getCollection(EntityName);

        String map = "function() { for (var key in this) { emit(key, null); }}";
        String reduce = "function(key, stuff) { return null; }";
        MapReduceCommand cmd = new MapReduceCommand(targetCollection, map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
        MapReduceOutput out = targetCollection.mapReduce(cmd);

        Iterable<DBObject> results = out.results();
        Map<MongoField,SqlFieldType> fieldsDictionary = new HashMap<MongoField, SqlFieldType>();

        for(DBObject obj:results){

            // TODO: determin type
            MongoField field = new MongoField();
            field.setFieldName((String)obj.get("_id"));

            if(field.getFieldName().equals("_id")){
                field.setRelations(Relations.PrimaryKey);
            } else {
                field.setRelations(Relations.None);
            }

            // Dla każdego pola przeprowadź badanie
            field.setMongoType(ResolveAndGetFieldTypes(dbName, EntityName, (String) obj.get("_id")));
            field.setValue(obj.get("value"));
            fieldsDictionary.put(field,MongoToSQL.mongoToSqlFieldConversion(field.getMongoType()));
            //BasicDBList, BasicDbObject
        }


        return fieldsDictionary;
    }

    // znajdz wszystkie TYPY DANEGO POLA...
    public Set<Class<?>> ResolveAndGetFieldTypes(String dbName, String EntityName, String fieldName) throws UnknownHostException {

        DB dataBase = mongoConnector.getInstance().getDB(dbName);
        DBCollection targetCollection = dataBase.getCollection(EntityName);

        DBObject query = new BasicDBObject(fieldName, new BasicDBObject("$exists", true));
        DBCursor results = targetCollection.find(query);
        Set<Class<?>> type = new HashSet<Class<?>>();
        Set<BasicDBObject> subDocumentsInOneEntity = new HashSet<BasicDBObject>();
        Set<BasicDBList> documentListInOneEntity = new HashSet<BasicDBList>();

        for(DBObject obj:results){

            if(obj.get(fieldName).getClass() == BasicDBList.class ){

                // pobranie od dokumentu podrzędnego ID
                type.add(obj.get("_id").getClass());
                //createEntityFromSubDocument()


                //documentListInOneEntity.add((BasicDBList) obj);
                //createSemiEntity(dbName, EntityName, fieldName, obj)

            }  if(obj.get(fieldName).getClass() == BasicDBObject.class ){
                //subDocumentsInOneEntity.add((BasicDBObject) obj);

                //trzeba stworzyc encje z ID.

            } else {
                type.add(obj.get(fieldName).getClass());
            }
        }

        subDocuments.add(subDocumentsInOneEntity);
        return type;
    }

    private void createEntityFromSubDocument(String dbName, String entityName, String fieldName, DBObject obj) {

        //Sprawdzenie czy encja juz istnieje.
        MongoEntity entity = mongoData.getEntityToEdit(fieldName);


        //Sprawdzanie po polach nowego obiektu
        for(String objFieldName:obj.keySet()){
            MongoField fieldInEntity = entity.findField(fieldName);

            if (fieldInEntity != null){
                //Dodanie nowego typu do istniejacego setu.
                fieldInEntity.getMongoType().add(obj.get(objFieldName).getClass());


                //TODO czy?!?!?!
            }

        }
    }

    @Override
    public void loadIntoMemory(String dbName){
        mongoData = new MongoDataBase();
        try {
            DB dataBase = mongoConnector.getInstance().getDB(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mongoData.setDBname(dbName);
        try {
            mongoData.setEntities(ResolveGetEntitesWithFieldsObjects(dbName));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

/*    private void createDependencies(){

        List<MongoEntity> entityList = mongoData.getEntities();
        boolean realtionFound;
        // Get All entities
        for(MongoEntity entity:entityList){

            // Get All Bindable Fields of Current Entity
            for(MongoField field:entity.getBindableFields()){

                // Get rest o Entites to, means exclude current entitie
                for(MongoEntity restOfEntities:mongoData.exludeEntity(entity.getEntityName())){

                    //Compare Filed searching for FK
                    for(MongoField fieldToCompare:entity.getBindableFields()){

                        if(field.IsInRelation(fieldToCompare)){
                            realtionFound = true;
                            break;
                        }

                    }

                    if(realtionFound){
                        realtionFound = false;
                        break;
                    }
                }

            }
        }


    }*/

/*    private void findAndCreateRelations(){

        List<MongoEntity> entityList = mongoData.getEntities();
        boolean realtionFound;
        // Get All entities
        for(MongoEntity entity:entityList){

            // Get All Bindable Fields of Current Entity
            for(MongoField field:entity.getBindableFields()){

                if(field.getFieldName().contains("_id"))


                // Get rest o Entites to, means exclude current entitie
                for(MongoEntity restOfEntities:mongoData.exludeEntity(entity.getEntityName())){

                    //Compare Filed searching for FK
                    for(MongoField fieldToCompare:entity.getBindableFields()){

                        if(field.IsInRelation(fieldToCompare)){
                            realtionFound = true;
                            break;
                        }

                    }

                }

            }
        }
    }*/
}


