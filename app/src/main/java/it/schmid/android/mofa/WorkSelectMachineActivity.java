package it.schmid.android.mofa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkMachine;

public class WorkSelectMachineActivity extends DashboardActivity {
    private static final String TAG = "WorkSelectMachineActivity";
    private int workId;
    SparseArray<Double> selectedMachines = new SparseArray<Double>();
    private Double proposedHour = 8.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_list);
        ListView listView = findViewById(R.id.machinelistview);
        Button closeButton = findViewById(R.id.machineclose_btn);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey("Work_ID")) {
            workId = bundle.getInt("Work_ID");
            //  Log.d(TAG, "Current workid: " + workId);
            List<WorkMachine> listMachines = DatabaseManager.getInstance().getWorkMachineByWorkId(workId);
            for (WorkMachine m : listMachines) {
                selectedMachines.put(m.getMachine().getId(), m.getHours()); //put the current selected machine in a sparseArray
            }
        }
        List<Machine> machineList = DatabaseManager.getInstance().getAllMachines();
        final ArrayAdapter<Machine> adapter = new SelectMachineAdapter(this, R.layout.machine_row, machineList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Machine machine = adapter.getItem(position);
                //  Log.d(TAG, "Current machine with id: " + machine.getId());
                Double hours = selectedMachines.get(machine.getId());
                if (hours != null) { //existing entry
                    proposedHour = hours;
                } else {
                    proposedHour = MofaApplication.getDefaultHour();
                }
                PromptDialog dlg = new PromptDialog(WorkSelectMachineActivity.this, R.string.title,
                        R.string.enter_hours, proposedHour) {
                    @Override
                    public boolean onOkClicked(Double input) {
                        // do something
                        //   Log.d(TAG, "showDialog: " + input);

                        addMachineToArray(machine.getId(), input);
                        adapter.notifyDataSetChanged();
                        MofaApplication.setDefaultHour(input);
                        proposedHour = MofaApplication.getDefaultHour(); //setting the local variable to the new value

                        return true; // true = close dialog

                    }
                };
                dlg.show();


            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveState(int workid, int machineid, Double hours) throws SQLException {
        List<WorkMachine> listWorkMachine = DatabaseManager.getInstance().getWorkMachineByWorkIdAndByMachineId(workid, machineid);
        if (listWorkMachine.size() == 0) {
            // Log.d (TAG, "New Machine");
            WorkMachine w = new WorkMachine();
            Work curWork = DatabaseManager.getInstance().getWorkWithId(workid);
            Machine curMachine = DatabaseManager.getInstance().getMachineWithId(machineid);
            w.setWork(curWork);
            w.setMachine(curMachine);
            w.setHours(hours);
            DatabaseManager.getInstance().addWorkMachine(w);
        } else {
            // Log.d (TAG, "Updating Machine");
            WorkMachine w = listWorkMachine.get(0);
            w.setHours(hours);
            DatabaseManager.getInstance().updateWorkMachine(w);
        }
    }

    private void saveSparseArray(SparseArray<Double> machineArray) {
        int machineId;
        for (int i = 0; i < machineArray.size(); i++) {
            machineId = machineArray.keyAt(i);
            Double hours = machineArray.get(machineId);
            try {
                saveState(workId, machineId, hours);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //writing the values of sparseArray to Database
        saveSparseArray(selectedMachines);
    }

    private class SelectMachineAdapter extends ArrayAdapter<Machine> {
        private static final String TAG = "SelectMachineAdapter";
        private final Context context;
        private final int itemLayout;
        private final List<Machine> machines;


        public SelectMachineAdapter(Context context, int textViewResourceId, List<Machine> machines) {
            super(context, textViewResourceId, machines);
            this.context = context;
            this.itemLayout = textViewResourceId;
            this.machines = machines;


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MachineHolder holderItem;
            if (convertView == null) {
                //inflating the layout
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(itemLayout, parent, false);
                //setting up viewholder
                holderItem = new MachineHolder();
                holderItem.mName = convertView.findViewById(R.id.machinelabel);
                holderItem.mHours = convertView.findViewById(R.id.hourslabel);
                holderItem.mIsSelected = convertView.findViewById(R.id.selected);

                convertView.setTag(holderItem);
            } else {
                holderItem = (MachineHolder) convertView.getTag();
            }
            final Machine machine = machines.get(position);
            holderItem.mName.setText(machine.getName());
            //default case
            holderItem.mIsSelected.setChecked(false);
            holderItem.mHours.setVisibility(View.GONE);
            Double hours;
            hours = selectedMachines.get(machine.getId());
            if (hours != null) { //existing entries, setting the hours and the checkbox
//                Log.d(TAG, "worker " + worker.getLastname() + " hours: " + hours);
                holderItem.mIsSelected.setChecked(true);
                holderItem.mHours.setVisibility(View.VISIBLE);
                holderItem.mHours.setText(hours.toString());
            }
            holderItem.mIsSelected.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    if (holderItem.mIsSelected.isChecked()) { //adding item to sparseArray and setting the textview
                        holderItem.mHours.setVisibility(View.VISIBLE);
                        proposedHour = MofaApplication.getDefaultHour();
                        holderItem.mHours.setText(proposedHour.toString());
                        addMachineToArray(machine.getId(), proposedHour);

                    } else {
                        removeMachineFromArray(machine.getId());
                        holderItem.mHours.setVisibility(View.GONE);
                        Log.d(TAG, "Removing");
                    }
                }
            });
            return convertView;
        }

        private class MachineHolder {
            TextView mName;
            TextView mHours;
            CheckBox mIsSelected;
        }


    }

    private void addMachineToArray(Integer id, Double proposedHour) {
        selectedMachines.put(id, proposedHour); //adding the selected worker to the SparseArray
    }

    private void removeMachineFromArray(Integer id) {
        selectedMachines.delete(id);
        try {
            List<WorkMachine> listWorkMachine = DatabaseManager.getInstance().getWorkMachineByWorkIdAndByMachineId(workId, id);
            if (listWorkMachine.size() > 0) {
                DatabaseManager.getInstance().deleteWorkMachine(listWorkMachine.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
