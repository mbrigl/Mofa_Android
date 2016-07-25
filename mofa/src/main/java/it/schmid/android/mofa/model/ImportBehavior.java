package it.schmid.android.mofa.model;

import it.schmid.android.mofa.NotificationService;

import org.json.JSONArray;

public abstract class ImportBehavior {
	public abstract void importMasterData(JSONArray importData);
	public abstract Boolean importMasterData(String xmlString, NotificationService notification);
}
