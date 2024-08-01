package it.schmid.android.mofa;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBar.Tab;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;

/**
 * @author schmida
 * Main Class for works
 */
public class WorkEditTabActivity extends AppCompatActivity implements WorkEditWorkFragment.SetWorkIdListener, WorkEditWorkFragment.CompleteBehaviour,
        WorkEditWorkFragment.ShowHarvestTabListener {
    private static final String TAG = "WorkEditTabActivity";
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    TextView tabCenter;
    TextView tabText;
    private Bundle mBundle; //bundle for workid
    private Integer workId = 0; //current workid
    private Boolean continueEnabled = false;
    static final int DATE_DIALOG_ID = 0;
    private final Boolean sprayToCheck = false; //check variable for spraying
    private final Boolean fertToCheck = false;

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
        mBundle = getIntent().getExtras();
        if (null != mBundle && mBundle.containsKey("Work_ID")) {
            workId = mBundle.getInt("Work_ID");
            Log.d(TAG, "[OnCreate] CurrWorkID= " + workId);
        }
        mTabsAdapter = new TabsAdapter(this, mViewPager);

        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.worktab),
                WorkEditWorkFragment.class, null);
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.workrestab),
                WorkEditResourcesFragment.class, null);


    }

    //callback method from workeditworkfragment, after selecting a work contains harvest codes
    public void showHarvestTabListener(int workId, Boolean status) {
    }

    public void setWorkIdListener(int workId) {
        //Log.d (TAG, "[callback - setWorkIdListener] - Invoking the callback method with workid: " + workId);
        this.workId = workId;

    }

    public void setContinue(boolean complete) {

        this.continueEnabled = complete;
    }

    public Boolean getContinueEnabled() {
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
        Boolean valid = false;
        Log.d(TAG, "[onPause] land is " + mofaApplication.getGlobalVariable("land"));
        Log.d(TAG, "[onPause] worker is " + mofaApplication.getGlobalVariable("worker"));
        if (mofaApplication.getGlobalVariable("land").equalsIgnoreCase("valid") && mofaApplication.getGlobalVariable("worker").equalsIgnoreCase("valid")) {
            //	Log.d(TAG, "[onPause] land and worker are valid" );
            valid = true;
        }

        //writing the validity flat into the DB
        if (workId != 0) {
            Work w = DatabaseManager.getInstance().getWorkWithId(workId);
            if (w.getValid() != valid) {
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
        Log.d(TAG, "onResume in WorkEditTabActivity");

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("Work_ID", workId);
        super.onSaveInstanceState(savedInstanceState);

    }


    public static class TabsAdapter extends FragmentPagerAdapter implements
            ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(AppCompatActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(),
                    info.args);
        }

        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        public void onPageScrollStateChanged(int state) {
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {

            int i = tab.getPosition();
            Log.d(TAG, "WorkEditTabActivty - Tab unselected: " + i);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }


}
