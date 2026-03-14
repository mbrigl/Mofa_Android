package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

import java.util.concurrent.TimeUnit;

import it.schmid.android.mofa.HomeActivity;
import it.schmid.android.mofa.PathConstants;
import okhttp3.OkHttpClient;

/**
 * Created by schmida on 22.07.16.
 */
public class DropboxClient {
    private static final String PREF_CREDENTIAL = "credential";

    public static DbxClientV2 getClient(DbxCredential credential) {
        try {
/*
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            X509TrustManager trustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
*/
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                  .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/mofa-app")
                    .withHttpRequestor(new OkHttp3Requestor(okHttpClient))
                    .build();
            return new DbxClientV2(config, credential);
        } catch (Exception e) {
            Log.e("DropboxClient", "SSL setup failed, falling back to default", e);
            DbxRequestConfig config = new DbxRequestConfig("dropbox/mofa-app", "en_US");
            return new DbxClientV2(config, credential);
        }
    }

    public static void authenticate(Context context, String appKey) {
        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("dropbox/mofa-app").build();
        Auth.startOAuth2PKCE(context, appKey, requestConfig, (DbxHost) null);
    }

    public static void getAccessToken(Context context) {
        DbxCredential credential = Auth.getDbxCredential();
        if (credential != null) {
            SharedPreferences prefs = context.getSharedPreferences(PathConstants.ID, Context.MODE_PRIVATE);
            prefs.edit().putString(PREF_CREDENTIAL, DbxCredential.Writer.writeToString(credential)).apply();
            new CreateFolderTask(DropboxClient.getClient(credential), context).execute();
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        }
    }

    public static DbxCredential retrieveCredential(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PathConstants.ID, Context.MODE_PRIVATE);
        String credentialStr = prefs.getString(PREF_CREDENTIAL, null);
        if (credentialStr == null) {
            Log.d("DropboxClient", "No credential found");
            return null;
        }
        try {
            return DbxCredential.Reader.readFully(credentialStr);
        } catch (Exception e) {
            Log.e("DropboxClient", "Failed to read credential", e);
            return null;
        }
    }

    public static boolean tokenExists(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PathConstants.ID, Context.MODE_PRIVATE);
        return prefs.getString(PREF_CREDENTIAL, null) != null;
    }

    public static void deleteAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PathConstants.ID, Context.MODE_PRIVATE);
        prefs.edit().remove(PREF_CREDENTIAL).remove("access-token").apply();
    }
}
