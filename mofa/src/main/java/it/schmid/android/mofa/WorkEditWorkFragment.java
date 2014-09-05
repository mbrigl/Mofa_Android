package it.schmid.android.mofa;

import it.schmid.android.mofa.adapter.TaskSpinnerAdapter;
import it.schmid.android.mofa.adapter.WorkVQuarterAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkVQuarter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.stmt.query.IsNull;


public class WorkEditWorkFragment extends SherlockFragment implements OnDateSetListener {
	private static final String TAG = "WorkEditWorkFragment";
	private List<String> sprayList =  new ArrayList<String>(
			Arrays.asList("Spraying", "Spritzen", "Behandlung","Trattamento","Spritzung", "Pflanzenschutz", "Herbizidbehandlung",
					"Chemische Unkrautbekämpfung","Chem. Unkrautbekämpfung")
		); 
	ShowSprayTabListener mCallback;
	SetWorkIdListener parentSetWorkId;
	CompleteBehaviour continueEnabled;
	ShowSoilFertilizerTabListener mShowSFertilizerTab;
	ShowHarvestTabListener mShowHarvestTab;
	private static final int REQUEST_CODE = 0;
	private Boolean saveStateOnPause = false;
	private int mworkId = 0;
	private Work work = null;
	private EditText mDateText;
	private EditText mNoteText;
	private Button mPickDate;
	private Button confirmButton;
	private Spinner mWork;
	private ImageButton mLand;
	private ListView mWorkVquarterList;
	private int mYear;
	private int mMonth;
	private int mDay;
	private LocationManager mgr; // variable for Location Manager
	private String best;		//variable for best provider for Location Manager
	private Boolean firstLoad=true;
	WorkEditTabActivity workEditActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DatabaseManager.init(getActivity());
		setHasOptionsMenu(true);
		
	
	}
	// interface to pass the workid to the spraying fragment
	public interface ShowSprayTabListener {
		public void showSprayTabListener (int workId, Boolean status);
	}
	public interface SetWorkIdListener{
		public void setWorkIdListener (int workId);
	}
	public interface CompleteBehaviour{
		public void setContinue (boolean complete);
	}
	public interface ShowSoilFertilizerTabListener{
		public void showSoilFertilizerTab (int workId, Boolean status);
	}
	public interface ShowHarvestTabListener{
		public void showHarvestTabListener (int workId, Boolean status);
	}
	// registering the callback, using onAttach
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (ShowSprayTabListener) activity;
			mShowSFertilizerTab = (ShowSoilFertilizerTabListener) activity;
			mShowHarvestTab = (ShowHarvestTabListener) activity;
			parentSetWorkId = (SetWorkIdListener) activity;
			continueEnabled = (CompleteBehaviour) activity;
		}catch (ClassCastException e){
			throw new ClassCastException(activity.toString()
					+ " must implement ShowSprayTabListener,SetWorkIdListener,continueEnabled");
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WorkEditTabActivity parentActivity = (WorkEditTabActivity) getActivity();
		mworkId = parentActivity.getWorkId();
		Log.d(TAG,"[onCreateView] CurrWorkID= " + mworkId);
		View view = inflater.inflate(R.layout.work_edit, container, false);
		mDateText =(EditText) view.findViewById(R.id.work_edit_date);
		mPickDate = (Button) view.findViewById(R.id.work_change_date);
		mWork = (Spinner) view.findViewById(R.id.tasklist);
		confirmButton = (Button) view.findViewById(R.id.work_save_button);
		mLand = (ImageButton) view.findViewById(R.id.work_change_land);
		mWorkVquarterList = (ListView)view.findViewById(R.id.currlandlist);
		mNoteText = (EditText) view.findViewById(R.id.noteeditText);
		MofaApplication mofaApplication = MofaApplication.getInstance();
		mofaApplication.putGlobalVariable("land", "null"); //setting the global variable for checking validity to null
		mofaApplication.putGlobalVariable("worker", "null");//setting the global variable for checking validity to null
		
		populateFields(mworkId);
		setListener();
		if (mworkId!=0){ //existing entry, we have to check the validity
			if (work.getValid()==true){ //we put the global variables to valid, otherwise their value remains to "null"
				mofaApplication.putGlobalVariable("land", "valid"); //setting the global variable for checking validity to null
				mofaApplication.putGlobalVariable("worker", "valid");//setting the global variable for checking validity to null
				
			}
		}
		mWork.requestFocus();
		
         return view;
	}
	private void populateFields(int id){
		String myDate;
		List<Task> taskList = DatabaseManager.getInstance().getAllTasks();
		final TaskSpinnerAdapter adapter = new TaskSpinnerAdapter(taskList,getActivity());
		mWork.setAdapter(adapter);
		
		if (id!=0) {
			work = DatabaseManager.getInstance().getWorkWithId(id);
			myDate = setCalendarDate(work.getDate());;
	        Log.d(TAG,work.getDate().toString());
	        Task selTask = work.getTask();
	        Log.d(TAG, "Position task " + selTask.getTask() + " at " + adapter.getPosition(selTask));
	        mWork.setSelection(adapter.getPosition(selTask));
	       // selectSpinnerItem(); //preselecting the stored task in the spinner
	        fillVQuarterList();	//filling list of vquarters of current work
	        
	        
	       
	        if (work.getNote()!=null){
				mNoteText.setText(work.getNote());
			}   
		}else{
			Date date = new Date();
	    	myDate = setCalendarDate(date);
		}
		
		mDateText.setText(myDate);
		mDateText.clearFocus();
	}
	
		private void fillVQuarterList() {
			try {
				List<VQuarter> selectedQuarters = DatabaseManager.getInstance().lookupVQuarterForWork(work);
				if (selectedQuarters.size()!=0){ // checking if we are allowed to enter rest of data
					Log.d(TAG, "Continue setting on true");
					continueEnabled.setContinue(true);
					
				}else{
					continueEnabled.setContinue(false);
					
				}
				Log.d(TAG, "Number VQuarters for Current Work " + selectedQuarters.size());
				WorkVQuarterAdapter adapter = new WorkVQuarterAdapter(getActivity(), R.layout.work_vquarter_row, selectedQuarters);
				mWorkVquarterList.setAdapter(adapter);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	 	 
		}
		

		
		private void setListener(){
			// add a click listener to the button
			mPickDate.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							DialogFragment newFragment = new DatePickerDialogFragment(WorkEditWorkFragment.this);
						    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
						}
			});
			confirmButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					saveState();
					getActivity().setResult(getActivity().RESULT_OK);
					getActivity().finish();
					
				}

			});
			mLand.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					saveStateOnPause=false;
					saveState();
					
					Log.d(TAG,"[mLand.setOnClickListener] - startActivity WorkSelectLandActivity with workid: " + mworkId);
					Intent i = new Intent(getActivity(),WorkSelectLandActivity.class);
					i.putExtra("Work_ID",mworkId );
					startActivity(i);
					//startActivityForResult(i, REQUEST_CODE);
				}
			});

			mWork.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				@SuppressLint("NewApi")
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
						int pos, long id) {
					 	Task item = (Task) parentView.getItemAtPosition(pos);
					 	MofaApplication app = MofaApplication.getInstance();
					 	
						String backEndSoftware = app.getBackendSoftware();
						if (Integer.parseInt(backEndSoftware)==1){ //ASA case
							String asaWorkSprayCodes=app.getSprayCodeASA();
							String asaWorkHerbicideCodes=app.getHerbicideCodeASA();
							String asaWorkFertilizerCodes=app.getFertilizerCodeASA();
							String asaWorkHarvestCodes=app.getHarvestCodeASA();
							Log.d(TAG, "ASA code = " + item.getCode());
							if (work!=null && contains(asaWorkSprayCodes.split(";"),item.getCode())){
								mCallback.showSprayTabListener(mworkId,true);
							}
							if (work!=null && contains(asaWorkHerbicideCodes.split(";"),item.getCode())){
								mCallback.showSprayTabListener(mworkId,true);
							}
							if (work!=null && contains(asaWorkFertilizerCodes.split(";"),item.getCode())){
								mShowSFertilizerTab.showSoilFertilizerTab(mworkId, true);
							}
							if (work!=null && contains(asaWorkHarvestCodes.split(";"),item.getCode())){
								Log.d(TAG, "New Harvest entry - Showing Tab");
								mShowHarvestTab.showHarvestTabListener(mworkId, true);
							}
						}else{ //libreoffice case
							//item.getTask().equalsIgnoreCase("Spraying")
						 	//showing spraytab only if
						 	// a) work in the list of defined tasks
						 	// b) id of work = 1 --> means if work has id of 1!!
						 	if (work!=null && sprayList.contains(item.getTask())|| (work!=null && item.getId()==1) || (work!=null && item.getId()==2)){
						 		mCallback.showSprayTabListener(mworkId,true);
						 	}
						 	if ((work!=null && item.getId()==3) ){ //showing Soilfertilizer Tab if work id ==3
						 		mShowSFertilizerTab.showSoilFertilizerTab(mworkId, true);
						 	}
						}
						
					 		
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			mNoteText.setOnFocusChangeListener(new OnFocusChangeListener() {          

		        public void onFocusChange(View v, boolean hasFocus) {
		            if(!hasFocus){
		            	Log.d(TAG, "[onFocusChange - mNoteText] - Saving state ");
		            	saveState();
		            }
		               
		        }
		    });
		}
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			@SuppressWarnings("deprecation")
			Date newDate = new Date(year-1900,monthOfYear,dayOfMonth);
		//	Log.d(TAG, "onDataSet - DataPicker :" + year +"," + monthOfYear + "," +dayOfMonth);
			
			mDateText.setText(setCalendarDate(newDate));
		}
		
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  savedInstanceState.putInt("Work_ID", mworkId);
		  //savedInstanceState.putString("Note",mNoteText.getText().toString());
		  Log.d(TAG,"onSaveInstanceState in WorkEditWorkFragment");
	  
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE) {
		    if (data.hasExtra("Work_ID")) {
		
		    }
		  }
		} 
		public String setCalendarDate(Date date){
			final String DATE_FORMAT = "dd.MM.yyyy";
			final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			final Calendar c = Calendar.getInstance();
			c.setTime(date);
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			Log.d(TAG, "setCalendarDate - " + mYear +", "+ mMonth +", " + "," +mDay );
			Log.d(TAG, "setCalendarDate Formatted Date: " + dateFormat.format(date));
			return (dateFormat.format(date));
			
		}
		private void saveState() {
			Task t = (Task) mWork.getSelectedItem();
			Date newDate = new Date(mYear-1900,mMonth,mDay);
			if (null != work){
				updateWork(t,newDate);
			}else
			{
				createNewWork(t,newDate);
			}
		}
		private void updateWork(Task t, Date d){
			work.setDate(d);
			work.setTask(t);
			work.setNote(mNoteText.getText().toString());
			DatabaseManager.getInstance().updateWork(work);
		}
		private void createNewWork(Task t, Date d){
			Work w = new Work();
			w.setDate(d);
			w.setTask(t);
			w.setNote(mNoteText.getText().toString());
			DatabaseManager.getInstance().addWork(w);
			mworkId= w.getId(); //getting the id of the new work
			parentSetWorkId.setWorkIdListener(mworkId); //setting the workid on the parent Activity
			
		}
		private static <T> boolean contains( final T[] array, final T v ) {
		    for ( final T e : array )
		        if ( e == v || v != null && v.equals( e ) )
		            return true;

		    return false;
		}
	
		 public void onCreateOptionsMenu (com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
			// inflater.inflate(R.menu.work_edit_menu, menu);
			//  super.onCreateOptionsMenu(menu, inflater);
		    }
		
		

//		@Override
//		public boolean onOptionsItemSelected(MenuItem item) {
//			switch (item.getItemId()) {
//			case R.id.work_gps_auto:
//				Log.d(TAG, "Automatically detecting vquarter");
//				startGPSCalc();
//				return true;
//			}
//			return super.onOptionsItemSelected(item);
//		}
		// Reaction to the menu selection
//		public boolean onMenuItemSelected(int featureId, MenuItem item) {
//						switch (item.getItemId()) {
//						case R.id.work_gps_auto:
//							Log.d(TAG, "Automatically detecting vquarter");
//							//startGPSCalc();
//							return true;
//					}
//						return onMenuItemSelected(featureId, item);
//			}
//			protected void startGPSCalc() { //initializing the GPS calculation
//				saveState();
//				mgr = (LocationManager) getActivity().getSystemService (getActivity().LOCATION_SERVICE);
//				Criteria criteria = new Criteria();
//				best = mgr.getBestProvider(criteria, true);
//				mgr.requestLocationUpdates(best, 100,1, this);
//				
//			}
	/**
	 * 
	 * @param location= current location
	 * @throws SQLException, due the function of databasemanager
	 */
//		private void autoDetectVQuarter(Location location)throws SQLException{
//			if (location == null){ // no gps signal
//				Log.d (TAG, "no location data available");
//				Toast.makeText(getActivity(), "no data available", Toast.LENGTH_LONG).show();
//			}else{ // checking if gps-signal lies in the stored range of gps signals
//				Log.d (TAG, "location data available: latitude:" + location.getLatitude() +", " + location.getLongitude()  );
//				Toast.makeText(getActivity(), "location data available: latitude:" + location.getLatitude() +", " + location.getLongitude(), Toast.LENGTH_LONG).show();
//				VQuarter currVquarter= locatedInVarietyQuarter(location);
//				if (currVquarter!= null){ // its in the saved plane for the vq
//					Toast.makeText(getActivity(), currVquarter.getLand().getName() + ", " + currVquarter.getVariety() , Toast.LENGTH_LONG).show();
//					//saveState();
//					List<WorkVQuarter> vquarterList = DatabaseManager.getInstance().getWorkVQuarterByWorkIdAndByVQuarterId(mworkId, currVquarter.getId());
//					if (vquarterList.size() == 0){ //not yet part of the current work
//						if (work==null) {
//							work = DatabaseManager.getInstance().getWorkWithId(mworkId);
//						}
//						WorkVQuarter w = new WorkVQuarter();
//						w.setWork(work);
//						w.setVquarter(currVquarter);
//						DatabaseManager.getInstance().addWorkVQuarter(w);
//						fillVQuarterList();	 //refilling the list
//						continueEnabled.setContinue(true);
//					}
//				}
//				mgr.removeUpdates(this);
//			}
//			
//		}
		/**
		 * 
		 * @param location
		 * @return the right vquarter otherwise null
		 */
//		private VQuarter locatedInVarietyQuarter(Location location){
//			VQuarter vquarter = null;
//			List<VQuarter> allVQuarters = DatabaseManager.getInstance().getAllVQuarters();
//			for (VQuarter vQuarter : allVQuarters) {
//				if (isInsideTwoPoints(vQuarter, location)){ // the checking function
//					vquarter = vQuarter; //found the right quarter
//					return vquarter;
//					}
//			}
//			return vquarter;
//		}
		/**
		 * 
		 * @param vquarter the current vquarter to check
		 * @param location the actual location
		 * @return
		 */
//		private boolean isInsideTwoPoints(VQuarter vquarter, Location location){
//			//only if no of the stored value is null we go to check the coordinates
//			if ((vquarter.getGps_x1()!= null) && (vquarter.getGps_x2()!=null)&& (vquarter.getGps_y1()!=null) && (vquarter.getGps_y2()!=null)){ 
//				Double x1,x2,y1,y2;
//				x1 = smallerValue(vquarter.getGps_x1(), vquarter.getGps_x2());
//				x2 = biggerValue(vquarter.getGps_x1(), vquarter.getGps_x2());
//				y1 = smallerValue(vquarter.getGps_y1(),vquarter.getGps_y2());
//				y2 = biggerValue(vquarter.getGps_x1(), vquarter.getGps_x2());
//				// here we check if the actual value lies between the stored values
//				if ((x1<=location.getLatitude()) && (x2>=location.getLatitude()) && (y1<=location.getLongitude()) && (y2>=location.getLongitude())){
//					Log.d (TAG, "x1= "+ vquarter.getGps_x1().toString() + " x2= " + vquarter.getGps_x2());
//					Log.d (TAG, "y1= "+ vquarter.getGps_y1().toString() + " y2= " + vquarter.getGps_y2());
//					Log.d (TAG, "Current location lat(x) = " + location.getLatitude() + " long(y) = "+ location.getLongitude());
//					Log.d(TAG, "Vquarter in the plane:  " + vquarter.getLand().getName() + ", " + vquarter.getVariety());
//					return true; // yep, this vquarter lies between the stored coordinates
//				}else{
//					return false; // not in the defined plane
//				}
//			}else{ // if one of the fields is null return false
//				return false;
//			}
//				
//		}
		/**
		 * helper function for the bigger and smaller value
		 * @param value1 one of two double values
		 * @param value2
		 * @return
		 */
//		private Double smallerValue(Double value1, Double value2){ 
//			if (value1<value2){
//				return value1;
//			}else{
//				return value2;
//			}
//		}
//		private Double biggerValue(Double value1, Double value2){
//			if (value1>value2){
//				return value1;
//			}else{
//				return value2;
//			}
//		}
//		public void onLocationChanged(Location location) {
//			try {
//				autoDetectVQuarter(location); // call back function to start the gps detection
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//		}



//		public void onProviderDisabled(String provider) {
//			// TODO Auto-generated method stub
//			
//		}
//
//
//
//		public void onProviderEnabled(String provider) {
//			// TODO Auto-generated method stub
//			
//		}
//
//
//
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//			// TODO Auto-generated method stub
//			
//		}


		@Override
		public void onPause() {
			super.onPause();
			if (saveStateOnPause){ //checking if this control variable is set by the land selecting button
				Log.d(TAG, "[onPause] Saving state!!!");
				saveState();
			}
			
			
//			if (mgr!=null){
//				mgr.removeUpdates(this);
//			}
			saveStateOnPause=true; //resetting the variable to true to change the state using onPause!
		}
		@Override
		public void onResume() {
			super.onResume();
			//Log.d(TAG, "[onResume] populate fields!!");
			if (!firstLoad){
				populateFields(mworkId);
			}
			firstLoad=false;
		}
		
		
}