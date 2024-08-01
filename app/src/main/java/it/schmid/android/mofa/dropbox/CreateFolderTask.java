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
    private final DbxClientV2 dbxClient;

    private final Context context;

    CreateFolderTask(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;

        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            dbxClient.files().createFolderV2(Globals.EXPORT);
            dbxClient.files().createFolderV2(Globals.IMPORT);
            dbxClient.files().createFolderV2(Globals.IMPORT + "/land");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/vquarter");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/worker");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/machine");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/task");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/pesticide");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/fertilizer");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/category");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/soilfertilizer");
            dbxClient.files().createFolderV2(Globals.IMPORT + "/extra");
            Log.d("CreateFolderTask", "Success - Creating Folders");
        } catch (DbxException e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //Toast.makeText(context, "Folder created successfully", Toast.LENGTH_SHORT).show();
    }
}
