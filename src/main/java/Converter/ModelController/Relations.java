package Converter.ModelController;

/**
 * Created by szef on 2014-08-12.
 */
public enum Relations {

    None(),
    PrimaryKey(),
    ForeginKey();

    @Override
    public String toString() {

        if (this == Relations.PrimaryKey) {
            return "PRIMARY KEY";
        }

        return super.toString();
    }
}
