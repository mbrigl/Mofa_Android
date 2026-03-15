package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.schmid.android.mofa.MofaConstants;

/**
 * Created by schmida on 22.07.16.
 */
public class CreateFolderTask {
    private final DbxClientV2 dbxClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    CreateFolderTask(DbxClientV2 dbxClient, Context context) {
        this.dbxClient = dbxClient;
    }

    public void execute() {
        executor.execute(() -> {
            try {
                dbxClient.files().createFolderV2(MofaConstants.EXPORT);
                dbxClient.files().createFolderV2(MofaConstants.IMPORT);
                dbxClient.files().createFolderV2(MofaConstants.IMPORT + "/land");
                dbxClient.files().createFolderV2(MofaConstants.IMPORT + "/vquarter");
                dbxClient.files().createFolderV2(MofaConstants.IMPORT + "/worker");
                dbxClient.files().createFolderV2(MofaConstants.IMPORT + "/machine");
                dbxClient.files().createFolderV2(MofaConstants.IMPORT + "/task");
                Log.d("CreateFolderTask", "Success - Creating Folders");
            } catch (DbxException e) {
                Log.e("CreateFolderTask", "Error creating folders", e);
            }
        });
    }
}
