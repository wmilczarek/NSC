package Converter.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szef on 2014-08-12.
 */
public class MongoDataBase {

    private List<MongoEntity> entities;
    private String DBname;

    public List<MongoEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<MongoEntity> entities) {
        this.entities = entities;
    }

    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public MongoEntity findEntity(String name) {

        for (MongoEntity entity : entities) {
            if (entity.getEntityName().equals(name))
                return entity;
        }

        return null;
    }

    public MongoEntity getEntityToEdit(String name) {

        for (MongoEntity entity : entities) {
            if (entity.getEntityName().equals(name))
                return entity;
        }

        return new MongoEntity(name);
    }

    public List<MongoEntity> exludeEntity(String name) {

        List<MongoEntity> mongoEntities = new ArrayList<MongoEntity>();

        for (MongoEntity entity : entities) {
            if (!entity.getEntityName().equals(name))
                mongoEntities.add(entity);
        }

        return mongoEntities;
    }
}
