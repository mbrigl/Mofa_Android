package it.schmid.android.mofa;


import it.schmid.android.mofa.adapter.WorkAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.misc.TransactionManager;


public class WorkOverviewActivity extends DashboardActivity implements SendingProcess.RemoveEntries{
	private static final String TAG = "WorkOverviewActivity";
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private List<Work> workList; // list of works
	private WorkAdapter adapter; // adapter for works
	private ActionBar actionBar;
	ListView listViewWork; // listview of work
	ImageView delIcon; // image

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this); //initialize the DatabaseManager
		
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        setContentView(R.layout.work_list);
		// ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.work_list,null);
		 listViewWork = (ListView) findViewById(R.id.listViewWork);
		 delIcon = (ImageView) findViewById(R.id.delete_icon);
		// setContentView(contentView);
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		fillData(listViewWork); //filling the list
	}
	/**
	 * Filling the listview with data
	 * @param lv is a reference to the listview
	 */
	private void fillData(ListView lv){
		
		//workList = DatabaseManager.getInstance().getAllWorksOrderByDate();
		workList = DatabaseManager.getInstance().getAllNotSendedWorks();
		Log.d(TAG,"Number of works:" + workList.size());
		adapter = new WorkAdapter(this, R.layout.work_row, workList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener(){ // listener for click event
		 public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
			Work work = adapter.getItem(position); //current work
			Log.d(TAG, "Current work with id: " + work.getId());
			Intent i = new Intent(WorkOverviewActivity.this, WorkEditTabActivity.class); // opening the corresponding activity
			i.putExtra("Work_ID", work.getId());
			
			// Activity returns an result if called with startActivityForResult
			startActivityForResult(i, ACTIVITY_EDIT);
		 	}
		});
		
	}
		
	// Called with the result of the other activity
		// requestCode was the origin request code send to the activity
		// resultCode is the return code, 0 is everything is ok
		// intend can be used to get data
		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent intent) {
			super.onActivityResult(requestCode, resultCode, intent);
			fillData(listViewWork); // refilling the controls

		}
		@Override
		public boolean onCreateOptionsMenu(final Menu menu) { //inflating the menu
			
			MenuInflater inflater = getSupportMenuInflater();
			inflater.inflate(R.menu.work_menu, menu);
			
			
			SpinnerAdapter mSpinnerAdapter;
			   if(Build.VERSION.SDK_INT <= 10)
			   {
			      mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.nav_list,android.R.layout.simple_spinner_item);
			   }
			   else
			   {
			      mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.nav_list,android.R.layout.simple_spinner_dropdown_item);
			   }
			   OnNavigationListener mOnNavigationListener = new OnNavigationListener()
			   {
				   
			      //Filtering the works
			 
			      public boolean onNavigationItemSelected(int position, long itemId)
			      {
			    	  String backEndSoftware;
			    	  MofaApplication app = MofaApplication.getInstance();
			  	      backEndSoftware = app.getBackendSoftware();
			         switch (position)
			         {
			         	case 0:
			         		 workList = DatabaseManager.getInstance().getAllWorksOrderByDate();
			                 adapter = new WorkAdapter(WorkOverviewActivity.this, R.layout.work_row, workList);
			                 listViewWork.setAdapter(adapter);
			                 
			                 break;
			         	case 1:
			         		try {
			         			if (Integer.parseInt(backEndSoftware)==1){//ASA Case
			         				workList = DatabaseManager.getInstance().getWorksForTaskIdOrderedASA("S");
			         			}else{
			         				workList = DatabaseManager.getInstance().getWorksForTaskIdOrdered(1);
			         			}
			         			
			         		} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
			         		}
			                 adapter = new WorkAdapter(WorkOverviewActivity.this, R.layout.work_row, workList);
			                 listViewWork.setAdapter(adapter);
			                 break;
			            case 2:
			            	try {
			            		if (Integer.parseInt(backEndSoftware)==1){//ASA Case
			            			workList = DatabaseManager.getInstance().getWorksForTaskIdOrderedASA("H");
			            		}else{
			            			workList = DatabaseManager.getInstance().getWorksForTaskIdOrdered(2);
			            		}
			            	} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
			            	}
			                 adapter = new WorkAdapter(WorkOverviewActivity.this, R.layout.work_row, workList);
			                 listViewWork.setAdapter(adapter);
			                 break;
			            case 3:
			            	try {
			            		if (Integer.parseInt(backEndSoftware)==1){//ASA Case
			            			workList = DatabaseManager.getInstance().getWorksForTaskIdOrderedASA("D");
			            		}else{
			            			workList = DatabaseManager.getInstance().getWorksForTaskIdOrdered(3);
			            		}
			            	} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
			            	}
			                 adapter = new WorkAdapter(WorkOverviewActivity.this, R.layout.work_row, workList);
			                 listViewWork.setAdapter(adapter);
			                 break;
			            default:
			            	try { 
			            		if (Integer.parseInt(backEndSoftware)==1){//ASA Case, we get all codes and then we search for all others
			            			workList = DatabaseManager.getInstance().getWorksForTaskIdOrderedASARest();
			            		}else{
			            			workList = DatabaseManager.getInstance().getWorksForTaskIdOrdered(4);
			            		}
				            } catch (SQLException e) {
								// TODO Auto-generated catch block
							e.printStackTrace();
				            }
				            adapter = new WorkAdapter(WorkOverviewActivity.this, R.layout.work_row, workList);
				            listViewWork.setAdapter(adapter);
				            break;
			          }
			                   
			          return true;
			        }
			     };
			               
			     actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
			     
			return true;
		}
		// Reaction to the menu selection
		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.work_menu_add:
				Log.d(TAG, "Adding an work");
				createWork();
				return true;
			case R.id.work_menu_upload:
				Log.d(TAG, "Upload the work entries");
				MofaApplication app = MofaApplication.getInstance();
				Boolean haveConnection = app.networkStatus();
				if (haveConnection){
					showUploadDialog();
				}else{
					Toast.makeText(getApplicationContext(), R.string.no_connection,Toast.LENGTH_LONG).show();
				}
				
				return true;
			}
			return super.onMenuItemSelected(featureId, item);
		}
		/**
		 * creating a new work
		 */
		private void createWork(){
			Intent i = new Intent(this,WorkEditTabActivity.class);
			startActivityForResult(i, ACTIVITY_CREATE);
		}
		/**
		 * export dialog
		 */
		private void showUploadDialog(){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(WorkOverviewActivity.this);
			alertDialog.setTitle(getString(R.string.export_title));
	        StringBuilder sb = new StringBuilder();
	        sb.append(getString(R.string.export_message));
			 LinearLayout linearLayout = new LinearLayout(this);
		     linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		                                LinearLayout.LayoutParams.FILL_PARENT));
		     linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		     alertDialog.setView(linearLayout);
		     alertDialog.setMessage(sb);
		     alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		    	 public void onClick(DialogInterface dialog,int which) {
		    		 //sendData();
		    		 SendingProcess sending = new SendingProcess(WorkOverviewActivity.this,ActivityConstants.WORK_OVERVIEW);
		    		 sending.sendData();
		    	 }
		     });
		  // Setting Negative "NO" Button
		        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            
		            dialog.cancel();
		            }
		        });
		  alertDialog.show();
		}
		/**
		 * function for uploading the data
		 */
	

	public void deleteAllEntriesOrg(){
		List<Work> removeWorkList=DatabaseManager.getInstance().getAllValidWorks();
		for (Work w : removeWorkList){
			DatabaseManager.getInstance().deleteCascWork(w);
		}
		//adapter.notifyDataSetChanged();
		fillData(listViewWork);
	}
    public void deleteAllEntries(){
        try {
            TransactionManager.callInTransaction(DatabaseManager.getInstance().getConnection(),
                    new Callable<Void>() {
                        public Void call() throws Exception {
                            List<Work> removeWorkList=DatabaseManager.getInstance().getAllValidNotSprayWorks();
                            for (Work w : removeWorkList){
                                DatabaseManager.getInstance().deleteCascWork(w);
                            }
                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //adapter.notifyDataSetChanged();
        fillData(listViewWork);
    }
		
	
}
