package it.schmid.android.mofa.model;


import com.j256.ormlite.field.DatabaseField;

public class WorkVQuarter {
    public final static String WORK_ID_FIELD_NAME = "work_id";
    public final static String VQUARTER_ID_FIELD_NAME = "vquarter_id";

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true, columnName = WORK_ID_FIELD_NAME)
    private Work work;

    @DatabaseField(foreign = true, columnName = VQUARTER_ID_FIELD_NAME)
    private VQuarter vquarter;

    @DatabaseField
    private Double amount;

    public WorkVQuarter() {
    }

    public WorkVQuarter(Work work, VQuarter vquarter) {
        this.work = work;
        this.vquarter = vquarter;
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

    public VQuarter getVquarter() {
        return vquarter;
    }

    public void setVquarter(VQuarter vquarter) {
        this.vquarter = vquarter;
    }

    @Override
    public String toString() {
        return vquarter.getId().toString();
    }
}
