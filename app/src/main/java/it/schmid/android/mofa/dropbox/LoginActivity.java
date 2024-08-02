package it.schmid.android.mofa.dropbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.schmid.android.mofa.MainActivity;
import it.schmid.android.mofa.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DropboxClient.authenticate(getApplicationContext(), getString(R.string.APP_KEY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        DropboxClient.getAccessToken(this, MainActivity.class);
    }
}
