package Converter.ViewModel;

/**
 * Created by szef on 2014-08-12.
 */
public enum MongoType {


    Double(1),
    String(2),
    Object(3),
    Array(4),
    BinaryData(5),
    Undefined(6),
    ObjectId(7),
    Boolean(8),
    Date(9),
    Null(10),
    RegularExpression(11),
    JavaScript(13),
    Symbol(14),
    JavaScriptScope(15),
    Integer32(16),
    Timestamp(17),
    Integer64(18),
    Minkey(255),
    Maxkey(127);

    private int fieldNumber;

    MongoType(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    MongoType() {
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }


}
