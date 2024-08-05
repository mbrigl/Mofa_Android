package it.schmid.android.mofa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class VQuarter implements Entity {
    public static final String TAG = "VQuarterClass";
    @DatabaseField(id = true)

    private Integer id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Land land;
    @DatabaseField
    private String variety;
    @DatabaseField
    private String clone;
    @DatabaseField
    private Integer plantYear;
    @DatabaseField
    private Double wateramount;
    @DatabaseField
    private Double size;
    @DatabaseField
    private String code;
    @DatabaseField
    private String data;
    @DatabaseField
    private Double gps_x1;
    @DatabaseField
    private Double gps_x2;
    @DatabaseField
    private Double gps_y1;
    @DatabaseField
    private Double gps_y2;
    private Boolean importError = false;

    public VQuarter() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Land getLand() {
        return land;
    }

    public void setLand(Land land) {
        this.land = land;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getClone() {
        return clone;
    }

    public void setClone(String clone) {
        this.clone = clone;
    }

    public Integer getPlantYear() {
        return plantYear;
    }

    public void setPlantYear(Integer plantYear) {
        this.plantYear = plantYear;
    }

    public Double getWateramount() {
        return wateramount;
    }

    public void setWateramount(Double wateramount) {
        this.wateramount = wateramount;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getGps_x1() {
        return gps_x1;
    }

    public void setGps_x1(Double gps_x1) {
        this.gps_x1 = gps_x1;
    }

    public Double getGps_x2() {
        return gps_x2;
    }

    public void setGps_x2(Double gps_x2) {
        this.gps_x2 = gps_x2;
    }

    public Double getGps_y1() {
        return gps_y1;
    }

    public void setGps_y1(Double gps_y1) {
        this.gps_y1 = gps_y1;
    }

    public Double getGps_y2() {
        return gps_y2;
    }

    public void setGps_y2(Double gps_y2) {
        this.gps_y2 = gps_y2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public final <R, T> R accept(Entity.Visitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
