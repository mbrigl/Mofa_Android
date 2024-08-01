package it.schmid.android.mofa;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.DropboxClient;
import it.schmid.android.mofa.dropbox.LoginActivity;
import it.schmid.android.mofa.dropbox.SendingProcess.RemoveEntries;
import it.schmid.android.mofa.model.Work;


public class HomeActivity extends AppCompatActivity implements RemoveEntries {
    private static final String TAG = "HomeActivity";
    public static final int NUM_HOME_BUTTONS = 3;

    private MofaApplication app;
    private DatabaseSync sync;


    // Image resources for the buttons
    private final Integer[] mImageIds = {
            R.drawable.home_button1,
            R.drawable.home_button2,
            R.drawable.home_button3
    };

    // Labels for the buttons
    private final Integer[] mLabelIds = {
            R.string.title_feature1,
            R.string.title_feature2,
            R.string.title_feature3
    };

    // Ids for the frames that define where the images go
    private final Integer[] mFrameIds = {
            R.id.frame1,
            R.id.frame2,
            R.id.frame3
    };

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //
        // Add the buttons that make up the Dashboard.
        // We do this with a LayoutInflater. Doing it that way gives us more control
        // over the size of the images and labels. Size values are defined in the layout
        // for the image button (see activity_home_button.xml). Since each of the different screen
        // sizes has their own dimens.xml file, you can adjust the sizes and scaling as needed.
        // (Values folders: values, values-xlarge, values-sw600dp, values-sw720p)
        //
        LayoutInflater li = this.getLayoutInflater();
        int imageButtonLayoutId = R.layout.activity_home_button;
        for (int j = 0; j < NUM_HOME_BUTTONS; j++) {
            int frameId = mFrameIds[j];
            int labelId = mLabelIds[j];
            int imageId = mImageIds[j];

            // Inflate a view for the image button. Set its image and label.
            View v = li.inflate(imageButtonLayoutId, null);
            ImageView iv = v.findViewById(R.id.home_btn_image);
            // if (iv != null) iv.setImageDrawable (imageId);
            if (iv != null) {
                iv.setImageResource(imageId);
                // Assign a value for the tag so the onClickFeature handler can determine which button was clicked.
                iv.setTag(Integer.valueOf(j + 1));
            }
            TextView tv = v.findViewById(R.id.home_btn_label);
            if (tv != null) tv.setText(labelId);


            // Find the frame where the image goes.
            // Attach the inflated view to that frame.
            View buttonView = v;
            FrameLayout frame = findViewById(frameId);
            if (frame != null) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                Gravity.CENTER);
                frame.addView(buttonView, lp);
            }

        }
        app = MofaApplication.getInstance();

    }

    @Override
    protected void onResume() {
        String sBackEnd;
        super.onResume();
        //setting title with additional info
        sBackEnd = "ASA";
        getSupportActionBar().setTitle("MoFa - " + sBackEnd);

    }

    public void onClickFeature(View v) {
        Integer featureNum = (Integer) v.getTag();
        if (featureNum == null) return;

        switch (featureNum) {
            case 1:
                if (DatabaseManager.getInstance().checkIfEmpty()) {
                    Toast.makeText(this, R.string.nodata, Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(this, WorkOverviewActivity.class));

                }

                break;
            case 2:
                MofaApplication app = (MofaApplication) getApplication();
                app.resetAuthentication();
                break;
            case 3:
                if (DropboxClient.tokenExists(this)) { //Dropbox API V2 - check if Token exists
                    sync = new DatabaseSync(DropboxClient.retrieveAccessToken(this), this);
                    sync.importFromDropbox();
                } else {
                    //No token
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //inflating the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }
    // Reaction to the menu selection

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            Log.d(TAG, "showing about dialog");
            AboutDialog about = new AboutDialog(this);
            about.setTitle("about MoFa");
            about.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void deleteAllEntries() {
        List<Work> workList;
        workList = DatabaseManager.getInstance().getAllWorksOrderByDate();
        for (Work w : workList) {
            DatabaseManager.getInstance().deleteCascWork(w);
        }
    }
}
