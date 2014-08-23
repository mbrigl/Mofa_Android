package it.schmid.android.mofa;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class EditPreferencesImport extends SherlockPreferenceActivity{
	@SuppressWarnings("deprecation")
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    
	      addPreferencesFromResource(R.xml.prefs_import);
	     
	   
	}
}
