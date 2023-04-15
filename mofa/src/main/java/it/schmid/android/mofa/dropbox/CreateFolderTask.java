package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxPathV2;

import it.schmid.android.mofa.PathConstants;

/**
 * Created by schmida on 22.07.16.
 */
public class CreateFolderTask extends AsyncTask {
    private DbxClientV2 dbxClient;

    private Context context;
    CreateFolderTask(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;

        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            dbxClient.files().createFolder(PathConstants.EXPORT);
            dbxClient.files().createFolder(PathConstants.IMPORT);
            dbxClient.files().createFolder(PathConstants.IMPORT + "/land");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/vquarter");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/worker");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/machine");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/task");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/pesticide");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/fertilizer");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/category");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/soilfertilizer");
            dbxClient.files().createFolder(PathConstants.IMPORT + "/extra");

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
