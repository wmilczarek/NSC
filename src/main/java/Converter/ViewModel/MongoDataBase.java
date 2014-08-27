package Converter.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szef on 2014-08-12.
 */
public class MongoDataBase {

    private List<MongoEntitySchema> entitiesSchema = new ArrayList<MongoEntitySchema>();

    private List<MongoEntityData> entitiesData = new ArrayList<MongoEntityData>();

    private List<MongoArraySchema> arraySchema = new ArrayList<MongoArraySchema>();

    private List<MongoEntityData> arrayData = new ArrayList<MongoEntityData>();

    private String DBname;

    public List<MongoEntitySchema> getEntitiesSchema() {
        return entitiesSchema;
    }

    public void setEntitiesSchema(List<MongoEntitySchema> entitiesSchema) {
        this.entitiesSchema = entitiesSchema;
    }

    public List<MongoEntityData> getEntitiesData() {
        return entitiesData;
    }

    public void setEntitiesData(List<MongoEntityData> entitiesData) {
        this.entitiesData = entitiesData;
    }

    public List<MongoArraySchema> getArraySchema() {
        return arraySchema;
    }

    public void setArraySchema(List<MongoArraySchema> arraySchema) {
        this.arraySchema = arraySchema;
    }

    public List<MongoEntityData> getArrayData() {
        return arrayData;
    }

    public void setArrayData(List<MongoEntityData> arrayData) {
        this.arrayData = arrayData;
    }

    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public MongoEntitySchema findEntity(String name) {

        for (MongoEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(name))
                return entity;
        }

        return null;
    }

    public MongoEntitySchema getEntitySchemaToEdit(String name) {

        for (MongoEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(name))
                return entity;
        }

        return new MongoEntitySchema(name);
    }

    public MongoEntityData getEntityDataToEdit(String name) {

        for (MongoEntityData entity : entitiesData) {
            if (entity.getEntityName().equals(name))
                return entity;
        }

        return new MongoEntityData(name);
    }

    // returns true if created
    public boolean appendEntityData(String name, List<MongoRowData> data) {

        for (MongoEntityData entity : entitiesData) {
            if (entity.getEntityName().equals(name)) {

                entity.setMongoFieldData(data);
                return false;
            }
        }

        MongoEntityData newEntityData = new MongoEntityData(name);
        newEntityData.setMongoFieldData(data);

        return true;
    }

    public boolean appendEntityRowData(String name, MongoRowData data) {

        for (MongoEntityData array : arrayData) {
            if (array.getEntityName().equals(name)) {

                array.setMongoFieldData(data);
                return false;
            }
        }

        MongoEntityData newEntityData = new MongoEntityData(name);
        newEntityData.setMongoFieldData(data);
        entitiesData.add(newEntityData);

        return true;
    }

    public boolean appendEntityData(String name, MongoRowData data) {

        for (MongoEntityData entity : entitiesData) {
            if (entity.getEntityName().equals(name)) {

                entity.setMongoFieldData(data);
                return false;
            }
        }

        MongoEntityData newEntityData = new MongoEntityData(name);
        newEntityData.setMongoFieldData(data);
        entitiesData.add(newEntityData);

        return true;
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


    public boolean appendIfExistsEntitySchema(MongoEntitySchema mongoEntitySchema) {

        for (MongoEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(mongoEntitySchema.getEntityName())) {

                entitiesSchema.remove(entity);
                entitiesSchema.add(mongoEntitySchema);
                return true;
            }
        }

        return false;
    }

    public boolean appendIfNotExistsEntitySchema(MongoEntitySchema mongoEntitySchema) {

        for (MongoEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(mongoEntitySchema.getEntityName())) {

                return false;
            }
        }

        entitiesSchema.add(mongoEntitySchema);
        return true;
    }


    public List<MongoEntitySchema> exludeEntity(String name) {

        List<MongoEntitySchema> mongoEntities = new ArrayList<MongoEntitySchema>();

        for (MongoEntitySchema entity : entitiesSchema) {
            if (!entity.getEntityName().equals(name))
                mongoEntities.add(entity);
        }

        return mongoEntities;
    }

    public MongoArraySchema getArraySchema(String name) {

        for (MongoArraySchema schema : this.arraySchema) {
            if (schema.getArrayName().equals(name))
                return schema;
        }

        return new MongoArraySchema(name);
    }

    public boolean appendIfNotExistsArraySchema(MongoArraySchema arraySchema) {

        for (MongoArraySchema array : this.arraySchema) {
            if (array.getArrayName().equals(arraySchema.getArrayName())) {

                return false;
            }
        }

        this.arraySchema.add(arraySchema);
        return true;
    }

    public boolean appendArrayRowData(String name, MongoRowData data) {

        for (MongoEntityData array : arrayData) {
            if (array.getEntityName().equals(name)) {

                array.setMongoFieldData(data);
                return false;
            }
        }

        MongoEntityData newArrayData = new MongoEntityData(name);
        newArrayData.setMongoFieldData(data);
        arrayData.add(newArrayData);

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

    public void importSchemaFromArray(MongoArraySchema mongoArraySchema) {

        MongoEntitySchema schema = new MongoEntitySchema(mongoArraySchema.getArrayName());
        MongoFieldSchema mongoFieldSchema = new MongoFieldSchema();
        mongoFieldSchema.setFieldName("value");
        mongoFieldSchema.setRelations(Relations.None);
        mongoFieldSchema.setTypesFromMongoApi(mongoArraySchema.getValueType());

        MongoFieldSchema mongoFieldId= new MongoFieldSchema();
        mongoFieldId.setFieldName(mongoArraySchema.getFatherName() + "_id");
        mongoFieldId.setRelations(Relations.ForeginKey);
        mongoFieldId.setTypesFromMongoApi(mongoArraySchema.getFatherIdValueType());

        schema.setEntityFields(mongoFieldSchema);
        schema.setEntityFields(mongoFieldId);

        this.entitiesSchema.add(schema);
        transferArrayDataToEntities(mongoArraySchema.getArrayName());
    }

    public void transferArrayDataToEntities(String arrayName) {

        for (MongoEntityData entityData : arrayData) {
            if (entityData.getEntityName().equals(arrayName)) {
                this.entitiesData.add(entityData);
            }
        }


    }

    public void transferAndConvertIntersectionTableData(String arrayName, String newName) {
        for (MongoEntityData entityData : arrayData) {
            if (entityData.getEntityName().equals(arrayName)) {

                for (MongoRowData field : entityData.getMongoFieldData()) {

                    Object temp = field.getFieldValue().get("value");
                    field.getFieldValue().remove("value");
                    field.setFieldValue(entityData.getEntityName() + "_id", temp);
                    entityData.setMongoFieldData(field);
                }

                entityData.setEntityName(newName);
                this.entitiesData.add(entityData);
            }
        }
    }

    public void createInstersectionEntitie(MongoArraySchema mongoArraySchema) {

        MongoEntitySchema schema = new MongoEntitySchema(mongoArraySchema.getArrayName() + "_" + mongoArraySchema.getFatherName());

        MongoFieldSchema mongoFieldSchemaOtherEntityId = new MongoFieldSchema();
        mongoFieldSchemaOtherEntityId.setFieldName(mongoArraySchema.getArrayName() + "_id");
        mongoFieldSchemaOtherEntityId.setRelations(Relations.ForeginKey);
        mongoFieldSchemaOtherEntityId.setTypesFromMongoApi(mongoArraySchema.getValueType());

        MongoFieldSchema mongoFieldSchemaFatherId = new MongoFieldSchema();
        mongoFieldSchemaFatherId.setFieldName(mongoArraySchema.getFatherName() + "_id");
        mongoFieldSchemaFatherId.setRelations(Relations.ForeginKey);
        mongoFieldSchemaFatherId.setTypesFromMongoApi(mongoArraySchema.getFatherIdValueType());


        schema.setEntityFields(mongoFieldSchemaOtherEntityId);
        transferAndConvertIntersectionTableData(mongoArraySchema.getArrayName(), mongoArraySchema.getArrayName() + mongoArraySchema.getFatherName());
        this.entitiesSchema.add(schema);
    }
}
