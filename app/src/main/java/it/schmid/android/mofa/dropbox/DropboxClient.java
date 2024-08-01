package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

import it.schmid.android.mofa.Globals;
import it.schmid.android.mofa.HomeActivity;

/**
 * Created by schmida on 22.07.16.
 */
public interface DropboxClient {

    String IDENTIFIER = "dropbox/mofa-app";

    static DbxClientV2 getClient(String accessToken) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(IDENTIFIER).build();
        return new DbxClientV2(config, accessToken);
    }

    static void authenticate(Context context, String secret) {
        Auth.startOAuth2Authentication(context, secret);
    }

    static void getAccessToken(Context context, Class<?> activity) {
        DbxCredential credential = Auth.getDbxCredential(); //generate Access Token
        if (credential != null && credential.getAccessToken() != null) {
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences(Globals.ID, Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", credential.getAccessToken()).apply();
            new CreateFolderTask(DropboxClient.getClient(credential.getAccessToken()), context).execute(); //creating the folder-structure for MoFa

            //Proceed to HomeActivityActivity
            Intent intent = new Intent(context, activity);
            context.startActivity(intent);
        }
    }

    static String retrieveAccessToken(Context context) {
        //check if ACCESS_TOKEN is previously stored on previous app launches
        SharedPreferences prefs = context.getSharedPreferences(Globals.ID, Context.MODE_PRIVATE);
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

    static boolean tokenExists(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Globals.ID, Context.MODE_PRIVATE);
        return prefs.getString("access-token", null) != null;
    }

    static void deleteAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Globals.ID, Context.MODE_PRIVATE);
        prefs.edit().remove("access-token").commit();
    }
}
