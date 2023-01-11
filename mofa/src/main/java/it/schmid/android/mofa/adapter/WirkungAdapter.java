package it.schmid.android.mofa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.Wirkung;

/**
 * Created by schmida on 30.05.17.
 */

public class WirkungAdapter extends BaseAdapter implements SpinnerAdapter {
    private Context context;
    private List<Wirkung> wirkungsList;
    private LayoutInflater inflatr;
    public WirkungAdapter(List<Wirkung> wirkungsList, Context context){
       this.wirkungsList = wirkungsList;
       this.context = context;
       this.inflatr = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
       return wirkungsList.size();
    }

    @Override
    public Object getItem(int position) {
        return wirkungsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public int getPosition (Wirkung w){ // used, getPosition, overriden equals in Task.java
        int i;
        i = wirkungsList.indexOf(w);

        return i;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflatr.inflate(R.layout.spinner_wirkung, null);
        TextView wirkText = (TextView) view.findViewById(R.id.wirkungText);
        TextView perText = (TextView) view.findViewById(R.id.periodeText);
        wirkText.setText(wirkungsList.get(position).getKultur() +", " + wirkungsList.get(position).getGrund());
        perText.setText(wirkungsList.get(position).getEinsatzPeriode());
        return view;

    }
}
