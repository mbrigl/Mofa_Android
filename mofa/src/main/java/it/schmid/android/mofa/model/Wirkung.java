package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by schmida on 24.04.17.
 */

public class Wirkung {

    @SerializedName("Grund")
    @Expose
    private String grund;
    @SerializedName("minDose")
    @Expose
    private Double minDose;
    @SerializedName("maxDose")
    @Expose
    private Double maxDose;
    @SerializedName("maxUseProYear")
    @Expose
    private Integer maxUseProYear;
    @SerializedName("maxAmountProUse")
    @Expose
    private Double maxAmountProUse;
    @SerializedName("maxUsageInSerie")
    @Expose
    private Integer maxUsageInSerie;
    @SerializedName("Kultur")
    @Expose
    private String kultur;
    @SerializedName("einsatzperiode")
    @Expose
    private String einsatzPeriode;
    @SerializedName("einsatzperCode")
    @Expose
    private String einsatzperCode;

    public String getGrund() {
        return grund;
    }

    public void setGrund(String grund) {
        this.grund = grund;
    }

    public Double getMinDose() {
        return minDose;
    }

    public void setMinDose(Double minDose) {
        this.minDose = minDose;
    }

    public Double getMaxDose() {
        return maxDose;
    }

    public void setMaxDose(Double maxDose) {
        this.maxDose = maxDose;
    }

    public Integer getMaxUseProYear() {
        return maxUseProYear;
    }

    public void setMaxUseProYear(Integer maxUseProYear) {
        this.maxUseProYear = maxUseProYear;
    }

    public Double getMaxAmountProUse() {
        return maxAmountProUse;
    }

    public void setMaxAmountProUse(Double maxAmountProUse) {
        this.maxAmountProUse = maxAmountProUse;
    }

    public Integer getMaxUsageInSerie() {
        return maxUsageInSerie;
    }

    public void setMaxUsageInSerie(Integer maxUsageInSerie) {
        this.maxUsageInSerie = maxUsageInSerie;
    }

    public String getKultur() {
        return kultur;
    }

    public void setKultur(String kultur) {
        this.kultur = kultur;
    }

    public String getEinsatzPeriode() {
        return einsatzPeriode;
    }

    public void setEinsatzPeriode(String einsatzPeriode) {
        this.einsatzPeriode = einsatzPeriode;
    }

    public String getEinsatzperCode() {
        return einsatzperCode;
    }

    public void setEinsatzperCode(String einsatzperCode) {
        this.einsatzperCode = einsatzperCode;
    }

    @Override
    public String toString() {
        return getGrund() + ", " + getKultur();
    }

    @Override
    public boolean equals(Object w) {

        if (w == this) return true;
        if (!(w instanceof Wirkung)) {
            return false;
        }
        Wirkung wirkung = (Wirkung) w;
        return
                Objects.equals(grund, wirkung.grund) &&
                        Objects.equals(kultur, wirkung.kultur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grund, kultur);
    }
}
