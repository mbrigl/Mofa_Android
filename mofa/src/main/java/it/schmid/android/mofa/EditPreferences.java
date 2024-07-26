package it.schmid.android.mofa;

import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


public class EditPreferences extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.prefs);

        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("updateOffline");
        final CheckBoxPreference dropboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("dropbox");
        final CheckBoxPreference showPestPref = (CheckBoxPreference) getPreferenceManager().findPreference("showPestInfos");
        final ListPreference backendSoftware = (ListPreference) getPreferenceManager().findPreference("listBackendFormat");
        final ListPreference encodePref = (ListPreference) getPreferenceManager().findPreference("listFormat");
        checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                if ((Boolean) newValue) {
                    if (isSdPresent()) {
                        createSdFolderStruct();
                    } else {
                        Toast.makeText(getApplicationContext(), "SD-Card not present or ready", Toast.LENGTH_LONG).show();
                    }

                }
                return true;
            }
        });
        dropboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                if ((Boolean) newValue) {
                    checkboxPref.setChecked(false);

                }
                return true;
            }
        });
        backendSoftware.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //	            if (newValue.toString().equalsIgnoreCase("1")){ //BIGAPPLE -- setting the settings for BigApple
//	            	Log.d("EditPreferences", "Pref BigApple " + preference.getKey() + " changed to " + newValue.toString()); 
//	            	dropboxPref.setChecked(true);
//	            	encodePref.setValue("2");
//	            }
                return true;
            }
        });
    }


    private void createSdFolderStruct() {
        if (isSdPresent()) { //SD-Card mounted
            File direct = new File(Environment.getExternalStorageDirectory() + PathConstants.PATH);
            if (!direct.exists())
                direct.mkdir();

            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/land");
            @SuppressWarnings("unused")
            boolean successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.EXPORT);
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/worker");
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/vquarter");
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/pesticide");
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/fertilizer");
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/soilfertilizer");
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/task");
            successful = direct.mkdirs();
            direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/machine");
            successful = direct.mkdirs();
        }
    }

    private boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
