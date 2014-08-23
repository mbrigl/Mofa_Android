package it.schmid.android.mofa.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class WorkMachine {
	public final static String WORK_ID_FIELD_NAME = "work_id";
	public final static String MACHINE_ID_FIELD_NAME = "machine_id";
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(foreign = true, columnName = WORK_ID_FIELD_NAME)
	@Expose
	private Work work;
	@DatabaseField(foreign = true, columnName = MACHINE_ID_FIELD_NAME)
	@Expose
	private Machine machine;
	@DatabaseField
	@Expose
	private Double hours;
	public WorkMachine(){
			}
	public WorkMachine(Work work, Machine machine){
		this.work = work;
		this.machine = machine;
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
	public Machine getMachine() {
		return machine;
	}
	public void setMachine(Machine machine) {
		this.machine = machine;
	}
	public Double getHours() {
		return hours;
	}
	public void setHours(Double hours) {
		this.hours = hours;
	}
	
}
