package Converter.ModelController.MongoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szef on 2014-08-24.
 */
public class MongoEntityData {

    private String entityName;
    private List<MongoRowData> mongoFieldData = new ArrayList<MongoRowData>();


    public MongoEntityData() {
    }

    public MongoEntityData(String entityName) {

        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<MongoRowData> getMongoFieldData() {
        return mongoFieldData;
    }

    public void setMongoFieldData(MongoRowData mongoFieldData) {
        this.mongoFieldData.add(mongoFieldData);
    }

    public void setMongoFieldData(List<MongoRowData> mongoFieldData) {
        this.mongoFieldData = mongoFieldData;
    }

}
