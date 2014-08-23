package it.schmid.android.mofa;

import it.schmid.android.mofa.adapter.WorkSoilFertilizerAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;

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


public class WorkEditSoilFertilizerFragment extends SherlockFragment  {
	private static final String TAG = "WorkEditSoilFertilizerFragment";
	private int mworkId = 0;
	private Work work = null;
	private Button confirmButton;
	private ImageButton mSoilFertilizer;
	private ListView mWorkSoilFertilizerList;
	

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
		View view = inflater.inflate(R.layout.work_edit_soilfertilizer, container, false);
		confirmButton = (Button) view.findViewById(R.id.work_save_button);
		mSoilFertilizer = (ImageButton) view.findViewById(R.id.work_change_soilfertilizer);
		mWorkSoilFertilizerList = (ListView)view.findViewById(R.id.currsoilfertilizerlist);
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
		    fillSoilFertilizerList(); // filling list of soil fertilizers of current work
	       
	        
		}
	}
	
		
		private void fillSoilFertilizerList(){
			List<WorkFertilizer> selectedSoilFertilizers = DatabaseManager.getInstance().getWorkFertilizerByWorkId(work.getId());
			//Log.d(TAG, "Number WorkWorker of current work" + selectedWorkers.size());
			WorkSoilFertilizerAdapter adapter = new WorkSoilFertilizerAdapter(getActivity(), R.layout.work_soilfertilizer_row, selectedSoilFertilizers);
			mWorkSoilFertilizerList.setAdapter(adapter);
			
		}
		
		
		private void setListener(){
			
			confirmButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					getActivity().setResult(getActivity().RESULT_OK);
					getActivity().finish();
					
				}

			});
		
			mSoilFertilizer.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					if ((mworkId!=0)&&(parentActivity.getContinueEnabled()==true)){
						Intent i = new Intent(getActivity(),WorkSelectSoilFertilizerActivity.class);
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
		  Log.d(TAG,"onSaveInstanceState in ParentClass WorkEditTabActivity");

		}
		
						
		@Override
		public void onPause() {
			super.onPause();
		}
		@Override
		public void onResume() {
			super.onResume();
			mworkId = parentActivity.getWorkId();
			Log.d(TAG,"[onResume] CurrWorkID= " + mworkId);
			populateFields(mworkId);
		}
		
		
}
