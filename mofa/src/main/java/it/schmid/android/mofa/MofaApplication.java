package it.schmid.android.mofa;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MofaApplication extends Application {
    public static final Integer WORK_NORMAL = 1;
    public static final Integer WORK_SPRAY = 2;
    public static final Integer WORK_FERT = 3;

    static final String TAG = "MofaApplication";
    private static double defaultHour = 8.00;
    private HttpClient httpClient;
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
        httpClient = createHttpClient();
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
        shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdownHttpClient();
    }

    private HttpClient createHttpClient() {
        Log.d(TAG, "createHttpClient()...");
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http",
                PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new
                ThreadSafeClientConnManager(params, schReg);
        return new DefaultHttpClient(conMgr, params);
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String getBackendSoftware() {
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("listBackendFormat", "2");
    }

    public void setBackendSoftware(String backEnd) {

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

    private void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
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
