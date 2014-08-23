package it.schmid.android.mofa;

import it.schmid.android.mofa.adapter.WorkMachineAdapter;
import it.schmid.android.mofa.adapter.WorkWorkerAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkMachine;
import it.schmid.android.mofa.model.WorkWorker;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;


public class WorkEditResourcesFragment extends SherlockFragment  {
	private static final String TAG = "WorkEditResourcesFragment";
	private int mworkId = 0;
	private Work work = null;
	private Button confirmButton;
	private ImageButton mWorker;
	private ImageButton mMachine;
	private ListView mWorkWorkerList;
	private ListView mWorkMachineList;
	private Boolean firstLoad=true;
	WorkEditTabActivity parentActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(getActivity());
		setHasOptionsMenu(true);	
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentActivity = (WorkEditTabActivity) getActivity();
		mworkId = parentActivity.getWorkId();
		Log.d(TAG,"[onCreateView] CurrWorkID= " + mworkId);
		View view = inflater.inflate(R.layout.work_edit_resources, container, false);
		confirmButton = (Button) view.findViewById(R.id.work_save_button);
		mWorker = (ImageButton) view.findViewById(R.id.work_change_worker);
		mMachine = (ImageButton) view.findViewById(R.id.work_change_machine);
		mWorkWorkerList = (ListView)view.findViewById(R.id.currworkerlist);
		mWorkMachineList = (ListView)view.findViewById(R.id.currmachinelist);
		populateFields(mworkId);
		setListener();
         return view;
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	private void populateFields(int id){
				
		if (id!=0) {
			work = DatabaseManager.getInstance().getWorkWithId(id);
		    fillWorkerList(); // filling list of workers of current work
	        fillMachineList();
	        
		}
	}
	
		
		private void fillWorkerList(){
			List<WorkWorker> selectedWorkers = DatabaseManager.getInstance().getWorkWorkerByWorkId(work.getId());
			
			//Log.d(TAG, "Number WorkWorker of current work" + selectedWorkers.size());
			WorkWorkerAdapter adapter = new WorkWorkerAdapter(getActivity(), R.layout.work_worker_row, selectedWorkers);
			mWorkWorkerList.setAdapter(adapter);
		}
		private void fillMachineList(){
			List<WorkMachine>selectedMachine = DatabaseManager.getInstance().getWorkMachineByWorkId(work.getId());
			//Log.d(TAG, "Number WorkMachine of current work" + selectedMachine.size());
			WorkMachineAdapter adapter = new WorkMachineAdapter(getActivity(),R.layout.work_machine_row,selectedMachine);
			mWorkMachineList.setAdapter(adapter);
		}
		
		private void setListener(){
			
			confirmButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					getActivity().setResult(getActivity().RESULT_OK);
					getActivity().finish();
					
				}

			});
		
			mWorker.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					if ((mworkId!=0)&&(parentActivity.getContinueEnabled()==true)){
						Intent i = new Intent(getActivity(),WorkSelectWorkerActivity.class);
						i.putExtra("Work_ID", mworkId);
						startActivity(i);
					}else{
						Toast.makeText(getActivity(), R.string.toast_msg_start, Toast.LENGTH_LONG).show();
					}
						
					
					
				}
			});
			mMachine.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					if ((mworkId!=0)&&(parentActivity.getContinueEnabled()==true)){
						Intent i = new Intent (getActivity(), WorkSelectMachineActivity.class);
						i.putExtra("Work_ID", mworkId);
						startActivity(i);
					}else{
						Toast.makeText(getActivity(), R.string.toast_msg_start, Toast.LENGTH_LONG).show();
					}
					
					
				}
			});
		

				
		}
		
		
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  savedInstanceState.putInt("Work_ID", mworkId);
		  Log.d(TAG,"onSaveInstanceState in WorkEditResourcesActivity");
//		  Toast.makeText(this, "Activity state saved:" + mworkId, Toast.LENGTH_LONG).show();
		}
		
						
		@Override
		public void onPause() {
			super.onPause();
		}
		@Override
		public void onResume() {
			super.onResume();
			if (!firstLoad){
				mworkId = parentActivity.getWorkId();
				Log.d(TAG,"[onResume] CurrWorkID= " + mworkId);
				populateFields(mworkId);
			}
			firstLoad=false;
		}
		
		
}
