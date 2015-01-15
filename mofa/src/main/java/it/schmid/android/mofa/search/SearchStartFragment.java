package it.schmid.android.mofa.search;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.actionbarsherlock.app.SherlockFragment;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.R;

/**
 * Created by schmida on 09.12.14.
 */
public class SearchStartFragment extends SherlockFragment {
    private static final String TAG = "SearchStartFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_frag_start, container, false);
        Button btnLastSprayOp = (Button) v.findViewById(R.id.btn_search_last_sprayop);
        Button btnSearchPest = (Button) v.findViewById(R.id.btn_search_pest);
        btnLastSprayOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnLastSprayOP clicked, loading new fragment");
                SherlockFragment landFragment = SearchLandFragment.newInstance(R.string.searchVQuarter,ActivityConstants.SEARCH_LAST_PEST);
                FragmentTransaction transaction = getSherlockActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.search_fragment_container, landFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        btnSearchPest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnSearchPest clicked, loading new fragment");
                SherlockFragment pestFragment = SearchPestFragment.newInstance(R.string.searchPestSel,ActivityConstants.SEARCH_PEST);
                FragmentTransaction transaction = getSherlockActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.search_fragment_container, pestFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return v;

    }
}
