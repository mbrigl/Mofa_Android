package it.schmid.android.mofa.search;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.db.SearchHashMapLoader;
import it.schmid.android.mofa.model.VQuarter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResult#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResult extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<Integer, List<String>>> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mTitle;
    private int queryType;
    private int prodId = 0;
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
    public static SearchResult newInstance(int param1, int param2) {
        SearchResult fragment = new SearchResult();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
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
            queryType = getArguments().getInt(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            vqIds = mListener.getVQList();
            if (queryType == ActivityConstants.SEARCH_PEST) {
                prodId = mListener.getProdId();
            }
            if (queryType == ActivityConstants.SEARCH_FERT) {
                prodId = mListener.getProdId();
            }
        }
        View v = inflater.inflate(R.layout.fragment_search_result, container, false);
        resultListView = v.findViewById(R.id.resultlistview);
        adapter = new ExpandableSearchResultAdapter(getActivity(), vqIds);
        resultListView.setAdapter(adapter);
        Button btnClose = v.findViewById(R.id.searchclose_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (mListener != null) {

                    mListener.closeActivity();
                }
            }
        });
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        return v;


    }


    public Loader<HashMap<Integer, List<String>>> onCreateLoader(int id, Bundle args) {
        if (queryType == ActivityConstants.SEARCH_PEST || queryType == ActivityConstants.SEARCH_FERT) {
            return new SearchHashMapLoader(getActivity(), vqIds, queryType, prodId);
        } else {
            return new SearchHashMapLoader(getActivity(), vqIds, queryType);
        }
    }


    public void onLoadFinished(Loader<HashMap<Integer, List<String>>> loader, HashMap<Integer, List<String>> data) {
        adapter.setData(data);
    }


    public void onLoaderReset(Loader<HashMap<Integer, List<String>>> loader) {
        adapter.setData(null);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GetVQListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity
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
        ArrayList<Integer> getVQList();

        int getProdId();

        void closeActivity();
    }

    public static class ExpandableSearchResultAdapter extends BaseExpandableListAdapter {
        private static final String TAG = "ExpandableSearchResultAdapter";
        Context context;
        private final List<Integer> headerList;
        private HashMap<Integer, List<String>> resultMap = new HashMap<Integer, List<String>>();

        public ExpandableSearchResultAdapter(Context context, List<Integer> headerList) {
            this.context = context;
            this.headerList = headerList;
        }

        public void setData(HashMap<Integer, List<String>> data) {
            Log.d(TAG, "setData is called");
            if (resultMap != null) {
                resultMap.clear();
            } else {
                resultMap = new HashMap<Integer, List<String>>();
            }
            if (data != null) {
                Log.d(TAG, "setData is called - data not null");
                resultMap = data;
                notifyDataSetChanged();
            }

        }

        public int getGroupCount() {
            return this.headerList.size();
        }


        public int getChildrenCount(int groupPosition) {
            return this.resultMap.get(this.headerList.get(groupPosition)).size();
        }


        public Object getGroup(int groupPosition) {
            return this.headerList.get(groupPosition);
        }


        public Object getChild(int groupPosition, int childPosition) {
            return this.resultMap.get(this.headerList.get(groupPosition))
                    .get(childPosition);
        }


        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }


        public boolean hasStableIds() {
            return false;
        }


        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            int varId = (Integer) getGroup(groupPosition);
            //  int sizechild = getChildrenCount(groupPosition);
            VQuarter vQ = DatabaseManager.getInstance().getVQuarterWithId(varId);
            String headerTitle = vQ.getLand().getName() + ", " + vQ.getVariety() + ", " + vQ.getPlantYear();
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.search_group_row, null);
            }

            TextView lblListHeader = convertView.findViewById(R.id.grouptext);
            lblListHeader.setText(headerTitle);

            return convertView;
        }


        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.search_child_row, null);
            }

            TextView txtListChild = convertView.findViewById(R.id.childtext);
            txtListChild.setText(childText);
            return convertView;
        }


        public boolean isChildSelectable(int i, int i2) {
            return false;
        }
    }

}
