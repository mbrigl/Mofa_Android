package it.schmid.android.mofa;




import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class WorkSelectWorkerActivity extends DashboardActivity{
	private static final String TAG = "WorkSelectWorkerActivity";
	private int workId;
	SparseArray<Double> selectedWorkers = new SparseArray<Double>() ;
	private Double proposedHour=8.00;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
        setContentView(R.layout.worker_list);
		ListView listView=(ListView)findViewById(R.id.workerlistview);
		Button closeButton=(Button)findViewById(R.id.workerclose_btn);

		Bundle bundle = getIntent().getExtras();
		if (null!=bundle && bundle.containsKey("Work_ID")) {
            workId = bundle.getInt("Work_ID");
            Log.d(TAG, "Current workid: " + workId);
            List<WorkWorker> listWorkers = DatabaseManager.getInstance().getWorkWorkerByWorkId(workId);
            for (WorkWorker w: listWorkers){
                selectedWorkers.put(w.getWorker().getId(),w.getHours()); //put the current selected workers in a sparseArray
            }
         //   work = DatabaseManager.getInstance().getWorkWithId(workId);
		}
		List<Worker> workerList = DatabaseManager.getInstance().getAllWorkers();
		//final ArrayAdapter<Worker> adapter = new ArrayAdapter<Worker>(this, R.layout.worker_row, R.id.workerlabel, workerList);
		final ArrayAdapter<Worker> adapter = new SelectWorkerAdapter(this,R.layout.worker_row,workerList );
        listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			 public void onItemClick(AdapterView<?> parent, View view,
		                int position, long id) {
				 	final Worker worker = adapter.getItem(position);
					Log.d(TAG, "Current worker with id: " + worker.getId());
                    Double hours = selectedWorkers.get(worker.getId());
                    if (hours!=null){ //existing entry
                        proposedHour = hours;
                    } else {
                        proposedHour = MofaApplication.getDefaultHour();
                    }

					PromptDialog dlg = new PromptDialog(WorkSelectWorkerActivity.this, R.string.title,
							R.string.enter_hours, proposedHour) {
						@Override
						public boolean onOkClicked(Double input) {
							// do something
							Log.d(TAG, "showDialog: " + input);

								//saveState(workId, worker.getId(), input);
                                addWorkerToArray(worker.getId(),input);
                                adapter.notifyDataSetChanged();
								MofaApplication.setDefaultHour(input);
                                proposedHour= MofaApplication.getDefaultHour(); //setting the local variable to the new value
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
    private void saveSparseArray(SparseArray<Double> workerArray){
        int workerId;
        for(int i = 0; i < workerArray.size(); i++) {
            workerId = workerArray.keyAt(i);
            Double hours = workerArray.get(workerId);
            try {
                saveState(workId,workerId,hours);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //writing the values of sparseArray to Database
        saveSparseArray(selectedWorkers);
    }

    private class SelectWorkerAdapter extends ArrayAdapter<Worker> {
        private static final String TAG = "SelectWorkerAdapter";
        private Context context;
        private int itemLayout;
        private List<Worker> workers;



        public SelectWorkerAdapter(Context context, int textViewResourceId, List<Worker> workers) {
            super(context, textViewResourceId, workers);
            this.context = context;
            this.itemLayout= textViewResourceId;
            this.workers = workers;


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final WorkerHolder holderItem;
            if (convertView==null){
                //inflating the layout
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(itemLayout,parent, false);
                //setting up viewholder
                holderItem = new WorkerHolder();
                holderItem.wImage = (ImageView) convertView.findViewById(R.id.icon);
                holderItem.wName = (TextView) convertView.findViewById(R.id.workerlabel);
                holderItem.wHours = (TextView)convertView.findViewById(R.id.hourslabel);
                holderItem.wIsSelected = (CheckBox)convertView.findViewById(R.id.selected);

                convertView.setTag(holderItem);
            }else{
                holderItem = (WorkerHolder) convertView.getTag();
            }
            //default case, needed due the fact that we are working with viewholders
            holderItem.wIsSelected.setChecked(false);
            holderItem.wHours.setVisibility(View.GONE);
            final Worker worker = workers.get(position);
            holderItem.wName.setText(worker.getFirstName() + " " + worker.getLastname());
            Double hours;
            hours = selectedWorkers.get (worker.getId());

            if (hours!=null){ //existing entries, setting the hours and the checkbox
//                Log.d(TAG, "worker " + worker.getLastname() + " hours: " + hours);
                holderItem.wIsSelected.setChecked(true);
                holderItem.wHours.setVisibility(View.VISIBLE);
                holderItem.wHours.setText(hours.toString());
            }
            holderItem.wIsSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holderItem.wIsSelected.isChecked()){ //adding item to sparseArray and setting the textview
                        holderItem.wHours.setVisibility(View.VISIBLE);
                        holderItem.wHours.setText(proposedHour.toString());
                        addWorkerToArray(worker.getId(),proposedHour);

                    }else{
                        removeWorkerFromArray(worker.getId());
                        holderItem.wHours.setVisibility(View.GONE);
                        Log.d (TAG, "Removing");
                    }
                }
            });
            return convertView;
        }

        private class WorkerHolder{
            ImageView wImage;
            TextView wName;
            TextView wHours;
            CheckBox wIsSelected;
        }



    }
    private void addWorkerToArray(int workerId, Double value){
        selectedWorkers.put(workerId,value); //adding the selected worker to the SparseArray
    }
    private void removeWorkerFromArray(int workerId){
        selectedWorkers.delete(workerId);
        try {
            List<WorkWorker> listWorkWorker = DatabaseManager.getInstance().getWorkWorkerByWorkIdAndByWorkerId(workId, workerId);
            if (listWorkWorker.size()>0){
                DatabaseManager.getInstance().deleteWorkWorker(listWorkWorker.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
