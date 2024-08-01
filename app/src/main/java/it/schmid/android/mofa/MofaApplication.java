package it.schmid.android.mofa;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MofaApplication extends Application {

    public static final Integer WORK_NORMAL = 1;

    private static double defaultHour = 8.00;
    private static MofaApplication instance;
    private ConcurrentHashMap<String, String> mGlobalVariables;
    private Set<AppStateListener> mAppStateListeners;

    private static Integer workType = WORK_NORMAL;

    public interface AppStateListener {
        void onStateChanged(String key, String value);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mGlobalVariables = new ConcurrentHashMap<String, String>();
        mAppStateListeners = Collections.synchronizedSet(new HashSet<AppStateListener>());
    }

    public static MofaApplication getInstance() {
        return instance;
    }

    public String getGlobalVariable(String key) {
        return mGlobalVariables.get(key);
    }

    public void putGlobalVariable(String key, String value) {
        mGlobalVariables.put(key, value);
        //notifyListeners (key,value);
    }

    public Integer getWorkType() {
        return workType;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



    public boolean networkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public static double getDefaultHour() {
        return defaultHour;
    }

    public static void setDefaultHour(double defaultHour) {
        MofaApplication.defaultHour = defaultHour;
    }
}
