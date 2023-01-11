package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class SprayPesticide {
	public final static String SPRAY_ID_FIELD_NAME = "spray_id";
	public final static String PESTICIDE_ID_FIELD_NAME = "pest_id";
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(foreign = true, columnName = PESTICIDE_ID_FIELD_NAME)
	@Expose
	private Pesticide pesticide;
	@DatabaseField(foreign = true, columnName = SPRAY_ID_FIELD_NAME)
	@Expose
	private Spraying spraying;
	@DatabaseField
	@Expose
	private Double dose;
	@DatabaseField
	@Expose
	private Double dose_amount;
	@DatabaseField
	@Expose
	private String reason;
	@DatabaseField
	@Expose
	private String periodCode;
	
	public SprayPesticide(){
			}
	public SprayPesticide(Pesticide pesticide, Spraying spraying){
		this.pesticide=pesticide;
		this.spraying=spraying;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Pesticide getPesticide() {
		return pesticide;
	}
	public void setPesticide(Pesticide pesticide) {
		this.pesticide = pesticide;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPeriodCode() {
		return periodCode;
	}

	public void setPeriodCode(String periodCode) {
		this.periodCode = periodCode;
	}
}
