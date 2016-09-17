package it.schmid.android.mofa;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;



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
	            if ((Boolean)newValue==true){
	            	if (isSdPresent()==true){
	            		createSdFolderStruct();
	            	}else{
	            		 Toast.makeText(getApplicationContext(), "SD-Card not present or ready", Toast.LENGTH_LONG).show();
	            	}
	            		
	            }
	            return true;
	        }
	    }); 
	    dropboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {            
	        public boolean onPreferenceChange(Preference preference, Object newValue) {
	            Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());       
	            if ((Boolean)newValue==true){
	            	checkboxPref.setChecked(false);
	            	
	            }
	            return true;
	        }
	    }); 
	    backendSoftware.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {           
	        public boolean onPreferenceChange(Preference preference, Object newValue) {
	            ;       
//	            if (newValue.toString().equalsIgnoreCase("1")){ //BIGAPPLE -- setting the settings for BigApple
//	            	Log.d("EditPreferences", "Pref BigApple " + preference.getKey() + " changed to " + newValue.toString()); 
//	            	dropboxPref.setChecked(true);
//	            	encodePref.setValue("2");
//	            }
	            return true;
	        }
	    });
	  }
	  
	  
	  private void createSdFolderStruct(){
			if (isSdPresent()){ //SD-Card mounted
				File direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend");
				   if(!direct.exists())
				    {
				        if(direct.mkdir()) 
				          {
				           //directory is created;
				          }
				    }
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/land");
				 @SuppressWarnings("unused")
				boolean successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/export");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/worker");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/vquarter");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/pesticide");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/fertilizer");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/soilfertilizer");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/task");
				 successful = direct.mkdirs();
				 direct = new File(Environment.getExternalStorageDirectory() + "/MoFaBackend/import/machine");
				 successful = direct.mkdirs();
			}
		}
		private boolean isSdPresent(){
			return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		}
}
