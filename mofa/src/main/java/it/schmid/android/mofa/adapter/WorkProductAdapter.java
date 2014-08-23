package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class WorkProductAdapter<T extends ProductInterface> extends ArrayAdapter<T>  implements Filterable{
	private static final String TAG = "WorkProductAdapter";
	Context context;
	int layoutResourceId;
	List<T> allPesticideData=null;
	List<T> filterPesticideData=null;
	private ModelFilter filter;
	
	public WorkProductAdapter(Context context, int layoutResourceId, List<T> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.allPesticideData = new ArrayList<T>();
		allPesticideData.addAll(data);
		this.filterPesticideData = new ArrayList<T>();
		filterPesticideData.addAll(allPesticideData);
		
	    getFilter();
		DatabaseManager.init(context);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		T p = filterPesticideData.get(position);
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
	private static class PesticideHolder{
		TextView txtPesticide;
    }
	 @Override
	    public Filter getFilter() {
	        if (filter == null){
	          filter  = new ModelFilter();
	        }
	        return filter;
	      }

	private class ModelFilter extends Filter
    {
		@Override
        protected FilterResults performFiltering(CharSequence constraint) {
            
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<T> filteredItems = new ArrayList<T>();
               
                for(int i = 0, l = allPesticideData.size(); i < l; i++)
                {
                    T p = allPesticideData.get(i);
                    if(p.getProductName().toLowerCase().contains(constraint))
                        filteredItems.add(p);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = allPesticideData;
                    result.count = allPesticideData.size();
                }
            }
            return result;
        }
		@SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           
            filterPesticideData = (ArrayList<T>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = filterPesticideData.size(); i < l; i++)
                add(filterPesticideData.get(i));
            notifyDataSetInvalidated();
        }
    }
	
}
