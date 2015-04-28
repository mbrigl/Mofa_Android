package it.schmid.android.mofa;



import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

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

public class WorkSelectSoilFertilizerActivity extends DashboardActivity{
	private static final String TAG = "WorkSelectSoilFertilizerActivity";
	private ListView listView;
	private int workId;
	private Button closeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
        setContentView(R.layout.soilfertilizer_list);
		listView=(ListView)findViewById(R.id.soilfertilizerlistview);
		closeButton=(Button)findViewById(R.id.soilfertilizerclose_btn);

		Bundle bundle = getIntent().getExtras();
		if (null!=bundle && bundle.containsKey("Work_ID")) {
            workId = bundle.getInt("Work_ID");
            Log.d(TAG, "Current workid: " + workId); 
         //   work = DatabaseManager.getInstance().getWorkWithId(workId);
		}
		List<SoilFertilizer> soilFertilizerList = DatabaseManager.getInstance().getAllSoilFertilizersOrderByName();
		final ArrayAdapter<SoilFertilizer> adapter = new ArrayAdapter<SoilFertilizer>(this, R.layout.soilfertilizer_row, R.id.soilfertilizerlabel, soilFertilizerList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			 public void onItemClick(AdapterView<?> parent, View view,
		                int position, long id) {
				 	final SoilFertilizer soilFertilizer = adapter.getItem(position);
					Log.d(TAG, "Current soilfertilizer with id: " + soilFertilizer.getId());
					
					PromptDialogKeyboard dlg = new PromptDialogKeyboard(WorkSelectSoilFertilizerActivity.this, R.string.title,
							R.string.enter_amount, 0.00) {
						@Override
						public boolean onOkClicked(Double input) {
							// do something
							Log.d(TAG, "showDialog: " + input);
							try {
								saveState(workId, soilFertilizer.getId(), input);
								
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
	private void saveState (int workid, int soilFertilizerid, Double amount) throws SQLException{
		List<WorkFertilizer> listWorkSoilFertilizer = DatabaseManager.getInstance().getWorkFertilizerByWorkIdAndBySoiFertilizerId(workid, soilFertilizerid);
		if (listWorkSoilFertilizer.size() == 0) {
			 Log.d (TAG, "New Entry");
			 WorkFertilizer f = new WorkFertilizer();
			 Work curWork = DatabaseManager.getInstance().getWorkWithId(workid);
			 SoilFertilizer curSoilFertilizer = DatabaseManager.getInstance().getSoilFertilizerWithId(soilFertilizerid);
			 f.setWork(curWork);
			 f.setSoilFertilizer(curSoilFertilizer);
			 f.setAmount(amount);
			 DatabaseManager.getInstance().addWorkFertilizer(f);
		}else{
			Log.d (TAG, "Updating Entry");
			WorkFertilizer f = listWorkSoilFertilizer.get(0);
			f.setAmount(amount);
			DatabaseManager.getInstance().updateWorkFertilizer(f);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  savedInstanceState.putInt("Work_ID", workId);

	}
}
