package it.schmid.android.mofa.search;

import android.net.Uri;
import android.os.Bundle;

import it.schmid.android.mofa.DashboardActivity;
import it.schmid.android.mofa.R;

/**
 * Created by schmida on 08.12.14.
 */
public class SearchActivity extends DashboardActivity implements SearchLandFragment.OnLandFragmentListener {

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

    @Override
    public void onLandFragmentInteraction(Uri uri) {

    }
}
