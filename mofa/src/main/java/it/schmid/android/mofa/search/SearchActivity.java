package it.schmid.android.mofa.search;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.DashboardActivity;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.interfaces.ProductInterface;

/**
 * Created by schmida on 08.12.14.
 */
public class SearchActivity extends DashboardActivity implements SearchLandFragment.OnLandFragmentListener,SearchResult.GetVQListener,SearchPestFragment.OnFragmentPesticideListener {
    private static final String TAG ="SearchActivity";
    private ArrayList<Integer> selVQList;
    private int prodId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.search_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SearchStartFragment firstSearchFragment = new SearchStartFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstSearchFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_fragment_container, firstSearchFragment).commit();
        }


    }

    //callback, when selected the vquarters
    public void onLandFragmentInteraction(HashMap<Integer, ArrayList<Integer>> selElements, int searchType) {

        selVQList = new ArrayList<Integer>();
        for (HashMap.Entry<Integer, ArrayList<Integer>> e : selElements.entrySet()){
            ArrayList<Integer> entries = e.getValue();
            selVQList.addAll(entries);
        }
        Log.d(TAG, "Callback from fragment with following entries: " + selVQList.toString());
        if (searchType==ActivityConstants.SEARCH_LAST_PEST){
            Fragment searchResult = SearchResult.newInstance(R.string.searchLastPest,searchType);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.search_fragment_container, searchResult);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(searchType==ActivityConstants.SEARCH_PEST){
            Fragment searchResult = SearchResult.newInstance(R.string.searchPest,searchType);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.search_fragment_container, searchResult);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(searchType==ActivityConstants.SEARCH_FERT){
            Fragment searchResult = SearchResult.newInstance(R.string.searchFert,searchType);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.search_fragment_container, searchResult);
            transaction.addToBackStack(null);
            transaction.commit();
        }


    }

    public ArrayList<Integer> getVQList() {
        return selVQList;
    }
    public int getProdId(){
        return prodId;
    }

    public void closeActivity() {

        this.finish();
    }
    //callback, when selecting a product(pesticide) item in the list,
    //we get the id and then we create the searchlandfragment.

    public void onFragPestInteraction(ProductInterface p, int searchType) {
        prodId = p.getId();
        Fragment landFragment = SearchLandFragment.newInstance(R.string.searchVQuarter, searchType);
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.search_fragment_container, landFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        Log.d(TAG, "[onFragPestInteraction] Selected product: " + p.getProductName());
    }
}
