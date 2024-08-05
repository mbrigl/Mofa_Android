package it.schmid.android.mofa;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.concurrent.ConcurrentHashMap;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.DropboxClient;

public class MofaApplication extends Application {

    private static MofaApplication INSTANCE;
    private static double defaultHour = 8.00;

    private final ConcurrentHashMap<String, String> mGlobalVariables;

    public MofaApplication() {
        INSTANCE = this;
        mGlobalVariables = new ConcurrentHashMap<String, String>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseManager.init(this);
    }

    public String getGlobalVariable(String key) {
        return mGlobalVariables.get(key);
    }

    public void putGlobalVariable(String key, String value) {
        mGlobalVariables.put(key, value);
        //notifyListeners (key,value);
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


    public void resetAuthentication() {
        String prefName = PreferenceManager.getDefaultSharedPreferencesName(this);
        SharedPreferences preferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);

        DropboxClient.deleteAccessToken(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit(); //resetting this preference to false
        editor.putBoolean("dropboxreset", false);
        editor.commit();

        Toast.makeText(this, R.string.dropboxresetmessage, Toast.LENGTH_LONG).show();
    }

    public static MofaApplication getInstance() {
        return INSTANCE;
    }
}
