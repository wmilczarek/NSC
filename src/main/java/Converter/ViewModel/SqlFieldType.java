package Converter.ViewModel;


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
    Binary;


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
        }


        return super.toString();
    }


}
