package it.schmid.android.mofa.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Land;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkVQuarter;

public class ExpandableLandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "ExpandableLandAdapter";
    private final List<Land> lands;

    private final LayoutInflater inflater;
    private final Map<Long, WorkVQuarter> hashSelQuarters;
    private WorkVQuarter mWorkVQuarter;
    private final Work mcurWork;
    private Boolean groupChecked = false;
    private final Boolean[] isSelected;

    public ExpandableLandAdapter(List<Land> lands, Context context, Map<Long, WorkVQuarter> hashSelQuarters, Work curWork) {
        super();
        Integer counter = 0;
        this.lands = lands;
        this.mcurWork = curWork;
        isSelected = new Boolean[lands.size()];
        this.hashSelQuarters = hashSelQuarters;
        this.inflater = LayoutInflater.from(context);
        DatabaseManager.init(context);
        for (Land l : lands) {
            isSelected[counter] = allSubItemsSelected(l);
            counter++;
        }
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
        if (v == null) {
            v = inflater.inflate(R.layout.land_child_row, parent, false);
            holder = new ViewHolder();

            holder.txtVar = v.findViewById(R.id.variety);
            holder.txtClone = v.findViewById(R.id.clone);
            holder.txtPlantYear = v.findViewById(R.id.plantyear);
            holder.selected = v.findViewById(R.id.selected);
            holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    VQuarter element = (VQuarter) holder.selected.getTag();
                    Log.d(TAG, "[getChildView] Selected variety(if): " + element.getVariety() + "," + element.getClone() + ",id:" + element.getId());
                    // First case: we uncheck an existing entry, therefore we have to remove it from DB and Hash-Table
                    if ((!isChecked) && (hashSelQuarters.containsKey(element.getId().longValue()))) { // we uncheck a quarter, which is in the hash -> remove it
                        //	Log.d(TAG, "Selected Quarters Table in HashTable:(Remove it) " + element.getId().longValue());
                        mWorkVQuarter = hashSelQuarters.get(element.getId().longValue());
                        DatabaseManager.getInstance().deleteWorkVquarter(mWorkVQuarter); //removing it from the WorkVQuarter table in the DB
                        hashSelQuarters.remove(element.getId().longValue()); //removing it from the hash-Table too
                    }
                    // Second case: we check a vquarter, which is not in the hash, therefore not in the DB-table too. We have to add it to hash and DB
                    if ((isChecked) && (!hashSelQuarters.containsKey(element.getId().longValue()))) {
                        //	Log.d(TAG, "Selected Quarter not in Hashtable: (Adding it) " + element.getId());
                        mWorkVQuarter = new WorkVQuarter();
                        mWorkVQuarter.setWork(mcurWork);
                        mWorkVQuarter.setVquarter(element);
                        DatabaseManager.getInstance().addWorkVQuarter(mWorkVQuarter);
                        hashSelQuarters.put(element.getId().longValue(), mWorkVQuarter);
                    }
                }

            });
            v.setTag(holder);
            holder.selected.setTag(vquarter);
        } else {
            Log.d(TAG, "Selected variety:(else) " + vquarter.getVariety() + "," + vquarter.getPlantYear() + ",id:" + vquarter.getId());
            //holder = (ViewHolder) v.getTag();
            v = convertView;
            ((ViewHolder) v.getTag()).selected.setTag(vquarter);
        }
        ViewHolder mholder = (ViewHolder) v.getTag();
        mholder.txtVar.setText(vquarter.getVariety());
        mholder.txtClone.setText(vquarter.getClone());
        if (vquarter.getPlantYear() != null) {
            mholder.txtPlantYear.setText(vquarter.getPlantYear().toString());
        }

        Log.d(TAG, "Number of entries in Hashmap:" + hashSelQuarters.size());
        // Log.d(TAG, "Setting variety to true: " + vquarter.getVariety()  +"," + vquarter.getPlantYear() + ",id:" + vquarter.getId());
        //Log.d(TAG, "Setting variety to false: " + vquarter.getVariety()  +"," + vquarter.getPlantYear() + ",id:" + vquarter.getId());
        mholder.selected.setChecked(hashSelQuarters.containsKey(vquarter.getId().longValue()));
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return lands.get(groupPosition).getVQuarters().size();
    }

    public Object getGroup(int groupPosition) {
        return groupPosition < getGroupCount() ? lands.get(groupPosition) : -1;
        //return lands.get(groupPosition);
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
        if (v == null) {
            v = inflater.inflate(R.layout.land_group_row, parent, false);
            holder = new ViewHolderParent();
            holder.txtLand = v.findViewById(R.id.landname);
            holder.selectedLand = v.findViewById(R.id.selected_land);

            v.setTag(holder);

        } else {
            holder = (ViewHolderParent) v.getTag();

        }

        holder.selectedLand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "[getGroupView] oncheckedChanged " + group.getName() + " position: " + groupPosition);
                isSelected[groupPosition] = isChecked;
                //Land l = (Land) buttonView.getTag();
                //Log.d(TAG,"[getGroupView] oncheckedChanged " + l.getName() );
                if (isChecked) {
                    groupChecked = true;

                    for (VQuarter vq : group.getVQuarters()) {
                        if (!hashSelQuarters.containsKey(vq.getId().longValue())) {
                            Log.d(TAG, "[getGroupView] adding vq in land " + group.getName() + ", " + groupPosition + " ," + getGroupId(groupPosition));
                            mWorkVQuarter = new WorkVQuarter();
                            mWorkVQuarter.setWork(mcurWork);
                            mWorkVQuarter.setVquarter(vq);
                            DatabaseManager.getInstance().addWorkVQuarter(mWorkVQuarter);
                            hashSelQuarters.put(vq.getId().longValue(), mWorkVQuarter);


                        }
                    }
                } else {
                    groupChecked = false;
                    Log.d(TAG, "[getGroupView] removing vq from land " + group.getName() + ", " + groupPosition + " ," + getGroupId(groupPosition));
                    for (VQuarter vq : group.getVQuarters()) {
                        if (hashSelQuarters.containsKey(vq.getId().longValue())) {
                            mWorkVQuarter = hashSelQuarters.get(vq.getId().longValue());
                            DatabaseManager.getInstance().deleteWorkVquarter(mWorkVQuarter); //removing it from the WorkVQuarter table in the DB
                            hashSelQuarters.remove(vq.getId().longValue()); //removing it from the hash-Table too


                        }
                    }
                }


            }
        });
        //	ViewHolderParent mholder = (ViewHolderParent) v.getTag();
        holder.txtLand.setText((group.getName()));
        if ((groupChecked) || (allSubItemsSelected(group))) {
            Log.d(TAG, "[getGroupView] - Setting viewholder checkbox " + groupPosition);
            holder.selectedLand.setChecked(isSelected[groupPosition]);
        } else {
            Log.d(TAG, "[getGroupView] - Removing viewholder checkbox " + groupPosition);
            holder.selectedLand.setChecked(isSelected[groupPosition]);
        }
        groupChecked = false;    //resetting the check variable


        notifyDataSetChanged();
        return v;
    }

    private Boolean allSubItemsSelected(Land land) {
        for (VQuarter vq : land.getVQuarters()) {
            if (!hashSelQuarters.containsKey(vq.getId().longValue())) {
                return false;
            }
        }
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
