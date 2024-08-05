package it.schmid.android.mofa.model;


import com.j256.ormlite.field.DatabaseField;

public class WorkWorker implements Entity {
    public final static String WORK_ID_FIELD_NAME = "work_id";
    public final static String WORKER_ID_FIELD_NAME = "worker_id";

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true, columnName = WORK_ID_FIELD_NAME)
    private Work work;

    @DatabaseField(foreign = true, columnName = WORKER_ID_FIELD_NAME)
    private Worker worker;

    @DatabaseField
    private Double hours;

    public WorkWorker() {
    }

    public WorkWorker(Work work, Worker worker) {
        this.work = work;
        this.worker = worker;
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

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    @Override
    public final <R, T> R accept(Entity.Visitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
