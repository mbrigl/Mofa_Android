package it.schmid.android.mofa.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

import it.schmid.android.mofa.model.Work;

public class WorkLoader extends AsyncTaskLoader<List<Work>> {
    private static final String TAG = "WorkLoader";
    Receiver receiver;
    public final static String RELOAD = "WorkLoader.RELOAD";
    private List<Work> mData;

    public WorkLoader(Context context) {
        super(context);

    }

    @Override
    public List<Work> loadInBackground() {
        Log.d(TAG, "Loading Data in a Background Process");
        return DatabaseManager.getInstance().getAllWorksOrderByDate();

    }

    @Override
    public void deliverResult(List<Work> data) {
        if (isReset()) {
            return;
        }
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
        // Log.d(TAG,"Delivered results");
    }

    @Override
    protected void onReset() {
        getContext().unregisterReceiver(receiver);
        onStopLoading();
        if (mData != null) {
            mData = null;
        }
        Log.d(TAG, "onReset, setting data to null");
    }

    @Override
    protected void onStartLoading() {
        receiver = new Receiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RELOAD);
        getContext().registerReceiver(receiver, filter);
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
        //	forceLoad();
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    public class Receiver extends BroadcastReceiver {
        WorkLoader loader;

        public Receiver(WorkLoader loader) {
            this.loader = loader;
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            loader.onContentChanged();

        }
    }


}

