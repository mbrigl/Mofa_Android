package it.schmid.android.mofa.model;

import it.schmid.android.mofa.NotificationService;

public abstract class ImportBehavior {

    public abstract Boolean importMasterData(String xmlString, NotificationService notification);
}
