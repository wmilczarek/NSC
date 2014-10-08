package Converter.ModelController.Controller.DB.DocumentDB.Cauch;

import Converter.ModelController.HelperTypes.NullType;
import com.google.gson.JsonElement;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by szef on 2014-09-09.
 */
public class CauchUtils {

    private CauchUtils() {
    }

    public static Object resolvePreMetaDataTypes(JsonElement element) {

        if (element == null) {
            return new NullType();
        }

        if (element.isJsonPrimitive()) {

            if (element.getAsJsonPrimitive().isNumber()) {

                Integer int32 = tryPasrseInt(element);

                if (int32 != null) {
                    return int32;
                }

                Long int64 = tryPasrseBigInt(element) != null ? tryPasrseBigInt(element).longValue() : null;

                if (int64 != null) {
                    return int64;
                }

                Double dbl = tryPasrseDouble(element);

                if (dbl != null) {
                    return dbl;
                }

            } else if (element.getAsJsonPrimitive().isBoolean()) {

                return element.getAsBoolean();

            } else if (element.getAsJsonPrimitive().isString()) {

                try {
                    Date testIfDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH).parse(element.getAsString());
                    return testIfDate;
                } catch (ParseException e) {
                    return element.getAsString();
                }


            }


        }

        if (element.isJsonNull()) {

            return new NullType();
        }

        return null;
    }


    private static Double tryPasrseDouble(JsonElement element) {
        try {

            return element.getAsDouble();

        } catch (Exception e) {
            return null;
        }
    }

    private static BigInteger tryPasrseBigInt(JsonElement element) {
        try {

            return element.getAsBigInteger();

        } catch (Exception e) {
            return null;
        }
    }

    private static Integer tryPasrseInt(JsonElement element) {
        try {

            if (element.getAsString().contains(".")) {
                return null;
            }

            return element.getAsInt();

        } catch (Exception e) {
            return null;
        }
    }

}
