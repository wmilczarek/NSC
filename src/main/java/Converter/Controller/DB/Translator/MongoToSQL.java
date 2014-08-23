package Converter.Controller.DB.Translator;

import Converter.ViewModel.SqlFieldType;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Set;

/**
 * Created by szef on 2014-08-12.
 */
public class MongoToSQL {

    private MongoToSQL() {
    }


    public static SqlFieldType mongoToSqlFieldConversion(Set<Class<?>> mongoTypes) {

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
            } else if (type == ObjectId.class) {
                sqlType = resolveTypeObjectID(sqlType);
            } else if (type == Date.class) {
                sqlType = resolveTypeDate(sqlType);
            }else if (type == Boolean.class) {
                sqlType = resolveTypeBoolen(sqlType);
            }

            /*else if(type instanceof DateTime){

            }else if(){

            }else if(){

            }else if(){

            }
*/
           /* if(type instanceof Integer){
                sqlType = resolveTypeInteger(sqlType);
            } else if(type instanceof Long){

                if(sqlType == null){
                    sqlType = SqlFieldType.Intager;
                }
            }
*/

        }

        return sqlType;
    }

    private static SqlFieldType resolveTypeBoolen(SqlFieldType type) {
        if (type == null) {
            return SqlFieldType.Bool;
        }
        return SqlFieldType.Text;
    }

    private static SqlFieldType resolveTypeDate(SqlFieldType type) {

        if (type == null) {
            return SqlFieldType.DateTime;
        }
            return SqlFieldType.Text;

    }

    private static SqlFieldType resolveTypeInteger(SqlFieldType type) {

        if (type == null) {
            return SqlFieldType.Intager;
        } else if (type == SqlFieldType.DateTime) {
            //TODO: nowe pole jesli bardzo sprzeczne.
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

    private static SqlFieldType resolveTypeObjectID(SqlFieldType type) {

        // TODO: Sprawdzic czy long nie jest za dlugi
        if (type == null || type == SqlFieldType.Intager || type == SqlFieldType.LongIntager) {
            return SqlFieldType.UUID;
        } else if (type == SqlFieldType.Binary) {

        } else {
            //TODO: nowe pole jesli bardzo sprzeczne.

            return SqlFieldType.Text;
        }

        return type;
    }

    private static SqlFieldType resolveTypeString(SqlFieldType type) {

        // TODO: Sprawdzic czy long nie jest za dlugi
        if (type == SqlFieldType.Binary) {
//!
        } else {
            //TODO: nowe pole jesli bardzo sprzeczne.

            return SqlFieldType.Text;
        }

        return type;
    }


}
