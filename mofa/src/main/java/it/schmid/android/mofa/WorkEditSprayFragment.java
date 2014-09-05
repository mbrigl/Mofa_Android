package it.schmid.android.mofa;

import it.schmid.android.mofa.adapter.WorkSelectedFertilizerAdapter;
import it.schmid.android.mofa.adapter.WorkSelectedPesticideAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class WorkEditSprayFragment extends SherlockFragment{
	private static final String TAG = "WorkEditSprayActivity";
	private ListView mWorkPesticideList;
	private ListView mWorkFertilizerList;
	private EditText sumWater;
	private Spinner concent;
	private Button confirmButton;
	private ImageButton pesticideButton;
	private ImageView refreshIcon;
	private Work work = null;
	private Spraying spray = null;
	private int mworkId = 0;
	private int msprayId = 0;
	private int spinnerPosition=0;
	private List<VQuarter> selectedQuarters;
	private WorkEditTabActivity parentActivity;
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
		 if (mworkId!=0){
			 Log.d(TAG,"[OnCreateView] - CurrWorkID= " + mworkId);
				if(DatabaseManager.getInstance().getSprayingByWorkId(mworkId).size()!=0){
					msprayId = DatabaseManager.getInstance().getSprayingByWorkId(mworkId).get(0).getId();
				}
		 }
		 View view = inflater.inflate(R.layout.work_edit_spray, container, false);
		 mWorkPesticideList = (ListView)view.findViewById(R.id.currpesticidelist);
		 mWorkFertilizerList = (ListView)view.findViewById(R.id.currfertilizerlist);
		 sumWater = (EditText)view.findViewById(R.id.sumwater);
		 confirmButton=(Button)view.findViewById(R.id.spray_save_button);
		 pesticideButton=(ImageButton)view.findViewById(R.id.work_select_pest);
		 refreshIcon=(ImageView)view.findViewById(R.id.refresh_sumwater_icon);
		 concent = (Spinner)view.findViewById(R.id.concent_spinner);
		 concent.setOnItemSelectedListener(new OnItemSelectedListenerWrapper
				 (spinnerPosition,new OnItemSelectedListener(){
					public void onItemSelected(AdapterView<?> aParentView, View aView, int aPosition, long anId) {
						//Toast.makeText(getActivity(), "Calling Spinner change", Toast.LENGTH_LONG).show();
						//new RefreshWaterDialog().show(getFragmentManager(), "MyDialog");
					}
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
			 
		 }));
		 populateFields(msprayId);
         setListener();
         return view;
	}
	private void fillSpinner(){
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.sprayconcentration, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		concent.setAdapter(adapter);
	}
	private void populateFields(int id){ // id=sprayingid
		fillSpinner();
		if (mworkId!=0){
			work = DatabaseManager.getInstance().getWorkWithId(mworkId);
			try {
				selectedQuarters= DatabaseManager.getInstance().lookupVQuarterForWork(work);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (id!=0){
				Log.d(TAG, "[populateFields] SprayId = " + id);
				spray = DatabaseManager.getInstance().getSprayingWithId(id);
				sumWater.setText(spray.getWateramount().toString());
				ArrayAdapter myAdap = (ArrayAdapter) concent.getAdapter(); //cast to an ArrayAdapter
				String strcon= spray.getConcentration().toString();
				Log.d(TAG, "[populateFields] concentration = " + spray.getConcentration().toString());
				spinnerPosition = myAdap.getPosition(strcon);
				concent.setSelection(spinnerPosition,true);
				fillPesticideList();
				fillFertilizerList();
		}else{ //new entry
			sumWater.setText(sumOfWater().toString());
		}
	}
	

	private void fillPesticideList(){
		
			List<SprayPesticide> selectedPesticides = DatabaseManager.getInstance().getSprayPesticideBySprayId(msprayId);
			Log.d(TAG, "Number Pesticides of current work" + selectedPesticides.size());
			WorkSelectedPesticideAdapter adapter = new WorkSelectedPesticideAdapter(getActivity(),WorkEditSprayFragment.this, R.layout.work_pesticide_row, selectedPesticides);
			mWorkPesticideList.setAdapter(adapter);
			
			
			
		
	}
	private void fillFertilizerList(){
		List<SprayFertilizer> selectedFertilizers = DatabaseManager.getInstance().getSprayFertilizerBySprayId(msprayId);
		Log.d(TAG, "Number Fertilizers of current work" + selectedFertilizers.size());
		WorkSelectedFertilizerAdapter adapter = new WorkSelectedFertilizerAdapter(getActivity(),WorkEditSprayFragment.this, R.layout.work_pesticide_row, selectedFertilizers);
		mWorkFertilizerList.setAdapter(adapter);
		
	}
	private void setListener(){
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveState();
				getActivity().finish();
			}
		});
		pesticideButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (parentActivity.getContinueEnabled()==true){
					Log.d(TAG,"Showing pesticide list");
					saveState();
					Intent i = new Intent(getActivity(),WorkProductTabActivity.class);
					i.putExtra("Spray_ID",msprayId );
					i.putExtra("Calling_Activity", ActivityConstants.WORK_SPRAYING_ACTIVITY);
					startActivity(i);
				}else{
					Toast.makeText(getActivity(), R.string.toast_msg_start, Toast.LENGTH_LONG).show();
				}
				
			}
		});
		refreshIcon.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Double water;
				//Toast.makeText(getActivity(), sumOfWater().toString(),Toast.LENGTH_LONG).show();
				Integer concentration = Integer.parseInt (concent.getSelectedItem().toString());
				water = sumOfWater()/concentration;
				sumWater.setText(format(water));
				
			}
		});
	}
	public Double getCurrentWaterAmount(){
		return Double.parseDouble (sumWater.getText().toString());
	}
	public Integer getCurrentConc(){
		return Integer.parseInt (concent.getSelectedItem().toString());
	}
	private void saveState(){
		Log.d(TAG,"Saving Spraying Data");
		Integer concentration = Integer.parseInt (concent.getSelectedItem().toString());
		Double wateramount = Double.parseDouble (sumWater.getText().toString());
		if(DatabaseManager.getInstance().getSprayingByWorkId(mworkId).size()==0){
			createNewSpraying(concentration,wateramount);
		}else{ //existing entry
			spray = DatabaseManager.getInstance().getSprayingByWorkId(mworkId).get(0);
			msprayId=spray.getId();
			updateSpraying(spray,concentration,wateramount);
		}
	}
	private void createNewSpraying(Integer c, Double w){
		spray = new Spraying();
		spray.setConcentration(c);
		spray.setWateramount(w);
		spray.setWork(work);
		DatabaseManager.getInstance().addSpray(spray);
		Log.d(TAG, "New Spray Entry");
		msprayId = spray.getId();
	}
	private void updateSpraying (Spraying s,Integer c,Double w){
		s.setConcentration(c);
		s.setWateramount(w);
		s.setWork(work);
		DatabaseManager.getInstance().updateSpray(s);
		Log.d(TAG, "Updating Spray Entry");
	}
	private Double sumOfWater(){
		Double sum=0.00;
		for (VQuarter v:selectedQuarters){
			if (v.getWateramount()!=null){
				sum= sum + v.getWateramount();
			}
			
		}
		return sum;
	}
	@Override
	public void onResume() {
		super.onResume();
		mworkId = parentActivity.getWorkId();
		Log.d(TAG,"[onResume] CurrWorkID= " + mworkId);
		populateFields(msprayId);
	}
	public static String format(double i)
	{
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		
		double toFormat = ((double)Math.round(i*100))/100;
		return nf.format(toFormat);
	}
}