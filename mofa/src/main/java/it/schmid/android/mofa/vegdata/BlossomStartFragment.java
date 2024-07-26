package it.schmid.android.mofa.vegdata;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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


public class BlossomStartFragment extends Fragment implements ExpandableVegDataAdapter.VegDataListener, ClearInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private String mJsonStr;
    private HashMap<Land, List<VQuarter>> landMap;
    private List<Land> lands;
    private HashMap<String, String> setDateMap;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    private OnBlossomStartInteractionListener mListener;

    public BlossomStartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlossomStartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlossomStartFragment newInstance(String param1) {
        BlossomStartFragment fragment = new BlossomStartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void refreshJson() {
        if (mListener != null) {
            mListener.onBlossomStartInteraction(createJsonFromMap());
        }
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

        return inflater.inflate(R.layout.fragment_blossom_start, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);
        expandableListAdapter = new ExpandableVegDataAdapter(getActivity(), lands, landMap, setDateMap, ActivityConstants.BLOSSOMSTART, this);

        expandableListView.setAdapter(expandableListAdapter);


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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBlossomStartInteractionListener) {
            mListener = (OnBlossomStartInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Log.d("onPause ", "current json string " + createJsonFromMap());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.d("onDetach ", "current json string " + createJsonFromMap());

        mListener = null;
    }

    @Override
    public void clear() {
        Log.d("BlossomStartFragment", "Calling clear");
        setDateMap.clear();
        if (mListener != null) {
            mListener.onBlossomStartInteraction(createJsonFromMap());
        }
        ((BaseExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBlossomStartInteractionListener {
        // TODO: Update argument type and name
        void onBlossomStartInteraction(String jsonString);
    }


}
