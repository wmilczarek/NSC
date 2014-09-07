package Converter.ModelController.MongoModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by szef on 2014-08-24.
 */
public class MongoRowData {

    private Map<String, Object> fieldValue = new HashMap<String, Object>();

    public Map<String, Object> getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Map<String, Object> fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setFieldValue(String name, Object value) {
        this.fieldValue.put(name,value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MongoRowData that = (MongoRowData) o;

        if (!fieldValue.equals(that.fieldValue)) return false;






        return true;
    }

    @Override
    public int hashCode() {
        return fieldValue.hashCode();
    }

}
