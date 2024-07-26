package it.schmid.android.mofa.vegdata;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.adapter.ExpandableVegDataAdapter;
import it.schmid.android.mofa.interfaces.ClearInterface;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;

/**
 * A simple {@link Fragment} subclass.
 */
public class EstimCropFragment extends Fragment implements ExpandableVegDataAdapter.VegDataListener, ClearInterface {
    private static final String ARG_PARAM1 = "param1";
    private String mJsonStr;
    private HashMap<Land, List<VQuarter>> landMap;
    private List<Land> lands;
    private OnEstimCropInteractionListener mListener;
    private HashMap<String, String> setAmountMap;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    public EstimCropFragment() {
        // Required empty public constructor
    }

    public static EstimCropFragment newInstance(String param1) {
        EstimCropFragment fragment = new EstimCropFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJsonStr = getArguments().getString(ARG_PARAM1);

        }
        lands = ((VegDataActivity) this.getActivity()).getLands();
        landMap = ((VegDataActivity) this.getActivity()).getLandMap();
        loadHashMapfromJson();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estim_crop, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);
        expandableListAdapter = new ExpandableVegDataAdapter(getActivity(), lands, landMap, setAmountMap, ActivityConstants.ESTIMCROP, this);

        expandableListView.setAdapter(expandableListAdapter);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEstimCropInteractionListener) {
            mListener = (OnEstimCropInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void refreshJson() {
        if (mListener != null) {
            mListener.onEstimCropInteraction(createJsonFromMap());
        }
    }

    private String createJsonFromMap() {

        Gson gson = new Gson();
        String json = gson.toJson(setAmountMap);
        return json;

    }

    private void loadHashMapfromJson() {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        setAmountMap = gson.fromJson(mJsonStr, type);
        if (setAmountMap == null) {
            setAmountMap = new HashMap<String, String>();
        }
    }

    @Override
    public void clear() {
        setAmountMap.clear();
        if (mListener != null) {
            mListener.onEstimCropInteraction(createJsonFromMap());
        }
        ((BaseExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();
    }

    public interface OnEstimCropInteractionListener {
        // TODO: Update argument type and name
        void onEstimCropInteraction(String jsonString);
    }
}
