package Converter.ModelController.Controller.DB.NoSQL.Mongo;

import Converter.ModelController.*;
import Converter.ModelController.MongoModel.*;

import java.util.Map;

/**
 * Created by szef on 2014-08-27.
 */
public class MongoOperationUtils {

    public static void changeName(MongoDataBase mongoDataBase) {

        for (MongoEntitySchema mongoEntitySchema : mongoDataBase.getEntitiesSchema()) {
            for (MongoFieldSchema mongoFieldSchema : mongoEntitySchema.getEntityFields()) {

                if (mongoFieldSchema.getRelationProperties().getRelations() == Relations.PrimaryKey) {
                    mongoFieldSchema.setFieldName(mongoEntitySchema.getEntityName() + "_PK");
                }
            }

        }


        for (MongoEntityData mongoEntityData : mongoDataBase.getEntitiesData()) {
            for (MongoRowData mongoRowData : mongoEntityData.getMongoFieldData()) {

                Object obj = mongoRowData.getFieldValue().get("_id");
                if (obj != null) {
                    mongoRowData.getFieldValue().remove("_id");
                    mongoRowData.getFieldValue().put(mongoEntityData.getEntityName() + "_PK", obj);
                }
            }

        }
    }

    public static MongoFieldSchema createForeginKey(String fieldName) {

        MongoFieldSchema mongoFieldSchemaId = new MongoFieldSchema();
        mongoFieldSchemaId.setTypesFromMongoApi(Long.class);
        mongoFieldSchemaId.setFieldName(fieldName + "_id");
        mongoFieldSchemaId.getRelationProperties().setRelations(Relations.ForeginKey);
        mongoFieldSchemaId.getRelationProperties().setFatherNames(fieldName);

        return mongoFieldSchemaId;
    }

    public void addIfNotExists(String entityName, MongoRowData checkRowData, MongoDataBase mongoDataBase) {

        MongoEntityData entity = mongoDataBase.findEntityData(entityName);

        boolean isEquale = false;

        //iterowanie po wierszu
        for (MongoRowData rowData : entity.getMongoFieldData()) {

            if (rowData.getFieldValue().size() != checkRowData.getFieldValue().size()) {
                break;
            }

            isEquale = true;

            //Sprawdzanie wiersza
            for (Map.Entry entry : rowData.getFieldValue().entrySet()) {

                Object obj = checkRowData.getFieldValue().get(entry.getKey());

                if (obj == null) {
                    isEquale = false;
                    break;
                } else if (!equals2(obj, entry.getValue())) {
                    isEquale = false;
                    break;
                }
            }

            //Jesli istnieje juz obiekt
            if(isEquale){
                return;
            }

        }

        entity.getMongoFieldData().add(checkRowData);
    }

    public boolean equals2(Object object2, Object object) {
        return object.getClass() == object2.getClass() && object.equals(object2);
    }
}
