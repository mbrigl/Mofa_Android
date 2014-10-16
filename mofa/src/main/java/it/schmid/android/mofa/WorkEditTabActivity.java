package it.schmid.android.mofa;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;


/**
 * 
 * @author schmida
 * Main Class for works
 */
public class WorkEditTabActivity extends DashboardActivity implements WorkEditWorkFragment.ShowSprayTabListener,WorkEditWorkFragment.SetWorkIdListener,WorkEditWorkFragment.CompleteBehaviour, 
									WorkEditWorkFragment.ShowSoilFertilizerTabListener,WorkEditWorkFragment.ShowHarvestTabListener
									{
	private static final String TAG = "WorkEditTabActivity";
	ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    TextView tabCenter;
    TextView tabText;
    private Bundle mBundle; //bundle for workid
	private Integer workId=0; //current workid
	private Boolean continueEnabled=false;
	static final int DATE_DIALOG_ID = 0;
	private  Boolean sprayToCheck = false; //check variable for spraying
	private  Boolean fertToCheck = false; 
	
	MofaApplication mofaApplication = MofaApplication.getInstance();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
	        workId = savedInstanceState.getInt("Work_ID");
		}
		
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);

        setContentView(mViewPager);
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mBundle= getIntent().getExtras();
		if (null!=mBundle && mBundle.containsKey("Work_ID")) {
			workId = mBundle.getInt("Work_ID");
			Log.d(TAG,"[OnCreate] CurrWorkID= " + workId);
		}
        mTabsAdapter = new TabsAdapter(this, mViewPager);

        mTabsAdapter.addTab(
                        bar.newTab().setText(R.string.worktab),
                        WorkEditWorkFragment.class, null);
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.workrestab),
                WorkEditResourcesFragment.class, null);
        
      
	}
	
	public void showSprayTab(){
		if (mTabsAdapter.getCount()<=2){ //only adding if there is not already done
		ActionBar bar = getSupportActionBar(); 
		Log.d(TAG, "[showSprayTab] - Size of tabs = " + mTabsAdapter.getCount());			
		mTabsAdapter.addTab(
                bar.newTab().setText(R.string.spraytab),
                WorkEditSprayFragment.class, mBundle);
				sprayToCheck=true;
		}
		
		
	}
	public void showSoilFertTab(){
		if (mTabsAdapter.getCount()<=2){ //only adding if there is not already done
			ActionBar bar = getSupportActionBar(); 
			Log.d(TAG, "[showSoilFertTab] - Size of tabs = " + mTabsAdapter.getCount());			
			mTabsAdapter.addTab(
	                bar.newTab().setText(R.string.soilferttab),
	                WorkEditSoilFertilizerFragment.class, mBundle);
					fertToCheck=true;
			}
		
		
		
	}
	public void showHarvestTab(){
		if (mTabsAdapter.getCount()<=2){ //only adding if there is not already done
			ActionBar bar = getSupportActionBar(); 
			Log.d(TAG, "[showHarvestTab] - Size of tabs = " + mTabsAdapter.getCount());			
			mTabsAdapter.addTab(
	                bar.newTab().setText(R.string.harvesttab),
	                WorkEditHarvestFragment.class, mBundle);
			}
	}
	//callback methods from workeditworkactivity, after selecting the spraying task
	public void showSprayTabListener(int workId, Boolean status){
		
		if (status==true){
			Log.d (TAG, "[callback - showSprayTabListener] - Invoking the callback method with workid: " + workId);
			showSprayTab();
		}
	}
	//callback method from workeditworkactivity, after selecting the fertilizing task -> task.id ==2
	public void showSoilFertilizerTab(int workId, Boolean status) {
		if (status == true){
			showSoilFertTab();
		}
		
	}
	//callback method from workeditworkfragment, after selecting a work contains harvest codes
	public void showHarvestTabListener(int workId, Boolean status) {
		// TODO Auto-generated method stub
		if (status == true){
			showHarvestTab();
		}
		
	}
	
	public void setWorkIdListener(int workId) {
		//Log.d (TAG, "[callback - setWorkIdListener] - Invoking the callback method with workid: " + workId);
		this.workId = workId;
		
	}
	public void setContinue(boolean complete) {
		
		this.continueEnabled = complete;
	}
	 public Boolean getContinueEnabled(){
		 return this.continueEnabled;
	 }
	 public Integer getWorkId() {
			return workId;
	 }
	
	
 //******** finish of control variables
	
	@Override
	public void onPause() {
		super.onPause();
		
		
		//setting the validity of an entry
		Boolean valid=false;
        	Log.d(TAG, "[onPause] land is " + mofaApplication.getGlobalVariable("land").toString() );
            Log.d(TAG, "[onPause] worker is " + mofaApplication.getGlobalVariable("worker").toString() );
		if (mofaApplication.getGlobalVariable("land").equalsIgnoreCase("valid") && mofaApplication.getGlobalVariable("worker").equalsIgnoreCase("valid")){
		//	Log.d(TAG, "[onPause] land and worker are valid" );	
			valid=true;
		}
		//second case spray work
		if (valid && sprayToCheck ){
			if (workId!=0){
				valid = !DatabaseManager.getInstance().sprayIsEmpty(workId); //returns true if no Pesticide/Fertilizer
			//	Log.d(TAG, "[onPause] spray Part is: " + valid);
			}
		}
			
		
		//third case soil fertilizing
		if (valid && fertToCheck){ //fertilizer work
			if (workId!=0){
				valid=!DatabaseManager.getInstance().soilFertIsEmpty(workId);
			}
		}
		//writing the validity flat into the DB	
		if (workId!=0){
			Work w = DatabaseManager.getInstance().getWorkWithId(workId);
			if  (w.getValid()!=valid){
					w.setValid(valid);
					DatabaseManager.getInstance().updateWork(w);
			}
		}
			
		
		
	//	Log.d(TAG, "[onPause] Current work is valid: " + valid);
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
	//	Toast.makeText(this, "Calling on Resume:" + mworkId, Toast.LENGTH_LONG).show();
		Log.d(TAG,"onResume in WorkEditTabActivity");
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  
	   savedInstanceState.putInt("Work_ID",workId);
	  super.onSaveInstanceState(savedInstanceState);
	 
	}
	

	





public static class TabsAdapter extends FragmentPagerAdapter implements
     ActionBar.TabListener, ViewPager.OnPageChangeListener
{

private final Context mContext;
private final ActionBar mActionBar;
private final ViewPager mViewPager;
private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

static final class TabInfo
{
     private final Class<?> clss;
     private final Bundle args;

     TabInfo(Class<?> _class, Bundle _args)
     {
             clss = _class;
             args = _args;
     }
}

public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager)
{
     super(activity.getSupportFragmentManager());
     mContext = activity;
     mActionBar = activity.getSupportActionBar();
     mViewPager = pager;
     mViewPager.setAdapter(this);
     mViewPager.setOnPageChangeListener(this);
}

public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args)
{
     TabInfo info = new TabInfo(clss, args);
     tab.setTag(info);
     tab.setTabListener(this);
     mTabs.add(info);
     mActionBar.addTab(tab);
     notifyDataSetChanged();
}

@Override
public int getCount()
{
     return mTabs.size();
}

@Override
public Fragment getItem(int position)
{
     TabInfo info = mTabs.get(position);
     return Fragment.instantiate(mContext, info.clss.getName(),
                     info.args);
}

public void onPageScrolled(int position, float positionOffset,
             int positionOffsetPixels)
{
}

public void onPageSelected(int position)
{
     mActionBar.setSelectedNavigationItem(position);
}

public void onPageScrollStateChanged(int state)
{
}

public void onTabSelected(Tab tab, FragmentTransaction ft)
{
     Object tag = tab.getTag();
     for (int i = 0; i < mTabs.size(); i++)
     {
             if (mTabs.get(i) == tag)
             {
                     mViewPager.setCurrentItem(i);
             }
     }
}

public void onTabUnselected(Tab tab, FragmentTransaction ft)
{	
	
	int i = tab.getPosition();
	Log.d(TAG, "WorkEditTabActivty - Tab unselected: " + i) ;
}

public void onTabReselected(Tab tab, FragmentTransaction ft)
{
}
}





























   
}
