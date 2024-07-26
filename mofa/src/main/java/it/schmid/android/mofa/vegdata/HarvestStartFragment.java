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
public class HarvestStartFragment extends Fragment implements ExpandableVegDataAdapter.VegDataListener, ClearInterface {
    private static final String ARG_PARAM1 = "param1";
    private String mJsonStr;
    private HashMap<Land, List<VQuarter>> landMap;
    private List<Land> lands;
    private HashMap<String, String> setDateMap;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    private HarvestStartFragment.OnHarvestStartInteractionListener mListener;

    public HarvestStartFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HarvestStartFragment newInstance(String param1) {
        HarvestStartFragment fragment = new HarvestStartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_harvest_start, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);
        expandableListAdapter = new ExpandableVegDataAdapter(getActivity(), lands, landMap, setDateMap, ActivityConstants.HARVESTSTART, this);

        expandableListView.setAdapter(expandableListAdapter);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHarvestStartInteractionListener) {
            mListener = (OnHarvestStartInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void refreshJson() {
        if (mListener != null) {
            mListener.onHarvestStartInteraction(createJsonFromMap());
        }
    }

    @Override
    public void clear() {
        setDateMap.clear();
        if (mListener != null) {
            mListener.onHarvestStartInteraction(createJsonFromMap());
        }
        ((BaseExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();
    }

    private void loadHashMapfromJson() {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        setDateMap = gson.fromJson(mJsonStr, type);
        if (setDateMap == null) {
            setDateMap = new HashMap<String, String>();
        }
    }

    private String createJsonFromMap() {

        Gson gson = new Gson();
        String json = gson.toJson(setDateMap);
        return json;

    }

    public interface OnHarvestStartInteractionListener {
        // TODO: Update argument type and name
        void onHarvestStartInteraction(String jsonString);
    }
}
