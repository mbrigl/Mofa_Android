package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class Spraying {
    public final static String WORK_ID_FIELD_NAME = "work_id";
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, columnName = WORK_ID_FIELD_NAME)
    @Expose
    private Work work;
    @DatabaseField
    private Double concentration;
    @DatabaseField
    private Double wateramount;
    @DatabaseField
    private Integer weather = 1;

    public Spraying() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Double getConcentration() {
        return concentration;
    }

    public void setConcentration(Double concentration) {
        this.concentration = concentration;
    }

    public Double getWateramount() {
        return wateramount;
    }

    public void setWateramount(Double wateramount) {
        this.wateramount = wateramount;
    }

    public Integer getWeather() {
        return weather;
    }

    public void setWeather(Integer weather) {
        this.weather = weather;
    }
}
