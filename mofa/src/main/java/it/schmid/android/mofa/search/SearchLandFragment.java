package it.schmid.android.mofa.search;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.ArrayList;
import java.util.Arrays;
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
        adapter = new ExpandableSearchLandAdapter(getActivity(),landList);
        landListView.setAdapter(adapter);
        TextView txtTitle = (TextView)v.findViewById(R.id.searchtitle);
        txtTitle.setText(mTitle);
        Button btnSearch = (Button)v.findViewById(R.id.landsearch_btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener !=null){
                    mListener.onLandFragmentInteraction(adapter.getChildCheckState());
                }
            }
        });
        return v;
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
        public void onLandFragmentInteraction(HashMap<Integer,ArrayList<Integer>> selElements);
    }

    public static class ExpandableSearchLandAdapter extends BaseExpandableListAdapter{
        private final String TAG="ExpandableSearchAdapter";
        private List<Land> lands;
        private HashMap<Integer,ArrayList<Integer>> childCheckState;


        private Context mContext;

       // private LayoutInflater inflater;
        public ExpandableSearchLandAdapter(Context context,List<Land>lands){
            this.lands = lands;
            childCheckState = new HashMap<Integer, ArrayList<Integer>>();

            mContext=context;
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
        public View getChildView(final int groupPosition, int childPosition,
                                 boolean isLastChild, final View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            final int mGroupPosition= groupPosition;
            final int mChildPosition= childPosition;
            ArrayList<Integer> selChilds = new ArrayList<Integer>();
            View v = convertView;
            final VQuarter vquarter = (VQuarter) getChild(groupPosition, childPosition);
            if( v == null ) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            final VQuarter element = (VQuarter) mholder.selected.getTag();
            if (childCheckState.containsKey(groupPosition)) {
                selChilds = (childCheckState.get(groupPosition));
                Log.d(TAG, "Current entries in group  " + groupPosition + "= " + selChilds.toString());
                if (selChilds.contains(element.getId())) {
                    mholder.selected.setChecked(true);
                } else {
                    mholder.selected.setChecked(false);
                }
            }else{
                mholder.selected.setChecked(false);
            }

            mholder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    ArrayList<Integer> hashEntries = new ArrayList<Integer>();
                    if (isChecked){
                        if (childCheckState.containsKey(groupPosition)){
                            hashEntries = (childCheckState.get(groupPosition));
                            hashEntries.add(element.getId());
                            childCheckState.put(groupPosition,hashEntries);
                            Log.d(TAG, "selected vquarters - adding to existing group " + element.getId() + "to group: " + groupPosition);
                        }else{
                            hashEntries.add(element.getId());
                            childCheckState.put(groupPosition,hashEntries);
                            Log.d(TAG, "selected vquarters - creating new group " + element.getId()+ "to group: " + groupPosition);
                        }

                    }else{
                        if (childCheckState.containsKey(groupPosition)){
                            hashEntries = childCheckState.get(groupPosition);
                            if (hashEntries.contains(element.getId())){
                                hashEntries.remove(element.getId());
                                Log.d(TAG, "Removing vquarter" + element.getId()+ "from group with this entries: " + hashEntries.toString());
                            }
                            if (hashEntries.size()==0){
                                childCheckState.remove(groupPosition);
                                Log.d(TAG, "Removing group");
                            }else{
                                childCheckState.put(groupPosition,hashEntries);
                            }
                        }

                    }
                }
            });
            notifyDataSetChanged();
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
            ViewHolderParent holder;
            ArrayList<Integer> selChilds = new ArrayList<Integer>();
            final Land group;
            //   View v = convertView;
            group = (Land) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.land_group_row, parent, false);
                holder = new ViewHolderParent();
                holder.txtLand = (TextView) convertView.findViewById(R.id.landname);
                holder.selectedLand = (CheckBox) convertView.findViewById(R.id.selected_land);
                //Log.d(TAG, "[getGroupView] Selected land:" + group.getName());

                convertView.setTag(holder);
            } else {
                holder = (ViewHolderParent) convertView.getTag();
            }
            holder.txtLand.setText((group.getName()));
            holder.selectedLand.setOnCheckedChangeListener(null);
            if (childCheckState.containsKey(groupPosition)) {
                selChilds = childCheckState.get(groupPosition);
                if (selChilds.size() == getChildrenCount(groupPosition)) {
                    holder.selectedLand.setChecked(true);
                } else {
                    holder.selectedLand.setChecked(false);
                }
            }else{
                holder.selectedLand.setChecked(false);
            }
            holder.selectedLand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                ArrayList<Integer> hashEntries = new ArrayList<Integer>();
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked){
                        for (VQuarter vq: getChild(group)){
                            hashEntries.add(vq.getId());
                        }
                        childCheckState.put(groupPosition,hashEntries);
                        Log.d(TAG, "Adding group with all childs on position " + groupPosition + " elements: " + hashEntries.toString());
                        }else{
                            childCheckState.remove(groupPosition);
                        }
                    }
            });

            notifyDataSetChanged();
            return convertView;


        }

        public HashMap<Integer, ArrayList<Integer>> getChildCheckState() {
            return childCheckState;
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
