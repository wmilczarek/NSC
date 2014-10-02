package Converter.ConverterMetaDataModels.MongoModel;

import Converter.ConverterMetaDataModels.BaseModel.DocumentDataBase;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findAndRemoveMetaData;
import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;


public class MongoDataBase extends DocumentDataBase{

    private Set<MongoEntitySchema> entitiesSchema = new HashSet<MongoEntitySchema>();

    @Override
    public Set<MongoEntitySchema> getEntitiesSchema() {
        return entitiesSchema;
    }


    public boolean appendEntitySchema(String name, MongoFieldSchema fieldSchema) {

        for (MongoEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(name)) {

                entity.appendEntityFields(fieldSchema);
                return false;
            }
        }

        MongoEntitySchema newEntityData = new MongoEntitySchema(name);
        newEntityData.appendEntityFields(fieldSchema);
        entitiesSchema.add(newEntityData);

        return true;
    }


    public boolean checkIfEntityExists(String name) {
        for (MongoEntitySchema entitie : this.entitiesSchema) {
            if (entitie.getEntityName().equals(name)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public void createIntersectionTable(TranslationMetaDataEntity array) {

        String arrayName = array.getMetaDataObjectName();
        MongoFieldSchema oldArrayField = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(arrayName + "_value"), ((MongoEntitySchema) array).getEntityFields());
        ((MongoEntitySchema)array).getEntityFields().remove(array.getMetaDataObjectName() + "_value");
        //!!!TODO: komenty
        MongoFieldSchema tableKeyForIntersection = ((MongoEntitySchema)array).getEntityFields().stream().filter(d -> !d.getFieldName().contains("_value")).collect(Collectors.toList()).get(0);


        String tableTypeName = tableKeyForIntersection.getFieldName().replace("_id","");
        String intersectionName = tableTypeName + "_" + array.getMetaDataObjectName();
        // zmiany nazy starej tablicy aby nie było konfliktów
        array.setMetaDataObjectName(arrayName+"_temp");



        MongoEntitySchema mongoNewArrayEntity = createNewArrayTable(arrayName, oldArrayField);

        MongoEntitySchema mongoNewItersectionEntity = createIntersectionTable(arrayName, tableKeyForIntersection, intersectionName);

        separeteDataToIntersectionEntity(array, arrayName, mongoNewArrayEntity, mongoNewItersectionEntity);

        entitiesSchema.remove(array);

    }

    private MongoEntitySchema createIntersectionTable(String arrayName, MongoFieldSchema tableKeyForIntersection, String intersectionName) {
        // obsluga tabeli posrednie, tworzeni, dopisywanie FK do array oraz tabeli nadżędnej
        MongoEntitySchema mongoNewItersectionEntity = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(intersectionName), this.getEntitiesSchema());
        mongoNewItersectionEntity.setEntityFields(tableKeyForIntersection);
        tableKeyForIntersection.keyChecker();

        MongoFieldSchema arrayKeyForIntersection = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema(arrayName + "_id"), mongoNewItersectionEntity.getEntityFields());
        arrayKeyForIntersection.setMetaDataType(Long.class);
        arrayKeyForIntersection.keyChecker();

        return mongoNewItersectionEntity;
    }

    private MongoEntitySchema createNewArrayTable(String arrayName, MongoFieldSchema oldArrayField) {

        MongoEntitySchema mongoNewArrayEntity = (MongoEntitySchema) findOrCreateMetaData(new MongoEntitySchema(arrayName), this.getEntitiesSchema());
        MongoFieldSchema mongoNewArrayKey = (MongoFieldSchema) findOrCreateMetaData(new MongoFieldSchema("_id"), mongoNewArrayEntity.getEntityFields());
        mongoNewArrayEntity.setEntityFields(oldArrayField);
        mongoNewArrayKey.keyChecker();
        mongoNewArrayKey.setMetaDataType(Long.class);

        return mongoNewArrayEntity;
    }

    private void separeteDataToIntersectionEntity(TranslationMetaDataEntity array, String arrayName, MongoEntitySchema mongoNewArrayEntity, MongoEntitySchema mongoNewItersectionEntity) {
        DocumentRowMetaData newArrayRow;
        DocumentRowMetaData newIntersectionRow;
        String tableIdname = ((MongoEntitySchema) array).getEntityFields().stream().filter(d -> d.getFieldName().contains("_value") == false).collect(Collectors.toList()).get(0).getFieldName();
        Set<Object> uniqValue = new HashSet<>();


            for(DocumentRowMetaData row:array.getTranslationMetaDataDocuments()){

                newArrayRow = new DocumentRowMetaData();
                newIntersectionRow = new DocumentRowMetaData();

                if( !uniqValue.contains(row.getFieldValue().get(arrayName + "_value"))) {

                    newArrayRow.setFieldValue(arrayName + "_value", row.getFieldValue().get(arrayName + "_value"));
                    newArrayRow.setFieldValue("_id", mongoNewArrayEntity.incrementAutoPriamryKey());
                    newIntersectionRow.setFieldValue(tableIdname, row.getFieldValue().get(tableIdname));
                    newIntersectionRow.setFieldValue(arrayName + "_id", mongoNewArrayEntity.getAutoPrimaryKey());
                    mongoNewArrayEntity.setMongoFieldData(newArrayRow);
                    uniqValue.add(row.getFieldValue().get(arrayName + "_value"));
                } else {

                    DocumentRowMetaData previousRow = mongoNewArrayEntity.getTranslationMetaDataDocuments().stream().filter(d -> d.getFieldValue().get(arrayName + "_value").equals(row.getFieldValue().get(arrayName + "_value"))).collect(Collectors.toList()).get(0);
                    newIntersectionRow.setFieldValue(tableIdname,row.getFieldValue().get(tableIdname));
                    newIntersectionRow.setFieldValue(arrayName + "_id", previousRow.getFieldValue().get("_id"));
                }


            mongoNewItersectionEntity.setMongoFieldData(newIntersectionRow);
        }
    }


}
