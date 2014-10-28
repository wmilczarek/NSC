package Converter.ModelController;


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
