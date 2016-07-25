package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.FruitQuality;
import it.schmid.android.mofa.model.Task;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class QualitySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
	private List<FruitQuality> qualities; 
	private Context context;
	public QualitySpinnerAdapter(List<FruitQuality> qualities, Context context){
		super();
		this.qualities = qualities;
		this.context = context;
	}
	public int getCount() {
		return qualities.size();
	}

	public Object getItem(int position) {
		return qualities.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	public int getPosition (FruitQuality quality){ // used, getPosition, overriden equals in FruitQuality.java
		int i;	
		i = qualities.indexOf(quality); 

		return i;
			
		}
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_dropdown_item, null);
        textView.setText(qualities.get(position).getQuality());
        return textView;
	}
	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context, R.layout.spinner_layout, null);
        textView.setText(qualities.get(position).getQuality());
        return textView;
    }
}
