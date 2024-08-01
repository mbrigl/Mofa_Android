package it.schmid.android.mofa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import it.schmid.android.mofa.NotificationService;

/**
 * Created by schmida on 26.01.16.
 */
@DatabaseTable
public class Global extends ImportBehavior {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String typeInfo;
    @DatabaseField
    private String data;
    @DatabaseField
    private Integer workId;

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(String typeInfo) {
        this.typeInfo = typeInfo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }
}
