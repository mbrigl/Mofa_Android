package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by schmida on 24.04.17.
 */

public class Wartefrist {
    @SerializedName("Karenzzeit")
    @Expose
    private String karenzzeit;
    @SerializedName("Kultur")
    @Expose
    private String kultur;
    @SerializedName("Anbauart")
    @Expose
    private String anbauart;
    private int beeRestriction;
    public String getKarenzzeit() {
        return karenzzeit;
    }

    public void setKarenzzeit(String karenzzeit) {
        this.karenzzeit = karenzzeit;
    }

    public String getKultur() {
        return kultur;
    }

    public void setKultur(String kultur) {
        this.kultur = kultur;
    }

    public String getAnbauart() {
        return anbauart;
    }

    public void setAnbauart(String anbauart) {
        this.anbauart = anbauart;
    }

    public int getBeeRestriction() {
        return beeRestriction;
    }

    public void setBeeRestriction(int beeRestriction) {
        this.beeRestriction = beeRestriction;
    }
}
