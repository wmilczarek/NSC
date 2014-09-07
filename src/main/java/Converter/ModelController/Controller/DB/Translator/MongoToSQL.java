package Converter.ModelController.Controller.DB.Translator;

import Converter.ModelController.HelperTypes.LongString;
import Converter.ModelController.HelperTypes.NullType;
import Converter.ModelController.HelperTypes.ShortString;
import Converter.ModelController.SqlFieldType;
import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class MongoToSQL {

    private MongoToSQL() {
    }


    public static SqlFieldType mongoToSqlFieldConversion(Set<Class<?>> mongoTypes) throws IncompatibleFieldTypeConversionException {

        SqlFieldType sqlType = null;

        for (Class type : mongoTypes) {

            if (type == Integer.class) {
                sqlType = resolveTypeInteger(sqlType);
            } else if (type == Long.class) {
                sqlType = resolveTypeLong(sqlType);
            } else if (type == Double.class) {
                sqlType = resolveTypeDouble(sqlType);
            } else if (type == String.class) {
                sqlType = resolveTypeString(sqlType);
            } else if (type == ShortString.class || type == ObjectId.class) {
                sqlType = resolveTypeShortString(sqlType);
            } else if (type == LongString.class) {
                sqlType = resolveTypeLongString(sqlType);
            } else if (type == Date.class) {
                sqlType = resolveTypeDate(sqlType);
            } else if (type == Boolean.class) {
                sqlType = resolveTypeBoolen(sqlType);
            } else if (type == NullType.class){
                sqlType = resolveTypeNull(sqlType);
            }else if ( type == new byte[0].getClass()){
                sqlType = resolveTypeByte(sqlType);
            }
        }

        return sqlType;
    }

    private static SqlFieldType resolveTypeByte(SqlFieldType sqlType) {
        return SqlFieldType.Binary;
    }

    private static SqlFieldType resolveTypeNull(SqlFieldType sqlType) {

        if(sqlType != null){
            return sqlType;
        }

        return SqlFieldType.Null;
    }

    private static SqlFieldType resolveTypeBoolen(SqlFieldType type) {
        if (type == null) {
            return SqlFieldType.Bool;
        }
        return SqlFieldType.Text;
    }

    private static SqlFieldType resolveTypeDate(SqlFieldType type) throws IncompatibleFieldTypeConversionException {

        if (type == null) {
            return SqlFieldType.DateTime;
        } else if(type == SqlFieldType.Bool || type == SqlFieldType.Binary ){
            throw new IncompatibleFieldTypeConversionException(type.toString());
        }

        return SqlFieldType.Text;

    }

    private static SqlFieldType resolveTypeInteger(SqlFieldType type) throws IncompatibleFieldTypeConversionException {

        if (type == null) {
            return SqlFieldType.Intager;
        } else if (type == SqlFieldType.DateTime || type == SqlFieldType.Binary) {
            throw new IncompatibleFieldTypeConversionException(type.toString());
        }

        return type;
    }

    private static SqlFieldType resolveTypeLong(SqlFieldType type) {

        if (type == null || type == SqlFieldType.Intager) {
            return SqlFieldType.LongIntager;
        } else if (type == SqlFieldType.DateTime) {
            //TODO: nowe pole jesli bardzo sprzeczne.
        }

        return type;
    }

    private static SqlFieldType resolveTypeDouble(SqlFieldType type) {

        if (type == null || type == SqlFieldType.Intager) {
            return SqlFieldType.LongIntager;
        } else if (type == SqlFieldType.DateTime) {
            //TODO: nowe pole jesli bardzo sprzeczne.
        } else if (type == SqlFieldType.LongIntager) {
            return SqlFieldType.Text;
        }

        return type;
    }

    private static SqlFieldType resolveTypeString(SqlFieldType type) {


        if (type == null) {

            return SqlFieldType.Text;

        } else if (type == SqlFieldType.Binary) {
//!
        } else {
            //TODO: nowe pole jesli bardzo sprzeczne.

            return SqlFieldType.Text;
        }

        return type;
    }

    private static SqlFieldType resolveTypeShortString(SqlFieldType type) {

        if (type == null) {

        return SqlFieldType.VarcharShort;

        } else if (type == SqlFieldType.Binary) {
//!
        } else if(type == SqlFieldType.Text) {
            //TODO: nowe pole jesli bardzo sprzeczne.

            return SqlFieldType.Text;
        } else if(type == SqlFieldType.VarcharLong) {
            return SqlFieldType.VarcharLong;
        }


        return type;
    }

    private static SqlFieldType resolveTypeLongString(SqlFieldType type) {

        // TODO: Sprawdzic czy long nie jest za dlugi
        if (type == null) {

            return SqlFieldType.VarcharLong;

        } else if (type == SqlFieldType.Binary) {
//!
        } else if(type == SqlFieldType.Text) {
            //TODO: nowe pole jesli bardzo sprzeczne.

            return SqlFieldType.Text;
        }

        return type;
    }

     public static String MongoToSqlValueConverter(Object value){

         if(value.getClass() == Date.class){
             DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             return  df.format(((Date) value)).replace("'","\'");
         } if ( value.getClass() == NullType.class){
             return "NULL";
         } if ( value.getClass() == new byte[0].getClass()){

             return new sun.misc.BASE64Encoder().encode((byte[])value);
         }

         return value.toString().replace("'","\'");
     }
}
