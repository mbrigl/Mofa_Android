package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class WorkFertilizer {
	public final static String WORK_ID_FIELD_NAME = "work_id";
	public final static String SOILFERTILIZER_ID_FIELD_NAME = "soilfertilizer_id";
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(foreign = true, columnName = WORK_ID_FIELD_NAME)
	@Expose
	private Work work;
	@DatabaseField(foreign = true, columnName = SOILFERTILIZER_ID_FIELD_NAME)
	@Expose
	private SoilFertilizer soilFertilizer;
	@DatabaseField
	@Expose
	private Double amount;
	public WorkFertilizer(){
	}
	public WorkFertilizer(Work work, SoilFertilizer soilFertilizer){
		this.work = work;
		this.soilFertilizer= soilFertilizer;
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
	public SoilFertilizer getSoilFertilizer() {
		return soilFertilizer;
	}
	public void setSoilFertilizer(SoilFertilizer soilFertilizer) {
		this.soilFertilizer = soilFertilizer;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
}
