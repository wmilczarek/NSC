package Converter.ModelController;


public enum SqlFieldType {

    Intager,
    LongIntager,
    DoublePrecision,
    VarcharShort,
    VarcharLong,
    DateTime,
    Text,
    UUID,
    Bool,
    Binary,
    Null;


    SqlFieldType() {
    }

    // used for script generation.
    @Override
    public String toString() {

        if (this == SqlFieldType.Text) {
            return "TEXT";
        } else if (this == SqlFieldType.Intager) {
            return "INT";
        } else if (this == SqlFieldType.LongIntager) {
            return "BIGINT";
        } else if (this == SqlFieldType.LongIntager) {
            return "BIGINT";
        } else if (this == SqlFieldType.DoublePrecision) {
            return "DOUBLE";
        } else if (this == SqlFieldType.Bool) {
            return "BOOL";
        } else if (this == SqlFieldType.DateTime) {
            return "DATETIME";
        } else if (this == SqlFieldType.VarcharShort) {
            return "VARCHAR(24)";
        } else if (this == SqlFieldType.VarcharLong) {
            return "VARCHAR(120)";
        } else if (this == SqlFieldType.Binary){
            return "BLOB";
        }


        return super.toString();
    }


}
