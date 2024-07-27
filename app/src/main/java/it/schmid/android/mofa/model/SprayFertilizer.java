package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class SprayFertilizer {
    public final static String SPRAY_ID_FIELD_NAME = "spray_id";
    public final static String FERTILIZER_ID_FIELD_NAME = "fert_id";
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, columnName = FERTILIZER_ID_FIELD_NAME)
    @Expose
    private Fertilizer fertilizer;
    @DatabaseField(foreign = true, columnName = SPRAY_ID_FIELD_NAME)
    @Expose
    private Spraying spraying;
    @DatabaseField
    @Expose
    private Double dose;
    @DatabaseField
    @Expose
    private Double dose_amount;

    public SprayFertilizer() {
    }

    public SprayFertilizer(Fertilizer fertilizer, Spraying spraying) {
        this.fertilizer = fertilizer;
        this.spraying = spraying;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public Spraying getSpraying() {
        return spraying;
    }

    public void setSpraying(Spraying spraying) {
        this.spraying = spraying;
    }

    public Double getDose() {
        return dose;
    }

    public void setDose(Double dose) {
        this.dose = dose;
    }

    public Double getDose_amount() {
        return dose_amount;
    }

    public void setDose_amount(Double dose_amount) {
        this.dose_amount = dose_amount;
    }

}
