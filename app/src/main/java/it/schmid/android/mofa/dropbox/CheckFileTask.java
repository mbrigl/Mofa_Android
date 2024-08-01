package it.schmid.android.mofa.dropbox;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import java.util.ArrayList;

import it.schmid.android.mofa.Globals;


/**
 * Created by schmida on 22.07.16.
 */
public class CheckFileTask extends AsyncTask<String, Void, ArrayList<Integer>> {
    String[] elements = {"/land", "/vquarter", "/machine", "/worker", "/task", "/pesticide", "/fertilizer", "/soilfertilizer", "/category", "/extra", "/reason", "/weather"};

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;
    private final ArrayList<Integer> selElements = new ArrayList<Integer>();
    private final String[] mElementDesc;
    private final StringBuilder sb = new StringBuilder();

    public interface Callback {
        void onDataLoaded(ArrayList<Integer> result, StringBuilder sb);

        void onError(Exception e);
    }

    public CheckFileTask(DbxClientV2 dbxClient, String[] elementDesc, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
        mElementDesc = elementDesc;


    }

    @Override
    protected void onPostExecute(ArrayList<Integer> result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDataLoaded(result, sb);
        }
    }

    @Override
    protected ArrayList<Integer> doInBackground(String... params) {
        String fileName = params[0];
        int pos = 1;
        for (String element : elements) {
            try {
                String path = Globals.IMPORT + element + fileName;
                mDbxClient.files().getMetadata(path);

                sb.append(mElementDesc[pos - 1]);
                sb.append("\n");
                selElements.add(pos);
            } catch (DbxException e) {
                Log.d("Error Dropbox", e.getLocalizedMessage());
            }
            pos++;
        }
        return selElements;
    }
}

