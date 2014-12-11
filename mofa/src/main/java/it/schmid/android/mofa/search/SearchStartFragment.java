package it.schmid.android.mofa.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import it.schmid.android.mofa.R;

/**
 * Created by schmida on 09.12.14.
 */
public class SearchStartFragment extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_frag_start, container, false);

    }
}
