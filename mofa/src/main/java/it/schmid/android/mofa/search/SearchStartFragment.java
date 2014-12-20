package it.schmid.android.mofa.search;

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
        btnLastSprayOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnLastSprayOP clicked, loading new fragment");
                SherlockFragment landFragment = SearchLandFragment.newInstance(R.string.searchLastPest);
                FragmentTransaction transaction = getSherlockActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.search_fragment_container, landFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        return v;

    }
}
