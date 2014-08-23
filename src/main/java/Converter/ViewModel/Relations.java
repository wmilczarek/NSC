package Converter.ViewModel;

/**
 * Created by szef on 2014-08-12.
 */
public enum Relations {

    None(),
    PrimaryKey(),
    ForeginKey();

    private String inRelationEntity;

    public String getInRealtaionField() {
        return inRealtaionField;
    }

    public void setInRealtaionField(String inRealtaionField) {
        this.inRealtaionField = inRealtaionField;
    }

    private String inRealtaionField;

    public String getInRelationEntity() {
        return inRelationEntity;
    }

    public void setInRelationEntity(String inRelationEntity) {
        this.inRelationEntity = inRelationEntity;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
