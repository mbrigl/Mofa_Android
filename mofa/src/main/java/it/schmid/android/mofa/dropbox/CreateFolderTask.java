package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.schmid.android.mofa.PathConstants;

/**
 * Created by schmida on 22.07.16.
 */
public class CreateFolderTask {
    private final DbxClientV2 dbxClient;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    CreateFolderTask(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;
        this.context = context;
    }

    public void execute() {
        executor.execute(() -> {
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
                Log.d("CreateFolderTask", "Success - Creating Folders");
            } catch (DbxException e) {
                Log.e("CreateFolderTask", "Error creating folders", e);
            }
        });
    }
}
