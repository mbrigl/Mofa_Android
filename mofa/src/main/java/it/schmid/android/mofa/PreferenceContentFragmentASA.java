package it.schmid.android.mofa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class PreferenceContentFragmentASA extends Fragment {
	private TextView asaTaxSettings=null;
	private TextView asaNoteSettings=null;
	private TextView asaNewVersion=null;
	private TextView asaCultivationType=null;
	private TextView asaLandOrder=null;
//	private TextView fertilizerCode=null;
//	private TextView fertilizerSoilCode=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View result=inflater.inflate(R.layout.prefscontentasa, parent, false);
		 asaTaxSettings=(TextView)result.findViewById(R.id.asa_tax_setting);
		 asaNoteSettings=(TextView)result.findViewById(R.id.asa_mofa_note);
		 asaNewVersion=(TextView)result.findViewById(R.id.asa_new_ver);
		 asaCultivationType=(TextView)result.findViewById(R.id.cultivationType);
		 asaLandOrder=(TextView)result.findViewById(R.id.asa_landorder_code);
		 //		 fertilizerCode=(TextView)result.findViewById(R.id.asa_fertilizer_code);
//		 fertilizerSoilCode=(TextView)result.findViewById(R.id.asa_fertilizer_soil_code);
 	    return(result);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
		asaTaxSettings.setText(Boolean.valueOf(prefs.getBoolean("asa_tax_setting", false)).toString());
		asaNoteSettings.setText(Boolean.valueOf(prefs.getBoolean("asa_mofa_note",false)).toString());
		asaLandOrder.setText(Boolean.valueOf(prefs.getBoolean("asa_landorder_code",false)).toString());
		asaNewVersion.setText(Boolean.valueOf(prefs.getBoolean("asa_new_ver",false)).toString());
		asaCultivationType.setText(prefs.getString("listCultivationType", "<unset>"));
//		fertilizerCode.setText(prefs.getString("asa_fertilizer_code", "BLATT"));
//		fertilizerSoilCode.setText(prefs.getString("asa_fertilizer_soil_code", "MD"));
		
	}
	
}
