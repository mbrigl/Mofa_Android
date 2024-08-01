package it.schmid.android.mofa;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MofaApplication extends Application {

    public static final Integer WORK_NORMAL = 1;

    static final String TAG = "MofaApplication";
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

    public String removeGlobalVariable(String key) {
        String value = mGlobalVariables.remove(key);
        //notifyListeners (key,value);
        return value;
    }

    public void putGlobalVariable(String key, String value) {
        mGlobalVariables.put(key, value);
        //notifyListeners (key,value);
    }

    public Integer getWorkType() {
        return workType;
    }

    public static void setWorkType(Integer wType) {
        workType = wType;

    }

    public void addAppStateListener(AppStateListener appStateListener) {
        mAppStateListeners.add(appStateListener);
    }

    public void removeAppStateListener(AppStateListener appStateListener) {
        mAppStateListeners.remove(appStateListener);
    }

    private void notifyListeners(String key, String value) {
        for (AppStateListener appStateListener : mAppStateListeners) {
            appStateListener.onStateChanged(key, value);
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Boolean newAsaVersion() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("asa_new_ver", false);
    }

    public String getLeafFertilizerCodeASA() {
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("fertilizerleafcode", "BLATT");
    }

    public String getSoilFertilizerCodeASA() {
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("fertilizersoilcode", "MD");
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
