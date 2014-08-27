package Converter.ViewModel;

/**
 * Created by szef on 2014-08-25.
 */
public class MongoArraySchema {

    private String arrayName;
    private String fatherName;
    private Class<?> valueType;
    private Class<?> fatherIdValueType;
    private Object value;

    public MongoArraySchema(String name) {
        this.arrayName = name;
    }

    public Class<?> getFatherIdValueType() {
        return fatherIdValueType;
    }

    public void setFatherIdValueType(Object fatherIdValueType) {
        this.fatherIdValueType = fatherIdValueType.getClass();
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void setValueType(Object valueType) {
        
        if (valueType.getClass() == String.class) {
            String stringType = (String) valueType;

            if(stringType.length() <= 24){
                valueType = new ShortString();
            } else if( stringType.length() <= 120){
                valueType = new LongString();
            }

        }

        this.valueType = valueType.getClass();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
