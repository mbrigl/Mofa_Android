package it.schmid.android.mofa.model;

import org.json.JSONArray;

import it.schmid.android.mofa.NotificationService;

public abstract class ImportBehavior {
    public abstract void importMasterData(JSONArray importData);

    public abstract Boolean importMasterData(String xmlString, NotificationService notification);
}
