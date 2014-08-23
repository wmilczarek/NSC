package Converter.ViewModel;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by szef on 2014-08-12.
 */
public class MongoEntity {

    public MongoEntity() {
    }

    public MongoEntity(String name) {
        this.entityName = name;
    }

    public Map<MongoField, SqlFieldType> getEntityFields() {
        return entityFields;
    }

    private Map<MongoField,SqlFieldType> entityFields;
    private String entityName;

    public void setEntityFields(Map<MongoField, SqlFieldType> entityFields) {
        this.entityFields = entityFields;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public MongoField findField(String name){

        for(MongoField field: entityFields.keySet()){
            if(field.getFieldName().equals(name))
                return field;
        }

        return null;
    }

    public Map<String,MongoField> getBindableFields(){

        List<MongoField> mongoFields = new ArrayList<MongoField>();
        Map<String,MongoField> bindableRelation = new HashMap<String, MongoField>();

        for(Map.Entry<MongoField,SqlFieldType> field:entityFields.entrySet()){

            if(field.getValue() == SqlFieldType.DateTime ||
                    field.getValue() == SqlFieldType.Binary ||
                    field.getValue() == SqlFieldType.DoublePrecision ||
                    field.getValue() == SqlFieldType.Text ||
                    field.getValue() == SqlFieldType.Bool ||
                    field.getKey().getRelations() != null){
                continue;
            } else if(mongoFields.contains("_id")){

                String inRelationEntity = field.getKey().getFieldName().replace("_id", "");
                //bindableRelation.put(field.getKey().getFieldName().replace("_id", ""),MongoField);
                Relations relation = Relations.ForeginKey;
                relation.setInRealtaionField(inRelationEntity);


                field.getKey().setRelations(relation);


                mongoFields.add(field.getKey());

            }
        }

        return null;
    }

    public MongoField getPrimaryKey(){

        for(MongoField field: entityFields.keySet()){
            if(field.getFieldName().equals("_id"))
                return field;
        }

        return null;
    }


}
