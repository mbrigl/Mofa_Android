package it.schmid.android.mofa.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;

public class WorkVQuarterAdapter extends ArrayAdapter<VQuarter> {
    private static final String TAG = "WorkVQuarterAdapter";
    Context context;
    String strVquarter;
    int layoutResourceId;
    List<VQuarter> data = null;

    public WorkVQuarterAdapter(Context context, int layoutResourceId, List<VQuarter> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        if (data.size() > 0) {
            MofaApplication mofaApplication = MofaApplication.getInstance();
            mofaApplication.putGlobalVariable("land", "valid");
        } else { //we have to check also the case that we have no land selected
            MofaApplication mofaApplication = MofaApplication.getInstance();
            mofaApplication.putGlobalVariable("land", "null");
            mofaApplication.putGlobalVariable("currStatus", "false");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VquarterHolder holder = null;
        String plantYear = "";
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new VquarterHolder();
            holder.txtVquarter = (TextView) row.findViewById(R.id.workvquarterLabel);
            row.setTag(holder);

        } else {
            holder = (VquarterHolder) row.getTag();
        }
        VQuarter vquarter = data.get(position);
        Land curLand = vquarter.getLand();
        if (vquarter.getPlantYear() != null) { //checking if plantYear==null
            plantYear = vquarter.getPlantYear().toString();
        }
        if (vquarter.getClone() != null) {
            strVquarter = curLand.getName() + ", " + vquarter.getVariety() + "," + plantYear + " " + vquarter.getClone();
        } else {
            strVquarter = curLand.getName() + ", " + vquarter.getVariety() + "," + plantYear;
        }

        holder.txtVquarter.setText(strVquarter);
        return row;
    }

    static class VquarterHolder {
        TextView txtVquarter;

    }
}
