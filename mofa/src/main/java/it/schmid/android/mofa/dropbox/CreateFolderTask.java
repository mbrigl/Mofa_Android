package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxPathV2;

/**
 * Created by schmida on 22.07.16.
 */
public class CreateFolderTask extends AsyncTask {
    private DbxClientV2 dbxClient;

    private Context context;
    private static final String exportPath = "/MoFaBackend/export";
    private static final String importPath = "/MoFaBackend/import";
    CreateFolderTask(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;

        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            dbxClient.files().createFolder(exportPath);
            dbxClient.files().createFolder(importPath);
            dbxClient.files().createFolder(importPath +"/land");
            dbxClient.files().createFolder(importPath +"/vquarter");
            dbxClient.files().createFolder(importPath + "/worker");
            dbxClient.files().createFolder(importPath + "/machine");
            dbxClient.files().createFolder(importPath + "/task");
            dbxClient.files().createFolder(importPath + "/pesticide");
            dbxClient.files().createFolder(importPath + "/fertilizer");
            dbxClient.files().createFolder(importPath + "/category");
            dbxClient.files().createFolder(importPath + "/soilfertilizer");
            dbxClient.files().createFolder(importPath + "/extra");

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
