package it.schmid.android.mofa;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.preference.PreferenceManager;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

public class MofaApplication extends Application {
    public static final String NOTIFICATION_CHANNEL_ID = "mofa_sync";
    private static double defaultHour = 8.00;
    private OkHttpClient httpClient;
    private static MofaApplication instance;
    private ConcurrentHashMap<String, String> mGlobalVariables;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        httpClient = new OkHttpClient();
        mGlobalVariables = new ConcurrentHashMap<String, String>();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "MoFa Sync",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("MoFa data synchronization notifications");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public static MofaApplication getInstance() {
        return instance;
    }

    public String getGlobalVariable(String key) {
        return mGlobalVariables.get(key);
    }

    public void putGlobalVariable(String key, String value) {
        mGlobalVariables.put(key, value);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdownHttpClient();
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public String getBackendSoftware() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("listBackendFormat", "2");
    }

    private void shutdownHttpClient() {
        // OkHttpClient manages its own connection pool lifecycle
    }

    public boolean networkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    public static double getDefaultHour() {
        return defaultHour;
    }

    public static void setDefaultHour(double defaultHour) {
        MofaApplication.defaultHour = defaultHour;
    }
}
