package it.schmid.android.mofa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PreferenceContentFragment extends SherlockFragment {
	  private TextView text=null;
	  private TextView list=null;
	  private TextView listbackend=null;
	  private TextView checkUpdateOffline=null;
	  private TextView checkDropbox=null;
	  private TextView checkResetDropbox=null;
	  private TextView checkUpdateLand=null;
	  private TextView checkUpdateVquarter=null;
	  private TextView checkUpdateMachine=null;
	  private TextView checkUpdateWorker=null;
	  private TextView checkUpdateTask=null;
	  private TextView checkUpdatePesticide=null;
	  private TextView checkUpdateFertilizer=null;
	  private TextView checkUpdateSoilFertilizer=null;

	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
	                           Bundle savedInstanceState) {
	    View result=inflater.inflate(R.layout.prefscontent, parent, false);

	    text=(TextView)result.findViewById(R.id.text);
	    list=(TextView)result.findViewById(R.id.list);
	    listbackend=(TextView)result.findViewById(R.id.listbackend);
	    checkUpdateOffline=(TextView)result.findViewById(R.id.updateofflinebox);
	    checkDropbox=(TextView)result.findViewById(R.id.dropbox);
	    checkResetDropbox=(TextView)result.findViewById(R.id.dropboxreset);
	    checkUpdateLand=(TextView)result.findViewById(R.id.landbox);
	    checkUpdateVquarter=(TextView)result.findViewById(R.id.vquarterbox);
	    checkUpdateMachine = (TextView)result.findViewById(R.id.machinebox);
	    checkUpdateWorker = (TextView)result.findViewById(R.id.workerbox);
	    checkUpdateTask = (TextView)result.findViewById(R.id.taskbox);
	    checkUpdatePesticide = (TextView)result.findViewById(R.id.pesticidebox);
	    checkUpdateFertilizer = (TextView)result.findViewById(R.id.fertilizerbox);
	    checkUpdateSoilFertilizer = (TextView)result.findViewById(R.id.soilfertilizerbox);
 	    return(result);
	  }

	  @Override
	  public void onResume() {
	    super.onResume();

	    SharedPreferences prefs=
	        PreferenceManager.getDefaultSharedPreferences(getActivity());

	    text.setText(prefs.getString("text", "<unset>"));
	    list.setText(prefs.getString("list", "<unset>"));
	    listbackend.setText(prefs.getString("listbackend", "<unset>"));
	    checkUpdateLand.setText(new Boolean(prefs.getBoolean("landbox", false)).toString());
	    checkDropbox.setText(new Boolean(prefs.getBoolean("dropbox", false)).toString());
	    checkResetDropbox.setText(new Boolean(prefs.getBoolean("dropboxreset", false)).toString());
	    checkUpdateVquarter.setText(new Boolean(prefs.getBoolean("vquarterbox", false)).toString());
	    checkUpdateMachine.setText(new Boolean(prefs.getBoolean("machinebox", false)).toString());
	    checkUpdateWorker.setText(new Boolean(prefs.getBoolean("workerbox", false)).toString());
	    checkUpdateTask.setText(new Boolean(prefs.getBoolean("taskbox", false)).toString());
	    checkUpdatePesticide.setText(new Boolean(prefs.getBoolean("pesticidebox", false)).toString());
	    checkUpdateFertilizer.setText(new Boolean(prefs.getBoolean("fertilizerbox", false)).toString());
	    checkUpdateSoilFertilizer.setText(new Boolean(prefs.getBoolean("soilfertilizerbox", false)).toString());
	    checkUpdateOffline.setText(new Boolean(prefs.getBoolean("updateofflinebox",false)).toString());
	  }
}
