package it.schmid.android.mofa.search;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link it.schmid.android.mofa.search.SearchLandFragment.OnLandFragmentListener} interface
 * to handle interaction events.
 * Use the {@link SearchLandFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchLandFragment  extends SherlockFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    ExpandableListView landListView;
    private ExpandableSearchLandAdapter adapter;
    // TODO: Rename and change types of parameters
    private String mTitle;


    private OnLandFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SearchLandFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchLandFragment newInstance(int param1) {
        SearchLandFragment fragment = new SearchLandFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchLandFragment() {
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
        View v = inflater.inflate(R.layout.land_list_search,container,false);
        landListView = (ExpandableListView)v.findViewById(R.id.landlistview);
        List<Land> landList = DatabaseManager.getInstance().getAllLands();
        adapter = new ExpandableSearchLandAdapter(landList);
        TextView txtTitle = (TextView)v.findViewById(R.id.searchtitle);
        txtTitle.setText(mTitle);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLandFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLandFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLandFragmentListener {
        // TODO: Update argument type and name
        public void onLandFragmentInteraction(Uri uri);
    }

    public static class ExpandableSearchLandAdapter extends BaseExpandableListAdapter{
        private List<Land> lands;
        private HashMap<Integer,boolean[]> childCheckState;
        private LayoutInflater inflater;
        public ExpandableSearchLandAdapter(List<Land>lands){
            this.lands = lands;
            childCheckState = new HashMap<Integer, boolean[]>();
        }
        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }
        public Object getChild(int groupPosition, int childPosition) {
            return lands.get(groupPosition).getVQuarters().get(childPosition);
        }
        public List<VQuarter> getChild(Land group) {
            return group.getVQuarters();
        }
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View v = convertView;
            final VQuarter vquarter = (VQuarter) getChild(groupPosition, childPosition);
            if( v == null ) {
                v = inflater.inflate(R.layout.land_child_row, parent, false);
                holder = new ViewHolder();
                holder.txtVar= (TextView) v.findViewById(R.id.variety);
                holder.txtClone= (TextView) v.findViewById(R.id.clone);
                holder.txtPlantYear = (TextView) v.findViewById(R.id.plantyear);
                holder.selected=(CheckBox) v.findViewById(R.id.selected);
                v.setTag(holder);
                holder.selected.setTag(vquarter);
            }else{
                v=convertView;
                ((ViewHolder) v.getTag()).selected.setTag(vquarter);
            }
            ViewHolder mholder = (ViewHolder) v.getTag();
            mholder.txtVar.setText (vquarter.getVariety());
            mholder.txtClone.setText (vquarter.getClone());
            if(vquarter.getPlantYear()!=null){
                mholder.txtPlantYear.setText (vquarter.getPlantYear().toString());
            }
            mholder.selected.setOnCheckedChangeListener(null); //restoring to null,because each call to setchecked calls onCheckChangeListener
            if (childCheckState.containsKey(groupPosition)){

            }
            return v;
        }
        public int getChildrenCount(int groupPosition) {
            return lands.get(groupPosition).getVQuarters().size();
        }

        public Object getGroup(int groupPosition) {
            return groupPosition < getGroupCount() ? lands.get(groupPosition) : -1;

        }

        public int getGroupCount() {
            return lands.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final ViewHolderParent holder;
            final Land group;
            View v = convertView;
            group = (Land) getGroup(groupPosition);
            if( v == null ){
                v = inflater.inflate(R.layout.land_group_row, parent, false);
                holder = new ViewHolderParent();
                holder.txtLand= (TextView) v.findViewById(R.id.landname);
               // holder.selectedLand = (CheckBox) v.findViewById(R.id.selected_land);
                //Log.d(TAG, "[getGroupView] Selected land:" + group.getName());

                v.setTag(holder);
            }else{
                holder = (ViewHolderParent) v.getTag();
            }
            holder.txtLand.setText((group.getName()));
            return v;
        }
        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        static class ViewHolder {
            protected TextView txtVar;
            protected TextView txtClone;
            protected TextView txtPlantYear;
            protected CheckBox selected;
        }

        static class ViewHolderParent {
            protected TextView txtLand;
            protected CheckBox selectedLand;
        }
    }

}
