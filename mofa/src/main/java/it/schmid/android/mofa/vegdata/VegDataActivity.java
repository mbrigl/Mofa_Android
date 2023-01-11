package it.schmid.android.mofa.vegdata;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.SendingProcess;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.ClearInterface;
import it.schmid.android.mofa.model.Global;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;

public class VegDataActivity extends AppCompatActivity implements BlossomStartFragment.OnBlossomStartInteractionListener, BlossomEndFragment.OnBlossomEndInteractionListener, EstimCropFragment.OnEstimCropInteractionListener,HarvestStartFragment.OnHarvestStartInteractionListener,SendingProcess.RemoveEntries {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public HashMap<Land,List<VQuarter>> landMap= new HashMap<Land,List<VQuarter>>();
    public List<Land> lands;
    private String jsonStringBlossStart="";
    private String jsonStringBlossEnd="";
    private String jsonStringEstimCrop="";
    private String jsonStringHarvestStart="";
    private boolean unSavedValues = false;
    private int[] tabIcons = {
            R.drawable.ic_action_flowerstart,
            R.drawable.ic_action_flowerend,
            R.drawable.ic_action_harveststart,
            R.drawable.ic_action_estimcrop
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.init(this);
        lands = DatabaseManager.getInstance().getAllLandsOrdered();
        for (Land land : lands) {
            List<VQuarter> vquarters = land.getVQuarters();
            landMap.put(land,vquarters);

        }
        setContentView(R.layout.activity_veg_data);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment blossomStartFragment = BlossomStartFragment.newInstance(getBlossomStartJson());
        Fragment blossomEndFragment = BlossomEndFragment.newInstance(getBlossomEndJson());
        Fragment harvestStartFragment = HarvestStartFragment.newInstance(getHarvestStartJson());
        Fragment estimCropFragment = EstimCropFragment.newInstance(getEstimCropJson());

        adapter.addFragment(blossomStartFragment, getString(R.string.blossomStart));
        adapter.addFragment(blossomEndFragment, getString(R.string.blossomEnd));
        adapter.addFragment(harvestStartFragment,getString(R.string.harvestStart));
        adapter.addFragment(estimCropFragment,getString(R.string.estimCrop));

        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vegdata_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.vegdata_clearContent:
                resetAllVegData();
                return true;
            case R.id.vegdata_menu_upload:
                MofaApplication app = MofaApplication.getInstance();
                Boolean haveConnection = app.networkStatus();
                if (haveConnection){
                    showUploadDialog();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.no_connection,Toast.LENGTH_LONG).show();
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBlossomStartInteraction(String jsonString) {
        jsonStringBlossStart = jsonString;
        unSavedValues = true;
        Log.d("VegDataActivity", "CallBack from BlossomStartFragment");
    }
    @Override
    public void onBlossomEndInteraction(String jsonString) {
        jsonStringBlossEnd = jsonString;
        unSavedValues = true;
        Log.d("VegDataActivity", "CallBack from BlossomEndFragment");

    }

    @Override
    public void onEstimCropInteraction(String jsonString) {
        jsonStringEstimCrop = jsonString;
        unSavedValues = true;
        Log.d("VegDataActivity", "CallBack from EstimCropFragment");
    }

    @Override
    public void onHarvestStartInteraction(String jsonString) {
        jsonStringHarvestStart = jsonString;
        unSavedValues = true;
        Log.d("VegDataActivity", "CallBack from HarvestStartFragment");
    }

    private void saveBlossomStart() {

        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).isEmpty()){
            Global blossomStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).get(0);
            blossomStart.setData(jsonStringBlossStart);
            DatabaseManager.getInstance().updateGlobal(blossomStart);
        }else {
            Global blossomStart = new Global();
            blossomStart.setTypeInfo(ActivityConstants.BLOSSOMSTART);
            blossomStart.setData(jsonStringBlossStart);
            DatabaseManager.getInstance().addGlobal(blossomStart);

        }

    }
    private void saveBlossomEnd(){
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).isEmpty()){
            Global blossomEnd = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).get(0);
            blossomEnd.setData(jsonStringBlossEnd);
            DatabaseManager.getInstance().updateGlobal(blossomEnd);
        }else {
            Global blossomEnd = new Global();
            blossomEnd.setTypeInfo(ActivityConstants.BLOSSOMEND);
            blossomEnd.setData(jsonStringBlossEnd);
            DatabaseManager.getInstance().addGlobal(blossomEnd);

        }

    }
    private void saveHarvestStart(){
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.HARVESTSTART).isEmpty()){
            Global harvestStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.HARVESTSTART).get(0);
            harvestStart.setData(jsonStringHarvestStart);
            DatabaseManager.getInstance().updateGlobal(harvestStart);
        }else {
            Global harvestStart = new Global();
            harvestStart.setTypeInfo(ActivityConstants.HARVESTSTART);
            harvestStart.setData(jsonStringHarvestStart);
            DatabaseManager.getInstance().addGlobal(harvestStart);

        }
    }
    private void saveEstimCrop(){
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.ESTIMCROP).isEmpty()){
            Global estimCrop = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.ESTIMCROP).get(0);
            estimCrop.setData(jsonStringEstimCrop);
            DatabaseManager.getInstance().updateGlobal(estimCrop);
        }else {
            Global estimCrop = new Global();
            estimCrop.setTypeInfo(ActivityConstants.ESTIMCROP);
            estimCrop.setData(jsonStringEstimCrop);
            DatabaseManager.getInstance().addGlobal(estimCrop);

        }
    }
    private String getBlossomStartJson(){
        jsonStringBlossStart="";
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).isEmpty()){
          Global blossomStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMSTART).get(0);
            jsonStringBlossStart = blossomStart.getData();
        };

        return jsonStringBlossStart;
    }
    private String getBlossomEndJson(){
        jsonStringBlossEnd="";
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).isEmpty()){
            Global blossomEnd = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.BLOSSOMEND).get(0);
            jsonStringBlossEnd = blossomEnd.getData();
        };
        return jsonStringBlossEnd;
    }

    private String getEstimCropJson(){
        jsonStringEstimCrop="";
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.ESTIMCROP).isEmpty()){
            Global estimCrop = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.ESTIMCROP).get(0);
            jsonStringEstimCrop = estimCrop.getData();
        };
        return jsonStringEstimCrop;
    }
    private String getHarvestStartJson(){
        jsonStringHarvestStart="";
        if (!DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.HARVESTSTART).isEmpty()){
            Global harvestStart = DatabaseManager.getInstance().getGlobalbyType(ActivityConstants.HARVESTSTART).get(0);
            jsonStringHarvestStart = harvestStart.getData();
        };
        return jsonStringHarvestStart;
    }
    private void resetAllVegData() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VegDataActivity.this);
        alertDialog.setTitle(getString(R.string.vegdata_clear_message_title));
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.vegdata_clear_message));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        alertDialog.setView(linearLayout);
        alertDialog.setMessage(sb);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                clearAllFragments();
                DatabaseManager.getInstance().flushVegData();



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
    private void clearAllFragments() {
        adapter.clearAllDataFromFragments();

    }
    /**
     * export dialog
     */
    private void showUploadDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VegDataActivity.this);
        alertDialog.setTitle(getString(R.string.export_vegdata_title));
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.export_vegdata_message));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        alertDialog.setView(linearLayout);
        alertDialog.setMessage(sb);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                //sendData();
                if (unSavedValues) {
                    saveBlossomStart();
                    saveBlossomEnd();
                    saveHarvestStart();
                    saveEstimCrop();
                }
                SendingProcess sending = new SendingProcess(VegDataActivity.this,ActivityConstants.VEGDATA_ACTIVITY);
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

    @Override
    protected void onPause() {
        super.onPause();
        saveBlossomStart();
        saveBlossomEnd();
        saveHarvestStart();
        saveEstimCrop();
        unSavedValues = false;
    }

    @Override
    public void deleteAllEntries() {

        //nothing to do for VegData. Not deleting data, later moment perhaps mark data, that where sended
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }
        public void clearAllDataFromFragments(){
            for (Fragment fm : mFragmentList){
                ((ClearInterface)fm).clear();
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public HashMap<Land, List<VQuarter>> getLandMap() {
        return landMap;
    }

    public List<Land> getLands() {
        return lands;
    }


}
