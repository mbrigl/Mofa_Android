package it.schmid.android.mofa;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import it.schmid.android.mofa.databinding.ActivityMainBinding;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.DropboxClient;
import it.schmid.android.mofa.dropbox.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private DatabaseSync sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> {
            if (DatabaseManager.getInstance().checkIfEmpty()) {
                Toast.makeText(this, R.string.nodata, Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(this, WorkOverviewActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_sync) {
            if (DropboxClient.tokenExists(this)) { //Dropbox API V2 - check if Token exists
                sync = new DatabaseSync(DropboxClient.retrieveAccessToken(this), this);
                sync.importFromDropbox();
            } else {
                //No token
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            return true;
        }

        if (id == R.id.action_reset) {
            MofaApplication app = (MofaApplication) getApplication();
            app.resetAuthentication();
            return true;
        }

        if (id == R.id.action_settings) {
            Snackbar.make(binding.getRoot(), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}