package Converter.ViewModel;

/**
 * Created by szef on 2014-08-15.
 */
public enum SqlFieldType {

    Intager,
    LongIntager,
    DoublePrecision,
    Varchar,
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

        if(this == SqlFieldType.Text){
            return "TEXT!!!";
        }

        return super.toString();
    }

    public static SqlFieldType resolveThis(SqlFieldType sqlType){

        if(sqlType == null){
            sqlType = SqlFieldType.Intager;
        } if( sqlType == sqlType.LongIntager){

        }

        return null;
    }
}
