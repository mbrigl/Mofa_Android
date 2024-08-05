package it.schmid.android.mofa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Worker implements Entity {
    public static final String TAG = "WorkerClass";

    @DatabaseField(id = true)

    private Integer id;
    @DatabaseField
    private String lastname;
    @DatabaseField
    private String firstname;
    @DatabaseField
    private String code;

    private Boolean importError = false;

    public Worker() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.lastname + " " + this.firstname;
    }

    @Override
    public final <R, T> R accept(Entity.Visitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
