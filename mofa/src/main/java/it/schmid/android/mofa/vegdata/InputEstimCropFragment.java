package it.schmid.android.mofa.vegdata;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.schmid.android.mofa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputEstimCropFragment extends Fragment {


    public InputEstimCropFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_estim_crop, container, false);
    }

}
