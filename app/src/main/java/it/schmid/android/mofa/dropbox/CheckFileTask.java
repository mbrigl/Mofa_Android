package it.schmid.android.mofa.dropbox;

import android.os.Handler;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import it.schmid.android.mofa.Globals;


/**
 * Created by schmida on 22.07.16.
 */
public class CheckFileTask implements Runnable {

    private final Handler handler;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private final String[] titles;
    private final String filename;
    private final StringBuilder sb = new StringBuilder();

    Exception mException;
    final ArrayList<Integer> result = new ArrayList<Integer>();


    @FunctionalInterface
    public interface Callback {

        void onDataLoaded(ArrayList<Integer> result, StringBuilder sb, Exception e);
    }

    public CheckFileTask(DbxClientV2 dbxClient, String[] titles, String filename, Handler handler, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
        this.titles = titles;
        this.filename = filename;
        this.handler = handler;
    }

    public void run() {
        int pos = 1;
        for (Item i : Item.values()) {
            try {
                String path = Globals.IMPORT + "/" + i.name().toLowerCase() + filename;
                FileMetadata meta = (FileMetadata) mDbxClient.files().getMetadata(path);
                LocalDateTime lastModified = LocalDateTime.ofInstant(meta.getServerModified().toInstant(), ZoneId.systemDefault());

                sb.append(titles[pos - 1]);
                sb.append(": ");
                sb.append(lastModified.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                sb.append("\n");
                result.add(pos);
            } catch (DbxException e) {
                Log.d("Error Dropbox", e.getLocalizedMessage());
            }
            pos++;
        }

        handler.post(() -> mCallback.onDataLoaded(result, sb, mException));
    }
/*
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    executor.execute(() -> {
        //Background work here
        handler.post(() -> {
            //UI Thread work here
        });
    });
 */
}

