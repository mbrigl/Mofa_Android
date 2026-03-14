package it.schmid.android.mofa.dropbox;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.schmid.android.mofa.PathConstants;


/**
 * Created by schmida on 22.07.16.
 */
public class CheckFileTask {
    private static final String[] ELEMENTS = {"/land", "/vquarter", "/machine", "/worker", "/task"};

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private final String[] mElementDesc;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @FunctionalInterface
    public interface Callback {
        void onDataLoaded(ArrayList<Integer> result, StringBuilder sb);
    }

    public CheckFileTask(DbxClientV2 dbxClient, String[] elementDesc, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
        mElementDesc = elementDesc;
    }

    public void execute(String fileName) {
        executor.execute(() -> {
            ArrayList<Integer> selElements = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            Exception error = null;
            int pos = 1;
            for (String element : ELEMENTS) {
                try {
                    String path = PathConstants.IMPORT + element + fileName;
                    mDbxClient.files().getMetadata(path);
                    sb.append(mElementDesc[pos - 1]);
                    sb.append("\n");
                    selElements.add(pos);
                } catch (DbxException e) {
                    Log.d("CheckFileTask", e.getLocalizedMessage());
                }
                pos++;
            }
            final ArrayList<Integer> result = selElements;
            final StringBuilder resultSb = sb;
            mainHandler.post(() -> mCallback.onDataLoaded(result, resultSb));
        });
    }

    public static void execute(DbxCredential credential, String[] elementDesc, String filename, Callback callback) {
        new CheckFileTask(DropboxClient.getClient(credential), elementDesc, callback).execute(filename);
    }
}
