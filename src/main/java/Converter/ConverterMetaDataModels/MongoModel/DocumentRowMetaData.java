package Converter.ConverterMetaDataModels.MongoModel;

import java.util.HashMap;
import java.util.Map;


public class DocumentRowMetaData {

    private Map<String, Object> fieldValue = new HashMap<String, Object>();

    public Map<String, Object> getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Map<String, Object> fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setFieldValue(String name, Object value) {
        this.fieldValue.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentRowMetaData that = (DocumentRowMetaData) o;

        if (!fieldValue.equals(that.fieldValue)) return false;


        return true;
    }

    @Override
    public int hashCode() {
        return fieldValue.hashCode();
    }

}
