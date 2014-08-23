package it.schmid.android.mofa;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkMachine;

import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class WorkSelectMachineActivity extends DashboardActivity{
	private static final String TAG = "WorkSelectMachineActivity";
	private ListView listView;
	private Button closeButton;
	private int workId;
	private Double proposedHour = 0.00;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_list);
		listView=(ListView)findViewById(R.id.machinelistview);
		closeButton=(Button)findViewById(R.id.machineclose_btn);

		Bundle bundle = getIntent().getExtras();
		if (null!=bundle && bundle.containsKey("Work_ID")) {
            workId = bundle.getInt("Work_ID");
            Log.d(TAG, "Current workid: " + workId); 
        	}
		List<Machine> machineList = DatabaseManager.getInstance().getAllMachines();
		final ArrayAdapter<Machine> adapter = new ArrayAdapter<Machine>(this, R.layout.machine_row, R.id.machinelabel, machineList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			 public void onItemClick(AdapterView<?> parent, View view,
		                int position, long id) {
				 	final Machine machine = adapter.getItem(position);
					Log.d(TAG, "Current machine with id: " + machine.getId());
					if (PropertySuggest.defaultHour!=null){
						 proposedHour = PropertySuggest.defaultHour;
					 }
					PromptDialog dlg = new PromptDialog(WorkSelectMachineActivity.this, R.string.title,
							R.string.enter_hours, proposedHour) {
						@Override
						public boolean onOkClicked(Double input) {
							// do something
							Log.d(TAG, "showDialog: " + input);
							try {
								saveState(workId, machine.getId(), input);
								PropertySuggest.defaultHour = input;
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return true; // true = close dialog

						}
					};
					dlg.show();

					
			 	}
		});	
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	private void saveState (int workid, int machineid, Double hours) throws SQLException{
		List<WorkMachine> listWorkMachine = DatabaseManager.getInstance().getWorkMachineByWorkIdAndByMachineId(workid, machineid);
		if (listWorkMachine.size() == 0) {
			 Log.d (TAG, "New Machine");
			 WorkMachine w = new WorkMachine();
			 Work curWork = DatabaseManager.getInstance().getWorkWithId(workid);
			 Machine curMachine = DatabaseManager.getInstance().getMachineWithId(machineid);
			 w.setWork(curWork);
			 w.setMachine(curMachine);
			 w.setHours(hours);
			 DatabaseManager.getInstance().addWorkMachine(w);
		}else{
			Log.d (TAG, "Updating Machine");
			WorkMachine w = listWorkMachine.get(0);
			w.setHours(hours);
			DatabaseManager.getInstance().updateWorkMachine(w);
		}
	}
}
