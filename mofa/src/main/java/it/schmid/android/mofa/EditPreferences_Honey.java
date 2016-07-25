package it.schmid.android.mofa;

import java.util.List;



import android.annotation.SuppressLint;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class EditPreferences_Honey extends SherlockPreferenceActivity {

	@SuppressLint("NewApi")
	@Override
	  public void onBuildHeaders(List<Header> target) {
	    loadHeadersFromResource(R.xml.prference_headers, target);

	  }
	 
}
