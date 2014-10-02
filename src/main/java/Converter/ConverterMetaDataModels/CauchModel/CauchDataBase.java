package Converter.ConverterMetaDataModels.CauchModel;

import Converter.ConverterMetaDataModels.BaseModel.DocumentDataBase;
import Converter.ConverterMetaDataModels.BaseModel.TranslationMetaDataEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class CauchDataBase extends DocumentDataBase {


    private Set<CouchEntitySchema> entitiesSchema = new HashSet<CouchEntitySchema>();

    private String DBname;

    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    @Override
    public void createIntersectionTable(TranslationMetaDataEntity array) {

    }

    @Override
    public Set<CouchEntitySchema> getEntitiesSchema() {
        return entitiesSchema;
    }


    public void setEntitiesSchema(Set<CouchEntitySchema> entitiesSchema) {
        this.entitiesSchema = entitiesSchema;
    }


    public CouchEntitySchema findOrCreate(String schemaName) {

        List<CouchEntitySchema> entity = this.entitiesSchema.stream().filter(e -> e.getEntityName() == schemaName).collect(Collectors.toList());
        ;

        if (entity.size() == 0) {
            return new CouchEntitySchema(schemaName);
        } else if (entity.size() != 1) {

            //TODO:exceptoin
        }

        return entity.get(0);
    }

    public boolean appendEntitySchema(String name, CauchFieldSchema fieldSchema) {

        for (CouchEntitySchema entity : entitiesSchema) {
            if (entity.getEntityName().equals(name)) {

                entity.appendEntityFields(fieldSchema);
                return false;
            }
        }

        CouchEntitySchema newEntityData = new CouchEntitySchema(name);
        newEntityData.appendEntityFields(fieldSchema);
        entitiesSchema.add(newEntityData);

        return true;
    }


}
