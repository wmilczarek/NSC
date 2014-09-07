package Converter.ModelController.CauchModel;

import Converter.ModelController.RelationProperties;
import Converter.ModelController.SqlFieldType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by szef on 2014-09-05.
 */
public class CauchFieldSchema {

    private Set<Class<?>> typesFromMongoApi = new HashSet<Class<?>>();
    private Object value;
    private RelationProperties relations = new RelationProperties();
    private String fieldName;
    private SqlFieldType sqlType;
}
