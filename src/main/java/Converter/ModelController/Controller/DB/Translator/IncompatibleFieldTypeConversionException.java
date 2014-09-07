package Converter.ModelController.Controller.DB.Translator;

public class IncompatibleFieldTypeConversionException extends Exception {

    public IncompatibleFieldTypeConversionException() {
    }

    public IncompatibleFieldTypeConversionException(String message) {
        super(message);
    }
}
