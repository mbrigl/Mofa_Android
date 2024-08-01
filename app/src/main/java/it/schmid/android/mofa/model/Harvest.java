package it.schmid.android.mofa.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class Harvest {
    public final static String ID_FIELD_NAME = "id";
    @DatabaseField(id = true)

    private Integer id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Work work;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)

    private FruitQuality fruitQuality;
    @DatabaseField(dataType = DataType.DATE_LONG)

    private Date date;
    @DatabaseField

    private Integer amount;
    @DatabaseField

    private Integer pass = 1;
    @DatabaseField

    private Integer boxes;
    @DatabaseField

    private String note;
    @DatabaseField

    private Double sugar;
    @DatabaseField

    private Double phValue;
    @DatabaseField

    private Double acid;
    @DatabaseField

    private Double phenol;

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

    public FruitQuality getFruitQuality() {
        return fruitQuality;
    }

    public void setFruitQuality(FruitQuality fruitQuality) {
        this.fruitQuality = fruitQuality;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public void setBoxes(Integer boxes) {
        this.boxes = boxes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getSugar() {
        return sugar;
    }

    public void setSugar(Double sugar) {
        this.sugar = sugar;
    }

    public Double getPhValue() {
        return phValue;
    }

    public void setPhValue(Double phValue) {
        this.phValue = phValue;
    }

    public Double getAcid() {
        return acid;
    }

    public void setAcid(Double acid) {
        this.acid = acid;
    }

    public Double getPhenol() {
        return phenol;
    }

    public void setPhenol(Double phenol) {
        this.phenol = phenol;
    }

    public Integer getPass() {
        return pass;
    }

    public void setPass(Integer pass) {
        this.pass = pass;
    }

}
