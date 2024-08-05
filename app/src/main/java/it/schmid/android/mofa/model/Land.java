package it.schmid.android.mofa.model;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable
public class Land implements Entity {

    public static final String TAG = "LandClass";

    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String code;

    @ForeignCollectionField(orderColumnName = "code")
    private ForeignCollection<VQuarter> vquarters;


    public Land() {

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

    public void setItems(ForeignCollection<VQuarter> vquarters) {
        this.vquarters = vquarters;
    }

    public List<VQuarter> getVQuarters() {
        ArrayList<VQuarter> vquarterList = new ArrayList<VQuarter>();
        for (VQuarter vquarter : vquarters) {
            vquarterList.add(vquarter);
        }
        return vquarterList;
    }

    @Override
    public final <R, T> R accept(Entity.Visitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
