package it.schmid.android.mofa;



import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;
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

public class WorkSelectWorkerActivity extends DashboardActivity{
	private static final String TAG = "WorkSelectWorkerActivity";
	private ListView listView;
	private int workId;
	private Button closeButton;
	private Double proposedHour = 0.00;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
        setContentView(R.layout.worker_list);
		listView=(ListView)findViewById(R.id.workerlistview);
		closeButton=(Button)findViewById(R.id.workerclose_btn);

		Bundle bundle = getIntent().getExtras();
		if (null!=bundle && bundle.containsKey("Work_ID")) {
            workId = bundle.getInt("Work_ID");
            Log.d(TAG, "Current workid: " + workId); 
         //   work = DatabaseManager.getInstance().getWorkWithId(workId);
		}
		List<Worker> workerList = DatabaseManager.getInstance().getAllWorkers();
		final ArrayAdapter<Worker> adapter = new ArrayAdapter<Worker>(this, R.layout.worker_row, R.id.workerlabel, workerList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			 public void onItemClick(AdapterView<?> parent, View view,
		                int position, long id) {
				 	final Worker worker = adapter.getItem(position);
					Log.d(TAG, "Current worker with id: " + worker.getId());
					if (PropertySuggest.defaultHour!=null){
						 proposedHour = PropertySuggest.defaultHour;
					 }
					PromptDialog dlg = new PromptDialog(WorkSelectWorkerActivity.this, R.string.title,
							R.string.enter_hours, proposedHour) {
						@Override
						public boolean onOkClicked(Double input) {
							// do something
							Log.d(TAG, "showDialog: " + input);
							try {
								saveState(workId, worker.getId(), input);
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
	private void saveState (int workid, int workerid, Double hours) throws SQLException{
		List<WorkWorker> listWorkWorker = DatabaseManager.getInstance().getWorkWorkerByWorkIdAndByWorkerId(workid, workerid);
		if (listWorkWorker.size() == 0) {
			 Log.d (TAG, "New Entry");
			 WorkWorker w = new WorkWorker();
			 Work curWork = DatabaseManager.getInstance().getWorkWithId(workid);
			 Worker curWorker = DatabaseManager.getInstance().getWorkerWithId(workerid);
			 w.setWork(curWork);
			 w.setWorker(curWorker);
			 w.setHours(hours);
			 DatabaseManager.getInstance().addWorkWorker(w);
		}else{
			Log.d (TAG, "Updating Entry");
			WorkWorker w = listWorkWorker.get(0);
			w.setHours(hours);
			DatabaseManager.getInstance().updateWorkWorker(w);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  savedInstanceState.putInt("Work_ID", workId);

	}
}
