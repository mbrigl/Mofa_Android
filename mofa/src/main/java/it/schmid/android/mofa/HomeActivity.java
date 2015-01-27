package it.schmid.android.mofa;




import it.schmid.android.mofa.SendingProcess.RemoveEntries;
import it.schmid.android.mofa.db.DatabaseHelper;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.db.DatabaseTestDB;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.search.SearchActivity;
import it.schmid.android.mofa.search.WorkerOverviewActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;
import com.j256.ormlite.support.DatabaseConnection;





public class HomeActivity extends DashboardActivity implements RemoveEntries{
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxxdQkUSBx7ZmOM4AFBBvgNTSN3GWdsfM7uE0ygmvLdXC7V0/tX2byfLMvvkUtfdVD/c986SQdDrB2Un4Hr9nCcAFlWW1DFQgZrHZaWbe/gmk1rcxqbjKTON2CnWyfWU0iLu2ZCSRcYquajpKasJdVlvkTR+mlhOahSB06GmDcZDr/Uzr5psepYpex97Tqny0N+LWwnNFZ4QUPn8YJ/l/BVc3oc+UpshY1h8ieIn7BVEO0Xyk0gGs08BCyvHjNWBYTQadMQRhnXBdLraKwX7v6ojBMWk6RsSjXv94fqyrtPywNYW0IlXwthp4L3xm8EXhiMGdiZ6XbCovHGh9ud7tUQIDAQAB";
	private static final byte[] SALT = new byte[] {88,77,37,44,48,10,59,27,03,63,60,02,59,50,50,53,58,69,13,95};
	private static final String TAG = "HomeActivity";
	public static final int NUM_HOME_BUTTONS = 6;
	/**
	 * // setting from preferences
	 */
	private Boolean resetDropbox;
	private Boolean dropBox; 
	static final String DROPBOX_IMPORT_PATH = "MoFaBackend/import";
	private Boolean offline;
	private String urlPath;
	private String format;
	private String backEndSoftware;
    private MofaApplication app;
	//**licensing variables
	private Handler mHandler;
	private LicenseChecker mChecker;
	private LicenseCheckerCallback mLicenseCheckerCallback;
	boolean licensed;
	boolean checkingLicense;
	boolean didCheck;
	
	private SharedPreferences preferences;
	//******************
	// Dropbox credentials
	//private static final String appKey = "zgo2dupm3ung3u6";
    //private static final String appSecret = "22u6lbkswjitll9";
    private static final int REQUEST_LINK_TO_DBX = 0;
    
    //Dropbox variable
    private DbxAccountManager mDbxAcctMgr;
	
	// Image resources for the buttons
	private Integer[] mImageIds = {
	        R.drawable.home_button1,
	        R.drawable.home_button2,
	        R.drawable.home_button3,
	        R.drawable.home_button4,
	        R.drawable.home_button5,
            R.drawable.home_button6
	        } ;

	// Labels for the buttons
	private Integer[] mLabelIds = {
	        R.string.title_feature1,
	        R.string.title_feature2,
	        R.string.title_feature3,
	        R.string.title_feature4,
            R.string.title_feature5,
	        R.string.title_feature6
	        } ;

	// Ids for the frames that define where the images go
	private Integer[] mFrameIds = {
	        R.id.frame1,
	        R.id.frame2,
	        R.id.frame3,
	        R.id.frame4,
	        R.id.frame5,
            R.id.frame6
	        } ;
	protected void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    DatabaseManager.init(this);
	    setContentView(R.layout.activity_home);
	    //
	    // Add the buttons that make up the Dashboard.
	    // We do this with a LayoutInflater. Doing it that way gives us more control
	    // over the size of the images and labels. Size values are defined in the layout
	    // for the image button (see activity_home_button.xml). Since each of the different screen
	    // sizes has their own dimens.xml file, you can adjust the sizes and scaling as needed.
	    // (Values folders: values, values-xlarge, values-sw600dp, values-sw720p)
	    //
	    LayoutInflater li = this.getLayoutInflater();
	    int imageButtonLayoutId = R.layout.activity_home_button;
	    for (int j = 0; j < NUM_HOME_BUTTONS; j++) {
	        int frameId = mFrameIds [j];
	        int labelId = mLabelIds [j];
	        int imageId = mImageIds [j];

	        // Inflate a view for the image button. Set its image and label.
	        View v = li.inflate (imageButtonLayoutId, null);
	        ImageView iv = (ImageView) v.findViewById (R.id.home_btn_image);
	        // if (iv != null) iv.setImageDrawable (imageId);
	        if (iv != null) {
	           iv.setImageResource (imageId);
	           // Assign a value for the tag so the onClickFeature handler can determine which button was clicked.
	           iv.setTag (new Integer (j+1));
	        }
	        TextView tv = (TextView) v.findViewById (R.id.home_btn_label);
	        if (tv != null) tv.setText (labelId);


	        // Find the frame where the image goes.
	        // Attach the inflated view to that frame.
	        View buttonView = v;
	        FrameLayout frame = (FrameLayout) findViewById (frameId);
	        if (frame != null) {
	           FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams
	                                                  (ViewGroup.LayoutParams.MATCH_PARENT,
	                                                   ViewGroup.LayoutParams.MATCH_PARENT,
	                                                   Gravity.CENTER);
	           frame.addView (buttonView, lp);
	        }
	        
	    }
        app = MofaApplication.getInstance();

	}

    @Override
    protected void onResume() {
        String sBackEnd;
        super.onResume();
        //setting title with additional info
        backEndSoftware = app.getBackendSoftware();
        if (Integer.parseInt(backEndSoftware)==1) {//ASA case
            sBackEnd="ASA";
        }else{
            sBackEnd="Excel";
        }
        getSupportActionBar().setTitle("MoFa - "  + sBackEnd );
    }

    public void onClickFeature (View v)
	{
	   Integer featureNum = (Integer) v.getTag ();
	   if (featureNum == null) return;

	   switch (featureNum) {
	      case 1 :
	           if (DatabaseManager.getInstance().checkIfEmpty()==true){
	        	   Toast.makeText(this, R.string.nodata, Toast.LENGTH_LONG).show();
	           }else{
	        	  startActivity(new Intent(this, WorkOverviewActivity.class));
	        	  
	           }
	           
	           break;
	      case 2 :
	         //  startActivity (new Intent(getApplicationContext(), F2Activity.class));
	    	  if (Build.VERSION.SDK_INT < 11) {
	    		  startActivity(new Intent(this, EditPreferences.class));
	    		} else {
	    		   startActivity(new Intent(this, EditPreferences_Honey.class));
	    		} 
	           break;
	      case 3 :
	    	   preferences = PreferenceManager.getDefaultSharedPreferences(this);
		       urlPath = preferences.getString("url", "");
		       resetDropbox=preferences.getBoolean("dropboxreset", false);
		       offline = preferences.getBoolean("updateOffline", false);
		       dropBox = preferences.getBoolean("dropbox", false);
		       format = preferences.getString("listFormat","-1");
		       

		      // Log.d(TAG, "Backendsoftware set to " + backEndSoftware);
		       //removeLicenseFlag(); //only for testing
		       //** checking if product is licensed !!!!
		       //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		       //licensed = sharedPref.getBoolean("LICENSED", false);
		       //** The following lines are to disable for productive use
		       licensed=app.getLicense(); 
		       licensed=true; //only for TESTING, disabling this line for productive use !!!
		       
		      if (licensed==false){ //not licensed or still to check
		    	   String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			       Log.i("Device Id", deviceId);
			       mHandler = new Handler();
			       mLicenseCheckerCallback = new MyLicenseCheckerCallback();
			       mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new   AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
			       doCheck();
		     }
		      if (licensed==true){ //seems to be licensed, go on..
		    	  app.setLicense(true);
		    	   
		    	   if (dropBox == true){ //DropBox sync
			    	   mDbxAcctMgr = DbxAccountManager.getInstance(MofaApplication.getInstance(), MofaApplication.appKey, MofaApplication.appSecret);
			    	   if (resetDropbox == true){ //resetting the link if enabled in preferences
			    		   mDbxAcctMgr.unlink();
			    		   Editor editor = preferences.edit(); //resetting this preference to false
			    		   editor.putBoolean("dropboxreset", false);
			    		   editor.commit();
			    	   }
			    	 //  mDbxAcctMgr.unlink(); //unlinking, only for testing to recreate folder structure
			    	   if (mDbxAcctMgr.hasLinkedAccount()==false){  // first call, we have to link
			    		  // Log.d(TAG, "[DropBox - Starting linking process]");
			    		   mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
			    	   }else{
			    		  // Log.d(TAG, "[DropBox - Already linked]");
			    		   importFromDropbox(); // checking if there are new import files
			    	   }
			    /**
			     * handling cases different from Dropbox	   
			     */
			       }else{
			    	   if (urlPath == ""){
				    	   //Toast.makeText(getApplicationContext(), R.string.restpathemptystring, Toast.LENGTH_LONG).show();
				    	  urlPath = DROPBOX_IMPORT_PATH;
				       }
			    	   showImportDialog();
			       }
		       }
		       
		       
		       
		       
	           break;
	 //     case 4 :
	 //   	  startActivity(new Intent(this, GPSLocationActivity.class));
	 //          break;
	      case 4: 
	    	  startActivity(new Intent (this, PurchaseActivity.class));
	    	  break;
	      case 5:
	    	  startActivity(new Intent (this, SearchActivity.class));
              break;
           case 6:
               startActivity(new Intent (this, WorkerOverviewActivity.class));
	      default: 
	    	   break;
	   }
	}
	
	/**
	 * 
	 * @param selItems contains an ArrayList of integers to decide which tables are to update
	 * @param url is the URL of the webservice entry point
	 */
	private void updateData(ArrayList<Integer>selItems, String url,Boolean offline,String format){
		@SuppressWarnings("unused")
		String extension;
		
		if (format.equalsIgnoreCase("1")){ //json
			extension =".json";
		}else{
			extension =".xml";
		}
		WebServiceCall importData = new WebServiceCall(this, offline, format, dropBox, backEndSoftware);
		importData.execute(selItems, url);
		

		
	}
	
	
	//DialogBox for Updating Data
	private void showImportDialog(){
		
		 boolean first = false; //only for checking if \n
		 StringBuilder sb = new StringBuilder();
	        final ArrayList<Integer> selElements = new ArrayList<Integer>(); //storing the checked preferences
	        	        
	        // Checking the preferences
	        if(preferences.getBoolean("updateLand", false)) {
	            sb.append(getString(R.string.landtable));
	            first=true;
	            selElements.add(1); // 1 means we update land,
	        }
	        if(preferences.getBoolean("updateVquarter", false)) {
	            if (first){
	            	sb.append("\n");
	          	    }
	            else {
	            	// Special case, its not possible to update only the variety quarters, we have a relation to the land!!! 
	            	// Therefore we add the land-element
	            	selElements.add(1);
	            }
	        	sb.append(getString(R.string.vquartertable));
	            first=true;
	            selElements.add(2); // 2 means we update the variety quarter
	        }
	        if(preferences.getBoolean("updateMachine", false)) {
	            if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.machinetable));
	            first=true;
	            selElements.add(3);
	        }
	        if(preferences.getBoolean("updateWorker", false)) {
	            if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.workertable));
	            first=true;
	            selElements.add(4);
	        }
	        if(preferences.getBoolean("updateTask", false)) {
	            if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.tasktable));
	            first=true;
	            selElements.add(5);
	        }
	        if(preferences.getBoolean("updatePesticide", false)){
	        	if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.pesticidetable));
	            first=true;
	            selElements.add(6);
	        }
	        if(preferences.getBoolean("updateFertilizer", false)){
	        	if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.fertilizertable));
	            first=true;
	            selElements.add(7);
	        }
	        if(preferences.getBoolean("updateSoilFertilizer", false)){
	        	if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.soilfertilizertable));
	            first=true;
	            selElements.add(8);
	        }
	        
	        if (selElements.size()>0){
				showAlertDialog(sb,selElements);
			}else{ // no updates
				showNoUpdateDialog();
			}
			 
	       
	}
	//deleting the table entries
	private void flushData(ArrayList<Integer>selItems){
		
		for(Integer i:selItems){
			switch(i)
			{
			case 1:
				DatabaseManager.getInstance().flushVQuarter(); //deleting vquarter too
				DatabaseManager.getInstance().flushLand();
				break;
			case 2:
				DatabaseManager.getInstance().flushVQuarter();
				break;
			case 3:
				DatabaseManager.getInstance().flushMachine();
				break;
			case 4:
				DatabaseManager.getInstance().flushWorker();
				break;
			case 5:
				DatabaseManager.getInstance().flushTask();
				break;
			case 6:
                Log.d(TAG, "Deleting existing entries");
				DatabaseManager.getInstance().flushPesticideNew();
				break;
			case 7:
				DatabaseManager.getInstance().flushFertilizerNew();
				break;
			case 8:
				DatabaseManager.getInstance().flushSoilFertilizer();
				break;
			case 9:
				DatabaseManager.getInstance().flushQuality();
				break;
			default:
				break;
				
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { //inflating the menu
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}
	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Log.d(TAG, "showing about dialog");
			AboutDialog about = new AboutDialog(this);
			about.setTitle("about MoFa");
			about.show();
			return true;
		case R.id.menu_test:
//			Log.d(TAG, "automatically filling DB");
			DatabaseTestDB.init(this);
			DatabaseTestDB.getInstance().createTestRecords();
			return true;
		
		}
		return super.onMenuItemSelected(featureId, item);
	}
	@SuppressWarnings("deprecation")
	private void showAlertDialog(StringBuilder sb,final ArrayList<Integer>selElements ){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
		 alertDialog.setTitle(R.string.importTitle);
		// Adding a checkbox for reimport all data
	        final CheckBox checkBox = new CheckBox(this);
	        checkBox.setText("Reimport ALL DATA");
	     //   if (Integer.parseInt(backEndSoftware)==1){ //special case ASA - we need to delete local data
	     //   	checkBox.setChecked(true);
	     //   	checkBox.setClickable(false);
	     //   }
	        Log.d(TAG,"Array contains: " + selElements.toString());
	        // Adding a listener for the checkbox with a
	        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
	        		if ((isChecked==true)&&((DatabaseManager.getInstance().getAllWorks().size()>0)||(DatabaseManager.getInstance().getAllPurchases().size()>0))){
	        			Toast.makeText(getApplicationContext(), R.string.reimportmessage,Toast.LENGTH_LONG).show();
	        		}
	        	}
	        });
	        LinearLayout linearLayout = new LinearLayout(this);
	        linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
	                                LinearLayout.LayoutParams.FILL_PARENT));
	        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
	        linearLayout.addView(checkBox);
	        alertDialog.setView(linearLayout);
	        alertDialog.setMessage(sb);
	        alertDialog.setPositiveButton(R.string.yesbutton, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	            if (checkBox.isChecked()==true) {
	            	if ((DatabaseManager.getInstance().getAllWorks().size()==0)&&(DatabaseManager.getInstance().getAllPurchases().size()==0) ) { // works table is empty
	            		flushData(selElements);
		            	if (dropBox==false){
		            		updateData(selElements,urlPath,offline,format); //starting the import of data
		            	}else{
		            		updateData(selElements,DROPBOX_IMPORT_PATH,offline,format); //starting the import of dropbox data
		            	}
	            		
	            	}else{ // works table not empty first export
	            		//Toast.makeText(getApplicationContext(), R.string.reimportmessage,Toast.LENGTH_LONG).show();
	            		if (DatabaseManager.getInstance().getAllWorks().size()!=0){
	            			SendingProcess sending = new SendingProcess(HomeActivity.this,ActivityConstants.WORK_OVERVIEW); //first make the export
				    		sending.sendData();
				    		Toast.makeText(getApplicationContext(), R.string.export_status_message,Toast.LENGTH_LONG).show();
	            		}
	            		if (DatabaseManager.getInstance().getAllPurchases().size()!=0){
	            			Toast.makeText(getApplicationContext(), R.string.reimportmessage,Toast.LENGTH_LONG).show();
	            		}
//			    		flushData(selElements); //deleting the old data
//		            	if (dropBox==false){  
//		            		updateData(selElements,urlPath,offline,format); //starting the import of data
//		            	}else{
//		            		updateData(selElements,DROPBOX_IMPORT_PATH,offline,format); //starting the import of dropbox data
//		            	}
	            	}
	            	
	            }else{
	           	// the standard case, only a update
	            	if (dropBox==false){
	            		updateData(selElements, urlPath,offline,format);
	            	}else{
	            		updateData(selElements,DROPBOX_IMPORT_PATH,offline,format);
	            	}
	            	
	            
	            }
	            
	            }
	        });
	 
	        // Setting Negative "NO" Button
	        alertDialog.setNegativeButton(R.string.nobutton, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            
	            dialog.cancel();
	            }
	        });
	        alertDialog.show();
	}
	private void showNoUpdateDialog(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
		alertDialog.setTitle(R.string.noUpdatesInfo);
		alertDialog.setMessage(R.string.noupdate)
		 .setPositiveButton("OK", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 dialog.cancel();
             }
         });

		alertDialog.show();
	}
	
	/**
	 * DropBox Operation for import
	 */
	private void importFromDropbox(){
		String extension;
		String filename = "/list"; //the filename is always list
		final ArrayList<Integer> selElements = new ArrayList<Integer>(); //storing the elements to import/update
		StringBuilder sb = new StringBuilder();
		boolean first = false; //only for checking if \n
		if (format.equalsIgnoreCase("1")){ //json
			extension =".json";
		}else{
			extension =".xml";
		}
		filename = filename + extension;

		DbxPath importPathWorker = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/worker" + filename);
		DbxPath importPathMachine = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/machine" + filename);
		DbxPath importPathTasks = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH+ "/task"+ filename);
		DbxPath importPathLands = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/land"+ filename);
		DbxPath importPathVquarter = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/vquarter"+ filename);
		DbxPath importPathFertilizer = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/fertilizer"+ filename);
		DbxPath importPathSoilFertilizer = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/soilfertilizer"+ filename);
		DbxPath importPathPesticide = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/pesticide"+ filename);	
		DbxPath importPathCategory = new DbxPath(DbxPath.ROOT , DROPBOX_IMPORT_PATH + "/category"+ filename);	

        try{
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            dbxFs.setMaxFileCacheSize(0); //setting the cache to 0 MB
			if (dbxFs.exists(importPathLands)) {
				refreshFile(importPathLands, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of land exists");
				 sb.append(getString(R.string.landtable));
				   	 first=true;
			         selElements.add(1); // 1 means we update land,
		         };
			
			if (dbxFs.exists(importPathVquarter)) {
				refreshFile(importPathVquarter, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of vquarter exists");
				 if (first){
		            	sb.append("\n");
		          	    }
		          //  else {
		            	// Special case, its not possible to update only the variety quarters, we have a relation to the land!!! 
		            	// Therefore we add the land-element
		           // 	selElements.add(1);
		           // }
		        	sb.append(getString(R.string.vquartertable));
		               first=true;
			            selElements.add(2); // 2 means we update the variety quarter
		           }
			
			if (dbxFs.exists(importPathMachine)) {
				refreshFile(importPathMachine, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of machine exists");
				if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.machinetable));
	            first=true;
	            selElements.add(3);
	            
			}
			if (dbxFs.exists(importPathWorker)) {
				refreshFile(importPathWorker, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of worker exists");
				 if (first){
		            	sb.append("\n");
		          	    }
		        	sb.append(getString(R.string.workertable));
		            first=true;
		            selElements.add(4);
		            
				}
			if (dbxFs.exists(importPathTasks)) {
				refreshFile(importPathTasks, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of tasks exists:" + importPathTasks.toString());
				if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.tasktable));
	            first=true;
	            selElements.add(5);
				

			}
			if (dbxFs.exists(importPathPesticide)) {
				refreshFile(importPathPesticide, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of pesticide exists");
				if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.pesticidetable));
	            first=true;
	            selElements.add(6);
				
			}
			if (dbxFs.exists(importPathFertilizer)) {
				refreshFile(importPathFertilizer, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of fertilizer exists");
				if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.fertilizertable));
	            first=true;
	            selElements.add(7);
			
			}
			if (dbxFs.exists(importPathSoilFertilizer)) {
				refreshFile(importPathSoilFertilizer, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of soilfertilizer exists");
				if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.soilfertilizertable));
	            first=true;
	            selElements.add(8);
				 
			}
			if (dbxFs.exists(importPathCategory)) {
				refreshFile(importPathCategory, dbxFs);
				Log.d(TAG,"[importFromDropbox] Import file of category exists");
				if (first){
	            	sb.append("\n");
	          	    }
	        	sb.append(getString(R.string.categorytable));
	            first=true;
	            selElements.add(9);
				 
			}
			
            
		}catch (IOException e){
			Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
		}
		if (selElements.size()>0){
			showAlertDialog(sb,selElements);
		}else{ // no updates
			showNoUpdateDialog();
		}
		 
		
		
	}
	private void refreshFile(DbxPath dropboxPath, DbxFileSystem dbxFs){
		
		try {
		if (dbxFs.isFile(dropboxPath)) {
            DbxFile testFile = dbxFs.open(dropboxPath);
            DbxFileStatus status = testFile.getSyncStatus(); //get sync status
            //Log.d (TAG, "[DROPBOX] getting newer file");
            if (status.isLatest==false){ // not the newest file
            	Log.d (TAG, "[HomeActivity - DROPBOX] getting newer file " + dropboxPath.toString());
            	testFile.getNewerStatus(); //getting newer file
            	
            }
            
             testFile.close();
                      
			}
		
		}catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	private void createDropBoxFolders(){
		
		final String ROOT_IMPORT_PATH = "MoFaBackend/import";
		final String ROOT_EXPORT_PATH = "MoFaBackend/export";
		try{
			DbxPath importPath = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH);
			DbxPath exportPath = new DbxPath(DbxPath.ROOT, ROOT_EXPORT_PATH);
			DbxPath importPathWorker = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/worker");
			DbxPath importPathMachine = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/machine");
			DbxPath importPathTasks = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/task");
			DbxPath importPathLands = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/land");
			DbxPath importPathVquarter = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/vquarter");
			DbxPath importPathFertilizer = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/fertilizer");
			DbxPath importPathSoilFertilizer = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/soilfertilizer");
			DbxPath importPathPesticide = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/pesticide");
			DbxPath importPathCategory = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH + "/category");
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			if (!dbxFs.exists(importPath)) {
				Log.d(TAG, "[createDropBoxFolders] Creating folder");
				dbxFs.createFolder(importPath);
				dbxFs.createFolder(exportPath);
				dbxFs.createFolder(importPathWorker);
				dbxFs.createFolder(importPathMachine);
				dbxFs.createFolder(importPathTasks);
				dbxFs.createFolder(importPathLands);
				dbxFs.createFolder(importPathVquarter);
				dbxFs.createFolder(importPathPesticide);
				dbxFs.createFolder(importPathFertilizer);
				dbxFs.createFolder(importPathSoilFertilizer);
				dbxFs.createFolder(importPathCategory);
			}
		}catch (IOException e) {
       	Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
      }
		
		
		
	}
	/**
	 * only for dropbox; checking if ok
	 */
	/**
	 * callback after signing in for the first time
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		final String ROOT_IMPORT_PATH = "MoFaBackend/import";
	//    Log.d(TAG, "[onActivityResult] Callback from Dropbox");
		if (requestCode == REQUEST_LINK_TO_DBX) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d(TAG, "[onActivityResult] OK - Callback OK from Dropbox");
	        	try {
					DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
                    dbxFs.setMaxFileCacheSize(0);
					DbxPath importPath = new DbxPath(DbxPath.ROOT , ROOT_IMPORT_PATH);
					if (!dbxFs.exists(importPath)){
						 createDropBoxFolders(); // we have to create the folder structure
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	           
	        } else {
	        	Toast.makeText(getApplicationContext(), R.string.dropboxconnerror, Toast.LENGTH_LONG).show(); //error
	        }
	    } else {
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	}
	public void deleteAllEntries() {
		List<Work> workList;
		workList = DatabaseManager.getInstance().getAllWorksOrderByDate();
		for (Work w : workList){
			DatabaseManager.getInstance().deleteCascWork(w);
		}
		
	}
    private void doCheck() {

        didCheck = false;
        checkingLicense = true;
        setProgressBarIndeterminateVisibility(true);

        mChecker.checkAccess(mLicenseCheckerCallback);
    }
 //only for testing purpose, resetting the licensing flag
    private void removeLicenseFlag(){
    	SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
    	sharedPref.edit().remove("LICENSED").commit();
    }
   //check the license
    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {

     //   @Override
        public void allow(int reason) {
            // TODO Auto-generated method stub
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }               
            Log.i("License","Accepted!");       
            	//SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            	//SharedPreferences.Editor editor = sharedPref.edit();
            	//editor.putBoolean("LICENSED", true);
            	//editor.commit();
            
                //You can do other things here, like saving the licensed status to a
                //SharedPreference so the app only has to check the license once.
            
            licensed = true;
            checkingLicense = false;
            didCheck = true;

        }

        @SuppressWarnings("deprecation")
      //  @Override
        public void dontAllow(int reason) {
            // TODO Auto-generated method stub
             if (isFinishing()) {
                    // Don't update UI if Activity is finishing.
                    return;
                }
                Log.i("License","Denied!");
                Log.i("License","Reason for denial: "+reason);                                                                              

                        //You can do other things here, like saving the licensed status to a
                        //SharedPreference so the app only has to check the license once.

                licensed = false;
                checkingLicense = false;
                didCheck = true;               

                showDialog(0);

        }

        @SuppressWarnings("deprecation")
     //   @Override
        public void applicationError(int reason) {
            // TODO Auto-generated method stub
            Log.i("License", "Error: " + reason);
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            licensed = true;
            checkingLicense = false;
            didCheck = false;

            showDialog(0);
        }


    }

    protected Dialog onCreateDialog(int id) {
        // We have only one dialog.
        return new AlertDialog.Builder(this)
                .setTitle("UNLICENSED APPLICATION DIALOG TITLE")
                .setMessage("This application is not licensed, please buy it from the play store.")
                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://market.android.com/details?id=" + getPackageName()));
                        startActivity(marketIntent);
                        finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("Re-Check", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doCheck();
                    }
                })

                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener(){
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        Log.i("License", "Key Listener");
                        finish();
                        return true;
                    }
                })
                .create();

    }


}
