package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by schmida on 22.07.16.
 */
public class DropboxClient {
    public static DbxClientV2 getClient(String ACCESS_TOKEN) {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/mofa-app", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }
    public static String retrieveAccessToken(Context context) {
        //check if ACCESS_TOKEN is previously stored on previous app launches
        SharedPreferences prefs = context.getSharedPreferences("it.schmid.android.mofa", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        if (accessToken == null) {
            Log.d("AccessToken Status", "No token found");
            return null;
        } else {
            //accessToken already exists
            Log.d("AccessToken Status", "Token exists");
            return accessToken;
        }
    }

}
