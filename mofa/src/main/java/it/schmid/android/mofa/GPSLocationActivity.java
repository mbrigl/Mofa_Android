package it.schmid.android.mofa;

import it.schmid.android.mofa.adapter.VQSpinnerAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.VQuarter;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class GPSLocationActivity extends DashboardActivity {
	private static final String TAG = "GPSLocationActivity";
	
	private double lat;
	private double lng;
	private LocationManager mgr;
	private Spinner mVquarter;
	private ImageButton gps1Start;
	private ImageButton gps2Start;
	private ImageButton gps1Save;
	private ImageButton gps2Save;
	private TextView txtGPSLong1;
	private TextView txtGPSLat1;
	private TextView txtGPSLong2;
	private TextView txtGPSLat2;
	private VQuarter currVquarter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
		setContentView(R.layout.gps_location);
		mVquarter = (Spinner) findViewById(R.id.spinner_varquarter_gps);
		gps1Start = (ImageButton)findViewById(R.id.start_gps1);
		gps2Start = (ImageButton)findViewById(R.id.start_gps2);
		gps1Save =  (ImageButton)findViewById(R.id.save_gps1);
		gps2Save =  (ImageButton)findViewById(R.id.save_gps2);
		txtGPSLong1 = (TextView) findViewById(R.id.textgpslong1);
		txtGPSLat1 = (TextView) findViewById(R.id.textgpslat1);
		txtGPSLong2 = (TextView)findViewById(R.id.textgpslong2);
		txtGPSLat2 = (TextView) findViewById(R.id.textgpslat2);

		gps1Start.setOnClickListener(myhandler1);
		gps2Start.setOnClickListener(myhandler2);
		gps1Save.setOnClickListener(savehandler1);
		gps2Save.setOnClickListener(savehandler2);
		fillSpinner();
		mVquarter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
		    	Object item = adapterView.getItemAtPosition(i);
		    	// casting object
		    	currVquarter = (VQuarter) item;
		    	Log.d(TAG, currVquarter.getVariety());
		    	if (currVquarter!=null){
		    		loadGPSDBData();
		    	}
		    } 

		    public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 
		initGps();
	}
	// filling the stored gps-data in the corresponding textfields
	private void loadGPSDBData(){
		txtGPSLat1.setText("" + currVquarter.getGps_x1());
		txtGPSLong1.setText("" + currVquarter.getGps_y1());
		txtGPSLat2.setText("" + currVquarter.getGps_x2());
		txtGPSLong2.setText("" + currVquarter.getGps_y2());
	}
	private void fillSpinner(){
		List<VQuarter> vquarterList = DatabaseManager.getInstance().getAllVQuarters();
		if (vquarterList.size()!=0){
			final VQSpinnerAdapter adapter = new VQSpinnerAdapter(vquarterList,this);
			mVquarter.setAdapter(adapter);
		}
		
	}
	private void initGps(){
		String svcName = Context.LOCATION_SERVICE;
	    mgr = (LocationManager)getSystemService(svcName);

	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setSpeedRequired(false);
	    criteria.setCostAllowed(true);
	    String provider = mgr.getBestProvider(criteria, true);
	    Location l = mgr.getLastKnownLocation(provider);
	    updateWithNewLocation(l);
	    mgr.requestLocationUpdates(provider, 5000, 3,
	                                           locationListener);
	}
	 private void updateWithNewLocation(Location location) {
		 Log.d(TAG, "[updateWithNewLocation] - refreshing gps"); 
		 if (location != null) {
		      lat = location.getLatitude();
		      lng = location.getLongitude();
		    }
		      
	 }
	 private final LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      updateWithNewLocation(location);
		    }

		    public void onProviderDisabled(String provider) {}
		    public void onProviderEnabled(String provider) {}
		    public void onStatusChanged(String provider, int status, 
		                                Bundle extras) {}
		  };
	
	//Clickhandlers for the four buttons to load and save the data
	View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
		      Log.d ("TAG", "Starting the GPS process or P1");
		      txtGPSLong1.setText("" + lng);
			  txtGPSLat1.setText(""+ lat);
		      
		    }
	};
	View.OnClickListener myhandler2 = new View.OnClickListener() {
		
		public void onClick(View v) {
			Log.d ("TAG", "Starting the GPS process of P2");
			txtGPSLong2.setText("" + lng);
			txtGPSLat2.setText(""+ lat);
			
		}
	}; 
View.OnClickListener savehandler1 = new View.OnClickListener() {
		
		public void onClick(View v) {
			{
				Double lat = Double.parseDouble (txtGPSLat1.getText().toString());
				Double lon = Double.parseDouble (txtGPSLong1.getText().toString());
				currVquarter.setGps_x1(lat);
				currVquarter.setGps_y1(lon);
				DatabaseManager.getInstance().updateVQuarter(currVquarter);
			}
			
		}
	};
View.OnClickListener savehandler2 = new View.OnClickListener() {
		
		public void onClick(View v) {
			Double lat = Double.parseDouble (txtGPSLat2.getText().toString());
			Double lon = Double.parseDouble (txtGPSLong2.getText().toString());
			currVquarter.setGps_x2(lat);
			currVquarter.setGps_y2(lon);
			DatabaseManager.getInstance().updateVQuarter(currVquarter);
			
		}
	};


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mgr!=null){
			mgr.removeUpdates(locationListener);
		}
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		initGps();
	}
	
	
	
}
