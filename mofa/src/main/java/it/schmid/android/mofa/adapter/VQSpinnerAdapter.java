package it.schmid.android.mofa.adapter;


import it.schmid.android.mofa.model.VQuarter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class VQSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
	private List<VQuarter> vquarters;
	private Context context;
	
	public VQSpinnerAdapter(List<VQuarter> vquarters, Context context) {
		super();
		this.vquarters = vquarters;
		this.context = context;
	}

	public int getCount() {
		return vquarters.size();
	}

	public Object getItem(int position) {
		return vquarters.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	public int getPosition (VQuarter vquarter){ //not used, perhaps used later to preselect the item
		int i; 
		i = this.vquarters.indexOf(vquarter);
		return i;
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
        textView.setText(vquarterString(position));
        return textView;
	}
	
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
        textView.setText(vquarterString(position));
        return textView;
	}

	private String vquarterString(int position){
		VQuarter currElement = vquarters.get(position);
		String vqText = "";
		vqText=currElement.getLand().getName() + ", " + currElement.getVariety() + ", " + currElement.getClone()  + ", " + currElement.getPlantYear();
		return vqText;
	}
 
}
