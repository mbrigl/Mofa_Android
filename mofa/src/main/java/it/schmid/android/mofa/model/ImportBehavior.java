package it.schmid.android.mofa.model;

import org.json.JSONArray;

import it.schmid.android.mofa.NotificationService;

public interface ImportBehavior {
    void importMasterData(JSONArray importData);

    boolean importMasterData(String xmlString, NotificationService notification);
}
