package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import it.schmid.android.mofa.PathConstants;

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
            dbxClient.files().createFolderV2(PathConstants.EXPORT);
            dbxClient.files().createFolderV2(PathConstants.IMPORT);
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/land");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/vquarter");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/worker");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/machine");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/task");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/pesticide");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/fertilizer");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/category");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/soilfertilizer");
            dbxClient.files().createFolderV2(PathConstants.IMPORT + "/extra");

            // Upload to Dropbox
//            InputStream inputStream = new FileInputStream(file);
//            dbxClient.files().uploadBuilder("/" + file.getName()) //Path in the user's Dropbox to save the file.
//                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
//                    .uploadAndFinish(inputStream);
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
