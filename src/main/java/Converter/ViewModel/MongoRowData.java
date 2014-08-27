package Converter.ViewModel;

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

}
