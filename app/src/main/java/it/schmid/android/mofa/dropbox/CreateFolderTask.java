package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import it.schmid.android.mofa.Globals;

/**
 * Created by schmida on 22.07.16.
 */
public class CreateFolderTask implements Runnable {

    private final DbxClientV2 client;
    private final Context context;

    CreateFolderTask(DbxClientV2 client, Context context) {
        this.client = client;
        this.context = context;
    }

    public void run() {
        try {
            client.files().createFolderV2(Globals.EXPORT);
            client.files().createFolderV2(Globals.IMPORT);
            for (Item c : Item.values()) {
                client.files().createFolderV2(Globals.IMPORT + "/" + c.name().toLowerCase());
            }
            Log.d("CreateFolderTask", "Success - Creating Folders");
        } catch (DbxException e) {
            Log.e("CreateFolderTask", "Failure - Creating Folders", e);
        }
    }
}
