package it.schmid.android.mofa.search;

import android.os.Bundle;

import it.schmid.android.mofa.DashboardActivity;
import it.schmid.android.mofa.R;

/**
 * Created by schmida on 08.12.14.
 */
public class SearchActivity extends DashboardActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
    }
}
