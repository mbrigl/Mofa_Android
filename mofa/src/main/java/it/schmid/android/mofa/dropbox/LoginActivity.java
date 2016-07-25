package it.schmid.android.mofa.dropbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;

import it.schmid.android.mofa.HomeActivity;
import it.schmid.android.mofa.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.APP_KEY));
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getAccessToken();
    }

    public void getAccessToken() {
        String accessToken = Auth.getOAuth2Token(); //generate Access Token
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("it.schmid.android.mofa", Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", accessToken).apply();
            new CreateFolderTask(DropboxClient.getClient(accessToken), this).execute(); //creating the folder-structure for MoFa
            //Proceed to HomeActivityActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
