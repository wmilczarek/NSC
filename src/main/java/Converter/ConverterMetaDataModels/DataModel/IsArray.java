package Converter.ConverterMetaDataModels.DataModel;


public enum IsArray {

    NO,
    DataArry,
    ReferenceArray,
    ToDelete;
    String from;
    String destiny;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    @Override
    public String toString() {

        if (this == DataArry) {
            return "_value";
        } else if (this == ReferenceArray) {
            return "_id";
        }

        return "NO";
    }


}
