package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import it.schmid.android.mofa.Globals;

/**
 * Created by schmida on 22.07.16.
 */
public class CreateFolderTask extends AsyncTask {

    private final DbxClientV2 client;
    private final Context context;

    CreateFolderTask(DbxClientV2 client, Context context) {
        this.client = client;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            client.files().createFolderV2(Globals.EXPORT);
            client.files().createFolderV2(Globals.IMPORT);
            client.files().createFolderV2(Globals.IMPORT + "/land");
            client.files().createFolderV2(Globals.IMPORT + "/vquarter");
            client.files().createFolderV2(Globals.IMPORT + "/worker");
            client.files().createFolderV2(Globals.IMPORT + "/machine");
            client.files().createFolderV2(Globals.IMPORT + "/task");
            Log.d("CreateFolderTask", "Success - Creating Folders");
        } catch (DbxException e) {
            Log.e("CreateFolderTask", "Failure - Creating Folders", e);
        }
        return null;
    }
}
