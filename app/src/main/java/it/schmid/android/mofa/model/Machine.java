package it.schmid.android.mofa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Machine implements Entity {
    public static final String TAG = "MachineClass";

    @DatabaseField(id = true)
    private Integer id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String code;

    public Machine() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return this.getName();
    }

    @Override
    public final <R, T> R accept(Entity.Visitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
