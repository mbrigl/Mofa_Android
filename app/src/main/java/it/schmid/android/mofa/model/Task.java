package it.schmid.android.mofa.model;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Task implements Entity {
    public static final String TAG = "TaskClass";

    @DatabaseField(id = true)

    private Integer id;
    @DatabaseField
    private String task;
    @DatabaseField
    private String data;
    @DatabaseField
    private String code;
    @DatabaseField
    private String type;
    @ForeignCollectionField
    private ForeignCollection<Work> works;
    private Boolean importError = false;


    public Task() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public void setItems(ForeignCollection<Work> works) {

        this.works = works;
    }


    @Override
    public boolean equals(Object obj) {
        //null instanceof Object will always return false
        if (!(obj instanceof Task))
            return false;
        if (obj == this)
            return true;
        return this.task.equalsIgnoreCase(((Task) obj).task);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = (id / 12) + 5;
        return result;
    }

    @Override
    public final <R, T> R accept(Entity.Visitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
