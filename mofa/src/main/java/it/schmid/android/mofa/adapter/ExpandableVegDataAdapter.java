package it.schmid.android.mofa.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import it.schmid.android.mofa.DatePickerDialogFragment;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.WorkEditWorkFragment;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;

/**
 * Created by schmida on 08.10.16.
 */

public class ExpandableVegDataAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Land> lands;
    private HashMap<Land,List<VQuarter>> landMap;
    private HashMap<String,String>dateMap;
    private Integer curId;
    private List<Integer> vqIdsFromLand= new ArrayList<Integer>();
    private VegDataListener mListener;

    public interface VegDataListener{
        void refreshJson();
    }
    public ExpandableVegDataAdapter(Context context, List<Land> lands, HashMap<Land,List<VQuarter>>landMap,HashMap<String,String>dateMap,VegDataListener mListener){
        this.context = context;
        this.lands = lands;
        this.landMap = landMap;
        this.dateMap = dateMap;
        this.mListener = mListener;
    }

    @Override
    public int getGroupCount() {
        return this.lands.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.landMap.get(this.lands.get(groupPosition))
                .size();
    }
    private List<Integer> getVQuartersIDFromLand(int groupPosition) {
        List<Integer> vqIdList = new ArrayList<Integer>();
        List<VQuarter> vquarters = this.landMap.get(this.lands.get(groupPosition));
        for (VQuarter vq : vquarters){
            vqIdList.add(vq.getId());
        }
        return vqIdList;
    }
    @Override
    public Object getGroup(int groupPosition) {
        return this.lands.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.landMap.get(this.lands.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Land land = (Land) getGroup(groupPosition);
        final String landDesc = land.getName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.veg_vquarter_list_header, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.textland);

        listTitleTextView.setText(landDesc);
        //always expanded group view
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vqIdsFromLand = getVQuartersIDFromLand(groupPosition);
                Calendar calender = Calendar.getInstance();
                Bundle args = new Bundle();
                args.putInt("year", calender.get(Calendar.YEAR));
                args.putInt("month", calender.get(Calendar.MONTH));
                args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                showDatePicker(args);

            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final VQuarter vQuarter = (VQuarter) getChild(groupPosition, childPosition);
        final String vQuarterDesc = vQuarter.getVariety() + " " + vQuarter.getPlantYear().toString();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.veg_vquarter_list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.textvquarter);
        final TextView expandedListDateTextView = (TextView) convertView.findViewById(R.id.textdate);
        final ImageView expandedClearContentImage = (ImageView) convertView.findViewById(R.id.clearcontent);
        if (dateMap.containsKey(vQuarter.getId().toString())) {
            //show date
            expandedListDateTextView.setVisibility(View.VISIBLE);
            expandedListDateTextView.setText(dateMap.get(vQuarter.getId().toString()));
            expandedClearContentImage.setVisibility(View.VISIBLE);
        }else{
            //no date defined for this vquarter
            expandedListDateTextView.setVisibility(View.GONE);
            expandedClearContentImage.setVisibility(View.GONE);
        }
        expandedClearContentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateMap.remove(vQuarter.getId().toString());
                notifyDataSetChanged();
                mListener.refreshJson();
                Log.d("Adapter","deleting date from "+ vQuarter.getId());
            }
        });
        expandedListTextView.setText(vQuarterDesc);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calender = Calendar.getInstance();
                Bundle args = new Bundle();
                //Log.d("Adapter","inside convertview click"+ vQuarter.getId());
                curId = vQuarter.getId();
                vqIdsFromLand.add(curId);
                if (dateMap.containsKey(curId.toString())){ //preselecting the day in datepicker
                    args.putInt("year", calender.get(Calendar.YEAR));
                    String curDate = dateMap.get(vQuarter.getId().toString());
                    String str[] = curDate.split("\\."); //escaping the dot, otherwise error
                    int day = Integer.parseInt(str[0]);
                    int month = Integer.parseInt(str[1])-1;
                    args.putInt("month", month);
                    args.putInt("day", day);
                }else{ // current date in datepicker


                    args.putInt("year", calender.get(Calendar.YEAR));
                    args.putInt("month", calender.get(Calendar.MONTH));
                    args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                }
                showDatePicker(args);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
                //Log.d ("AdapterCallback", "setting date on " + curId);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, monthOfYear, dayOfMonth);
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

                    for(Integer id: vqIdsFromLand) {
                        dateMap.put(id.toString(), sdf.format(date));
                    }
                vqIdsFromLand.clear();
                //dateMap.put(curId.toString(), sdf.format(date));
                notifyDataSetChanged();
                mListener.refreshJson();

        }
    };


    private void showDatePicker(Bundle args) {
        DatePickerDialogFragment date = new DatePickerDialogFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();

        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(((FragmentActivity)context).getSupportFragmentManager() , "Date Picker");
    }
}
