package it.schmid.android.mofa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;

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

    @Override
    public void importMasterData(JSONArray importData) {

    }

    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        return null;
    }
}
