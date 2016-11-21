package it.schmid.android.mofa.vegdata;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.adapter.ExpandableVegDataAdapter;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnBlossomEndInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlossomEndFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlossomEndFragment extends Fragment implements ExpandableVegDataAdapter.VegDataListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mJsonStr;
    private HashMap<Land,List<VQuarter>> landMap;
    private List<Land> lands;
    private HashMap<String,String> setDateMap;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    private OnBlossomEndInteractionListener mListener;

    public BlossomEndFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment BlossomEndFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlossomEndFragment newInstance(String param1) {
        BlossomEndFragment fragment = new BlossomEndFragment();
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
        lands = ((VegDataActivity)this.getActivity()).getLands();
        landMap = ((VegDataActivity)this.getActivity()).getLandMap();
        loadHashMapfromJson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((VegDataActivity)getActivity()).setFragmentRefreshListener(new VegDataActivity.ResetFragmentListener() {
            @Override
            public void clearFragment() {
                Log.d("Test", "calling refresh");
                setDateMap.clear();
                refreshJson();
                ((BaseExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();

            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blossom_end, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);
        expandableListAdapter = new ExpandableVegDataAdapter(getActivity(),lands,landMap,setDateMap,this);
        expandableListView.setAdapter(expandableListAdapter);

    }

    @Override
    public void refreshJson() {
        if (mListener != null) {
            mListener.onBlossomEndInteraction(createJsonFromMap());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBlossomEndInteractionListener) {
            mListener = (OnBlossomEndInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    private void loadHashMapfromJson(){
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Gson gson = new Gson();
        setDateMap =  gson.fromJson(mJsonStr, type);
        if (setDateMap == null) {
            setDateMap = new HashMap<String,String>();
        }
    }
    private String createJsonFromMap(){
        Gson gson = new Gson();
        String json = gson.toJson(setDateMap);
        return json;

    }
    @Override
    public void onPause() {
        super.onPause();
       // Log.d("onPause ", "current json string " + createJsonFromMap());
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener.onBlossomEndInteraction(createJsonFromMap());
        }
        mListener = null;
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
    public interface OnBlossomEndInteractionListener {
        // TODO: Update argument type and name
        void onBlossomEndInteraction(String jsonString);
    }
}
