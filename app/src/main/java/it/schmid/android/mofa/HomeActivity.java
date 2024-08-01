package it.schmid.android.mofa;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.dropbox.CheckFileTask;
import it.schmid.android.mofa.dropbox.DropboxClient;
import it.schmid.android.mofa.dropbox.LoginActivity;
import it.schmid.android.mofa.dropbox.SendingProcess;
import it.schmid.android.mofa.dropbox.SendingProcess.RemoveEntries;
import it.schmid.android.mofa.dropbox.WebServiceCall;
import it.schmid.android.mofa.model.Work;


public class HomeActivity extends AppCompatActivity implements RemoveEntries {
    private static final String TAG = "HomeActivity";
    public static final int NUM_HOME_BUTTONS = 3;
    /**
     * // setting from preferences
     */
    private Boolean resetDropbox;
    private String urlPath;
    private MofaApplication app;

    private SharedPreferences preferences;

    //Dropbox variable
    private String ACCESS_TOKEN;


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
        DatabaseManager.init(this);
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
                startActivity(new Intent(this, EditPreferences_Honey.class));
                break;
            case 3:
                preferences = PreferenceManager.getDefaultSharedPreferences(this);
                urlPath = preferences.getString("url", "");
                resetDropbox = preferences.getBoolean("dropboxreset", false);

                if (resetDropbox) { //resetting the link if enabled in preferences
                    DropboxClient.deleteAccessToken(this);
                    Editor editor = preferences.edit(); //resetting this preference to false
                    editor.putBoolean("dropboxreset", false);
                    editor.commit();
                }

                if (DropboxClient.tokenExists(this)) { //Dropbox API V2 - check if Token exists
                    ACCESS_TOKEN = DropboxClient.retrieveAccessToken(this);
                    importFromDropbox();
                } else {
                    //No token
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param selItems contains an ArrayList of integers to decide which tables are to update
     * @param url      is the URL of the webservice entry point
     */
    private void updateData(ArrayList<Integer> selItems, String url) {
        @SuppressWarnings("unused")
        WebServiceCall importData = new WebServiceCall(this, DropboxClient.getClient(ACCESS_TOKEN));
        importData.execute(selItems, url);
    }

    //deleting the table entries
    private void flushData(ArrayList<Integer> selItems) {
        for (Integer i : selItems) {
            switch (i) {
                case 1:
                    DatabaseManager.getInstance().flushVQuarter(); //deleting vquarter too
                    DatabaseManager.getInstance().flushLand();
                    break;
                case 2:
                    DatabaseManager.getInstance().flushVQuarter();
                    break;
                case 3:
                    DatabaseManager.getInstance().flushMachine();
                    break;
                case 4:
                    DatabaseManager.getInstance().flushWorker();
                    break;
                case 5:
                    DatabaseManager.getInstance().flushTask();
                    break;
                default:
                    break;

            }
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
		/*case R.id.menu_test:
			Log.d(TAG, "automatically filling DB");
			DatabaseTestDB.init(this);
			DatabaseTestDB.getInstance().createTestRecords();
			return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    private void showAlertDialog(StringBuilder sb, final ArrayList<Integer> selElements) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle(R.string.importTitle);
        // Adding a checkbox for reimport all data
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText("Reimport ALL DATA");
        //   if (Integer.parseInt(backEndSoftware)==1){ //special case ASA - we need to delete local data
        //   	checkBox.setChecked(true);
        //   	checkBox.setClickable(false);
        //   }
        Log.d(TAG, "Array contains: " + selElements.toString());
        // Adding a listener for the checkbox with a
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((isChecked) && (DatabaseManager.getInstance().getAllNotSendedWorks().size() > 0)) {
                    Toast.makeText(getApplicationContext(), R.string.reimportmessage, Toast.LENGTH_LONG).show();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(checkBox);
        alertDialog.setView(linearLayout);
        alertDialog.setMessage(sb);
        alertDialog.setPositiveButton(R.string.yesbutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    if (DatabaseManager.getInstance().getAllNotSendedWorks().size() == 0) { // works table is empty
                        flushData(selElements);
                        updateData(selElements, Globals.IMPORT); //starting the import of dropbox data
                    } else { // works table not empty first export
                        //Toast.makeText(getApplicationContext(), R.string.reimportmessage,Toast.LENGTH_LONG).show();
                        if (DatabaseManager.getInstance().getAllWorks().size() != 0) {
                            SendingProcess sending = new SendingProcess(HomeActivity.this); //first make the export
                            sending.sendData();
                            Toast.makeText(getApplicationContext(), R.string.export_status_message, Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    // the standard case, only a update
                    updateData(selElements, Globals.IMPORT);
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(R.string.nobutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void showNoUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle(R.string.noUpdatesInfo);
        alertDialog.setMessage(R.string.noupdate)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    /**
     * DropBox Operation for import
     */
    private void importFromDropbox() {
        final ProgressDialog waitingSpinner = new ProgressDialog(this);
        waitingSpinner.setTitle(getString(R.string.waitingspinnertitle));
        waitingSpinner.setMessage(getString(R.string.waitingspinnertext));
        waitingSpinner.show();
        String extension;
        String filename = "/list"; //the filename is always list
        final ArrayList<Integer> selElements = new ArrayList<Integer>(); //storing the elements to import/update
        final String[] elementDesc = {getString(R.string.landtable), getString(R.string.vquartertable), getString(R.string.machinetable),
                getString(R.string.workertable), getString(R.string.tasktable)};
        StringBuilder sb = new StringBuilder();
        boolean first = false; //only for checking if \n
        extension = ".xml";
        filename = filename + extension;
        new CheckFileTask(DropboxClient.getClient(ACCESS_TOKEN), elementDesc, new CheckFileTask.Callback() {

            @Override
            public void onDataLoaded(ArrayList<Integer> result, StringBuilder sb) {
                if (result.size() > 0) {
                    waitingSpinner.dismiss();
                    showAlertDialog(sb, result);

                } else { // no updates
                    waitingSpinner.dismiss();
                    showNoUpdateDialog();
                }

            }

            @Override
            public void onError(Exception e) {

            }
        }).execute(filename);
//

    }


    public void deleteAllEntries() {
        List<Work> workList;
        workList = DatabaseManager.getInstance().getAllWorksOrderByDate();
        for (Work w : workList) {
            DatabaseManager.getInstance().deleteCascWork(w);
        }
    }
}
