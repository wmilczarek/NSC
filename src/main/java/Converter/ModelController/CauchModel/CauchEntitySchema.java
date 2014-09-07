package Converter.ModelController.CauchModel;


import java.util.ArrayList;
import java.util.List;

public class CauchEntitySchema {

    private List<CauchFieldSchema> entityFields = new ArrayList<CauchFieldSchema>();
    private String entityName;
    private Long autoPrimaryKey = Long.valueOf(0);

    public CauchEntitySchema(String schemaName) {
        entityName = schemaName;
    }

    public List<CauchFieldSchema> getEntityFields() {
        return entityFields;
    }

    public void setEntityFields(List<CauchFieldSchema> entityFields) {
        this.entityFields = entityFields;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getAutoPrimaryKey() {
        return autoPrimaryKey;
    }

    public void setAutoPrimaryKey(Long autoPrimaryKey) {
        this.autoPrimaryKey = autoPrimaryKey;
    }

}
