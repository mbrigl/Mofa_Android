package it.schmid.android.mofa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PreferenceContentFragmentASA extends SherlockFragment{
	private TextView asaTaxSettings=null;
	private TextView harvestWorkCode=null;
	private TextView sprayWorkCode=null;
	private TextView herbicideWorkCode=null;
	private TextView fertilizerWorkCode=null;
	private TextView fertilizerCode=null;
	private TextView fertilizerSoilCode=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View result=inflater.inflate(R.layout.prefscontentasa, parent, false);
		 asaTaxSettings=(TextView)result.findViewById(R.id.asa_tax_setting);
		 harvestWorkCode=(TextView)result.findViewById(R.id.asa_harvest_codes);
		 sprayWorkCode=(TextView)result.findViewById(R.id.asa_spray_code);
		 herbicideWorkCode=(TextView)result.findViewById(R.id.asa_herbicide_code);
		 fertilizerWorkCode=(TextView)result.findViewById(R.id.asa_soilfertilizer_codes);
		 fertilizerCode=(TextView)result.findViewById(R.id.asa_fertilizer_code);
		 fertilizerSoilCode=(TextView)result.findViewById(R.id.asa_fertilizer_soil_code);
 	    return(result);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
		asaTaxSettings.setText(new Boolean(prefs.getBoolean("asa_tax_setting", false)).toString());
		harvestWorkCode.setText(prefs.getString("asa_harvest_codes","510;515;520;530"));
		sprayWorkCode.setText(prefs.getString("asa_spray_code","310"));
		herbicideWorkCode.setText(prefs.getString("asa_herbicide_code","425"));
		fertilizerWorkCode.setText(prefs.getString("asa_soilfertilizer_codes","235"));
		fertilizerCode.setText(prefs.getString("asa_fertilizer_code", "BLATT"));
		fertilizerSoilCode.setText(prefs.getString("asa_fertilizer_soil_code", "MD"));
		
	}
	
}
