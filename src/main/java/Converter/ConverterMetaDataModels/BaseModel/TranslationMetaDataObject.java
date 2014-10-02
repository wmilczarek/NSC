package Converter.ConverterMetaDataModels.BaseModel;


public abstract class TranslationMetaDataObject {


    protected String metaDataObjectName;

    public TranslationMetaDataObject(String metaDataObjectName) {
        this.metaDataObjectName = metaDataObjectName;
    }

    public String getMetaDataObjectName() {
        return metaDataObjectName;
    }

    public void setMetaDataObjectName(String metaDataObjectName) {
        this.metaDataObjectName = metaDataObjectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslationMetaDataObject that = (TranslationMetaDataObject) o;

        if (metaDataObjectName != null ? !metaDataObjectName.equals(that.metaDataObjectName) : that.metaDataObjectName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return metaDataObjectName != null ? metaDataObjectName.hashCode() : 0;
    }
}