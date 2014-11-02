package Converter.ConverterMetaDataModels.DataModel;

import Converter.ModelController.Controller.DB.Translator.DocumentDBToSQL;
import Converter.ModelController.Controller.DB.Translator.IncompatibleFieldTypeConversionException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static Converter.ModelController.Controller.DB.DocumentDB.CommonOperationUtils.findOrCreateMetaData;


public class TranslationDataBase {

    private String DBname;
    private Set<TranslationEntitySchema> entitiesSchema = new HashSet<>();

    public TranslationDataBase(String dbName) {
        this.DBname = dbName;
    }

    public Set<TranslationEntitySchema> getEntitiesSchema() {
        return entitiesSchema;
    }

    public void setEntitiesSchema(Set<TranslationEntitySchema> entitiesSchema) {
        this.entitiesSchema = entitiesSchema;
    }

    public boolean appendEntitySchema(String name, TranslationFieldSchema fieldSchema) {

        for (TranslationEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(name)) {

                entity.appendEntityFields(fieldSchema);
                return false;
            }
        }

        TranslationEntitySchema newEntityData = new TranslationEntitySchema(name);
        newEntityData.appendEntityFields(fieldSchema);
        entitiesSchema.add(newEntityData);

        return true;
    }


    public boolean checkIfEntityExists(String name) {
        for (TranslationEntitySchema entitie : this.entitiesSchema) {
            if (entitie.getEntityName().equals(name)) {
                return true;
            }
        }

        return false;

    }

    public void createIntersectionTableFromDenormalizedData(TranslationEntitySchema array) {

        String arrayName = array.getMetaDataObjectName();
        TranslationFieldSchema oldArrayField = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(arrayName + "_value"), ((TranslationEntitySchema) array).getEntityFields());
        ((TranslationEntitySchema) array).getEntityFields().remove(array.getMetaDataObjectName() + "_value");
        //!!!TODO: komenty
        TranslationFieldSchema tableKeyForIntersection = ((TranslationEntitySchema) array).getEntityFields().stream().filter(d -> !d.getFieldName().contains("_value")).collect(Collectors.toList()).get(0);


        String tableTypeName = tableKeyForIntersection.getFieldName().replace("_id", "");
        String intersectionName = tableTypeName + "_" + array.getMetaDataObjectName();
        // zmiany nazwy starej tablicy aby nie było konfliktów
        entitiesSchema.remove(array);
        array.setMetaDataObjectName(arrayName + "_temp");

        TranslationEntitySchema mongoNewArrayEntity = createNewArrayTable(arrayName, oldArrayField);
        TranslationEntitySchema mongoNewItersectionEntity = createIntersectionTable(arrayName, tableKeyForIntersection, intersectionName);


        //todo: usuwanie
        separeteDataToIntersectionEntity(array, arrayName, mongoNewArrayEntity, mongoNewItersectionEntity);

        this.setEntitiesSchema(entitiesSchema);
    }

    private TranslationEntitySchema createIntersectionTable(String arrayName, TranslationFieldSchema tableKeyForIntersection, String intersectionName) {
        // obsluga tabeli posrednie, tworzeni, dopisywanie FK do array oraz tabeli nadżędnej
        TranslationEntitySchema mongoNewItersectionEntity = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(intersectionName), this.getEntitiesSchema());
        mongoNewItersectionEntity.setEntityFields(tableKeyForIntersection);
        tableKeyForIntersection.keyChecker();

        TranslationFieldSchema arrayKeyForIntersection = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema(arrayName + "_id"), mongoNewItersectionEntity.getEntityFields());
        arrayKeyForIntersection.setMetaDataType(Long.class);
        arrayKeyForIntersection.keyChecker();

        return mongoNewItersectionEntity;
    }

    private TranslationEntitySchema createNewArrayTable(String arrayName, TranslationFieldSchema oldArrayField) {

        TranslationEntitySchema mongoNewArrayEntity = (TranslationEntitySchema) findOrCreateMetaData(new TranslationEntitySchema(arrayName), this.getEntitiesSchema());
        TranslationFieldSchema mongoNewArrayKey = (TranslationFieldSchema) findOrCreateMetaData(new TranslationFieldSchema("_id"), mongoNewArrayEntity.getEntityFields());
        mongoNewArrayEntity.setEntityFields(oldArrayField);
        mongoNewArrayKey.keyChecker();
        mongoNewArrayKey.setMetaDataType(Long.class);

        return mongoNewArrayEntity;
    }

    private void separeteDataToIntersectionEntity(TranslationEntitySchema array, String arrayName, TranslationEntitySchema mongoNewArrayEntity, TranslationEntitySchema mongoNewItersectionEntity) {
        DocumentRowMetaData newArrayRow;
        DocumentRowMetaData newIntersectionRow;
        String tableIdname = ((TranslationEntitySchema) array).getEntityFields().stream().filter(d -> d.getFieldName().contains(array.isFromArray().toString()) == false).collect(Collectors.toList()).get(0).getFieldName();
        Set<Object> uniqValue = new HashSet<>();


        for (DocumentRowMetaData row : array.getTranslationMetaDataDocuments()) {

            newArrayRow = new DocumentRowMetaData();
            newIntersectionRow = new DocumentRowMetaData();

            if (!uniqValue.contains(row.getFieldValue().get(arrayName + array.isFromArray().toString()))) {

                newArrayRow.setFieldValue(arrayName + array.isFromArray().toString(), row.getFieldValue().get(arrayName + array.isFromArray().toString()));
                newArrayRow.setFieldValue("_id", mongoNewArrayEntity.incrementAutoPriamryKey());
                newIntersectionRow.setFieldValue(tableIdname, row.getFieldValue().get(tableIdname));
                newIntersectionRow.setFieldValue(arrayName + "_id", mongoNewArrayEntity.getAutoPrimaryKey());
                mongoNewArrayEntity.setFieldData(newArrayRow);
                uniqValue.add(row.getFieldValue().get(arrayName + array.isFromArray().toString()));
            } else {

                DocumentRowMetaData previousRow = mongoNewArrayEntity.getTranslationMetaDataDocuments().stream().filter(d -> d.getFieldValue().get(arrayName + array.isFromArray().toString()).equals(row.getFieldValue().get(arrayName + "_value"))).collect(Collectors.toList()).get(0);
                newIntersectionRow.setFieldValue(tableIdname, row.getFieldValue().get(tableIdname));
                newIntersectionRow.setFieldValue(arrayName + "_id", previousRow.getFieldValue().get("_id"));
            }


            mongoNewItersectionEntity.setFieldData(newIntersectionRow);
        }
    }


    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public void translateFieldsOfAllEntetiesToSqlTypes() {

        for (TranslationEntitySchema entitySchema : this.getEntitiesSchema()) {

            for (TranslationFieldSchema FieldSchema : entitySchema.getTranslationMetaDataFieldsSchema()) {

                try {
                    FieldSchema.setSqlType(DocumentDBToSQL.documentToSqlFieldConversion(FieldSchema.getMetadataType()));
                } catch (IncompatibleFieldTypeConversionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void joinReferenceArrays(TranslationEntitySchema array, TranslationEntitySchema translationEntitySchema) {

        for (DocumentRowMetaData rowData : translationEntitySchema.getTranslationMetaDataDocuments()) {

            array.setFieldData(rowData);
        }


        //TODO:REMOVE
    }
}
