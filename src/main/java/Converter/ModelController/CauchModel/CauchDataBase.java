package Converter.ModelController.CauchModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by szef on 2014-09-05.
 */
public class CauchDataBase {

    private List<CauchEntitySchema> entitiesSchema = new ArrayList<CauchEntitySchema>();
    private String DBname;

    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public List<CauchEntitySchema> getEntitiesSchema() {
        return entitiesSchema;
    }
/*

    private List<MongoEntityData> entitiesData = new ArrayList<MongoEntityData>();

    private List<MongoArraySchema> arraySchema = new ArrayList<MongoArraySchema>();

    private List<MongoEntityData> arrayData = new ArrayList<MongoEntityData>();
*/

    public void setEntitiesSchema(List<CauchEntitySchema> entitiesSchema) {
        this.entitiesSchema = entitiesSchema;
    }


    public CauchEntitySchema createOrGet(String schemaName){

        List<CauchEntitySchema> entity = this.entitiesSchema.stream().filter(e -> e.getEntityName() == schemaName).collect(Collectors.toList());;

        if( entity.size() == 0){
            return new CauchEntitySchema(schemaName);
        } else if(entity.size() != 1) {

            //TODO:exceptoin
        }

        return entity.get(0);
    }


}
