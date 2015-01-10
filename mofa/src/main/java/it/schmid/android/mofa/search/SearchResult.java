package it.schmid.android.mofa.search;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseHelper;


import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.db.SearchHashMapLoader;
import it.schmid.android.mofa.model.VQuarter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResult#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResult extends SherlockFragment implements LoaderManager.LoaderCallbacks<HashMap<Integer,List<String>>> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mTitle;
    private Integer[] vquartersId;
    // The Loader's id (this id is specific to the ListFragment's LoaderManager)
    private static final int LOADER_ID = 1;
    private GetVQListener mListener;
    private ArrayList<Integer> vqIds = new ArrayList<Integer>();
    private ExpandableListView resultListView;
    private ExpandableSearchResultAdapter adapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment SearchResult.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResult newInstance(int param1) {
        SearchResult fragment = new SearchResult();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    public SearchResult() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getResources().getString(getArguments().getInt(ARG_PARAM1));

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener !=null){
            vqIds=mListener.getVQList();
        }
        View v = inflater.inflate(R.layout.fragment_search_result,container,false);
        resultListView =(ExpandableListView) v.findViewById(R.id.resultlistview);
        adapter = new ExpandableSearchResultAdapter(getActivity(),vqIds);
        resultListView.setAdapter(adapter);
        Button btnClose = (Button)v.findViewById(R.id.searchclose_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener !=null){

                    mListener.closeActivity();
                }
            }
        });
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        return v;



    }

    @Override
    public Loader<HashMap<Integer,List<String>>> onCreateLoader(int id, Bundle args) {
       return new SearchHashMapLoader(getActivity(),vqIds);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<Integer, List<String>>> loader, HashMap<Integer, List<String>> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<HashMap<Integer, List<String>>> loader) {
        adapter.setData(null);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GetVQListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GetVQListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface GetVQListener {
        // TODO: Update argument type and name
        public ArrayList<Integer> getVQList();
        public void closeActivity();
    }

    public static class ExpandableSearchResultAdapter extends BaseExpandableListAdapter {
        private static final String TAG = "ExpandableSearchResultAdapter";
        Context context;
        private List<Integer> headerList;
        private HashMap<Integer,List<String>> resultMap = new HashMap<Integer, List<String>>();
        public ExpandableSearchResultAdapter(Context context, List<Integer> headerList){
            this.context = context;
            this.headerList=headerList;
        }
        public void setData(HashMap<Integer,List<String>> data){
            Log.d(TAG, "setData is called");
            if (resultMap != null) {
                resultMap.clear();
            } else {
                resultMap = new HashMap<Integer, List<String>>();
            }
            if (data != null) {
                Log.d(TAG, "setData is called - data not null");
                resultMap=data;
                notifyDataSetChanged();
            }

        }
        @Override
        public int getGroupCount() {
            return this.headerList.size();
        }

        @Override
       public int getChildrenCount(int groupPosition) {
            return this.resultMap.get(this.headerList.get(groupPosition)).size();
            }

        @Override
        public Object getGroup(int groupPosition) {
            return this.headerList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.resultMap.get(this.headerList.get(groupPosition))
                    .get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition){
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            int varId = (Integer) getGroup(groupPosition);
            VQuarter vQ = DatabaseManager.getInstance().getVQuarterWithId(varId);
            String headerTitle = vQ.getLand().getName() + ", " + vQ.getVariety() + ", " + vQ.getPlantYear();
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.search_group_row, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.grouptext);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.search_child_row, null);
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.childtext);
            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }
    }

}
