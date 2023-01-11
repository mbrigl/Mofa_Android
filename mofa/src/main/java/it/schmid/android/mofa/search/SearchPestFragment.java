package it.schmid.android.mofa.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;



import java.util.List;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.R;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.ProductInterface;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link it.schmid.android.mofa.search.SearchPestFragment.OnFragmentPesticideListener}
 * interface.
 */
public class SearchPestFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mTitle;
    private int searchType;
    private List<? extends ProductInterface> prodList;
    private OnFragmentPesticideListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static SearchPestFragment newInstance(int param1,int param2) {
        SearchPestFragment fragment = new SearchPestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2,param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchPestFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitle = getResources().getString(getArguments().getInt(ARG_PARAM1));
            searchType = getArguments().getInt(ARG_PARAM2);

        }
        if (searchType == ActivityConstants.SEARCH_FERT){
            prodList = DatabaseManager.getInstance().getUsedFertilizerList();
        }else {
           prodList = DatabaseManager.getInstance().getUsedPesticideList();
        }

        mAdapter = new PesticideAdapter<ProductInterface>(getActivity(),R.layout.pesticide_row,prodList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchpest, container, false);
        TextView searchTitle = (TextView) view.findViewById(R.id.searchtitle);
        searchTitle.setText(mTitle);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentPesticideListener) activity;
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


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragPestInteraction((ProductInterface)mAdapter.getItem(position),searchType);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    public interface OnFragmentPesticideListener {
        // TODO: Update argument type and name
        public void onFragPestInteraction(ProductInterface product, int searchType);
    }
    public static class PesticideAdapter<T extends ProductInterface> extends ArrayAdapter<T>{
       List<? extends ProductInterface>data;
       Context context;
       int layoutResourceId;
       public PesticideAdapter(Context context,int layoutResourceId, List<? extends ProductInterface> data){
           super(context,layoutResourceId, (List<T>) data);
           this.context= context;
           this.layoutResourceId=layoutResourceId;
           this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final T p = (T) data.get(position);
            PesticideHolder holder = null;
            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new PesticideHolder();
                holder.txtPesticide = (TextView) row.findViewById(R.id.txt_pesticide_item);
                row.setTag(holder);

            }else{
                holder = (PesticideHolder)row.getTag();
            }


            holder.txtPesticide.setText(p.getProductName());
            return row;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public T getItem(int position) {
            return (T)data.get(position);
        }

        private static class PesticideHolder{
            TextView txtPesticide;
        }
    }
}
