package Converter.ConverterMetaDataModels.DataModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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


    public boolean equalsNormalization(Object o, List<String> keySet) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentRowMetaData that = (DocumentRowMetaData) o;

        List<String> keysInThis = fieldValue.keySet().stream().filter(f -> keySet.contains(f)).collect(Collectors.toList());
        List<String> keysInThat = ((DocumentRowMetaData) o).getFieldValue().keySet().stream().filter(f -> keySet.contains(f)).collect(Collectors.toList());

        if( !keysInThat.equals(keysInThis)){
            return false;
        }

        for (Map.Entry<String,Object> entry:fieldValue.entrySet()){

            if( keysInThis.contains(entry.getKey())){

                if(!that.getFieldValue().get(entry.getKey()).equals(this.getFieldValue().get(entry.getKey()))){
                    return false;
                }
            }
        }


        return true;
    }


    @Override
    public int hashCode() {
        return fieldValue.hashCode();
    }

}
