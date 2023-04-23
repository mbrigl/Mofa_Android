package it.schmid.android.mofa;

import java.io.File;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;
@TargetApi(11)
public class WorkingJournalPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int res= getActivity().getResources().getIdentifier(getArguments().getString("resource"),
		                                    "xml",
		                                    getActivity().getPackageName());
		    addPreferencesFromResource(res);
		  
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		//Log.d("WorkingJournalPreferenceFragment", "calling listener");
		CheckBoxPreference offlineUpdate = (CheckBoxPreference)getPreferenceScreen().findPreference("updateOffline");
		CheckBoxPreference dropbox = (CheckBoxPreference)getPreferenceScreen().findPreference("dropbox");
		CheckBoxPreference dropboxReset = (CheckBoxPreference)getPreferenceScreen().findPreference("dropboxreset");
		ListPreference backendSoftware = (ListPreference)getPreferenceScreen().findPreference("listBackendFormat");
		ListPreference encodePref = (ListPreference)getPreferenceScreen().findPreference("listFormat");
		if (offlineUpdate.isChecked()){
		//	Log.d("WorkingJournalPreferenceFragment", "offlineupdate = true");
			if (isSdPresent()==true){
				
				createSdFolderStruct();
        	}else{
        		 Toast.makeText(getActivity(), "SD-Card not present or ready", Toast.LENGTH_LONG).show();
        	}
		}
		if (dropbox.isChecked()){
			//Toast.makeText(getActivity(), R.string.dropboxmsg, Toast.LENGTH_LONG).show();
			offlineUpdate.setChecked(false);
		}
		if (backendSoftware.getValue().equals("1")){ //ASA settings - we set preferences for ASA on Android 4.x
		//	Log.d("WorkingJournalPreferenceFragment", "Pref ASA ");
			dropbox.setChecked(true);
			encodePref.setValue("2");
		}
		if(dropboxReset.isChecked()){
			Toast.makeText(getActivity(), R.string.dropboxresetmessage, Toast.LENGTH_LONG).show();
		}

		   
	}
	 private void createSdFolderStruct(){
			if (isSdPresent()){ //SD-Card mounted
				File direct = new File(Environment.getExternalStorageDirectory() + PathConstants.PATH);
				if(!direct.exists())
					direct.mkdir();

				direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/land");
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
				direct = new File(Environment.getExternalStorageDirectory() + PathConstants.IMPORT + "/task");
				successful = direct.mkdirs();
				direct = new File(Environment.getExternalStorageDirectory() +PathConstants.IMPORT + "/machine");
				successful = direct.mkdirs();
			}
		}
		private boolean isSdPresent(){
			return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		}
		@Override
		public void onResume() {
		    super.onResume();
		    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

		@Override
		public void onPause() {
		    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		    super.onPause();
		}
}
