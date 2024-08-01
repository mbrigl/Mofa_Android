package it.schmid.android.mofa.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.schmid.android.mofa.PromptDialog;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Machine;
import it.schmid.android.mofa.model.WorkMachine;

public class WorkMachineAdapter extends ArrayAdapter<WorkMachine> {
    private static final String TAG = "WorkMachineAdapter";
    Context context;
    int layoutResourceId;
    List<WorkMachine> data = null;

    public WorkMachineAdapter(Context context, int layoutResourceId, List<WorkMachine> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MachineHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new MachineHolder();
            holder.txtMachine = row.findViewById(R.id.workmachineLabel);
            holder.delIcon = row.findViewById(R.id.delete_machine_icon);
            row.setTag(holder);
        } else {
            holder = (MachineHolder) row.getTag();
        }
        final WorkMachine workMachine = data.get(position);
        Machine machine = DatabaseManager.getInstance().getMachineWithId(workMachine.getMachine().getId());
        String strMachine = machine.getName() + " " + workMachine.getHours();
        holder.txtMachine.setText(strMachine);
        holder.delIcon.setImageResource(R.drawable.ic_trash_empty);
        holder.delIcon.setClickable(true);
        holder.txtMachine.setClickable(true);
        holder.txtMachine.setOnClickListener(new OnClickListener() { //handling the change of hours by clicking on the list

            public void onClick(View v) {

                PromptDialog dlg = new PromptDialog(context, R.string.title,
                        R.string.enter_hours, workMachine.getHours()) {
                    @Override
                    public boolean onOkClicked(Double input) {
                        // do something

                        workMachine.setHours(input);
                        DatabaseManager.getInstance().updateWorkMachine(workMachine);
                        notifyDataSetChanged();


                        return true; // true = close dialog

                    }
                };
                dlg.show();


            }
        });
        holder.delIcon.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showYesNoDeleteDialog(workMachine, position);

            }
        });
        return row;
    }

    private void showYesNoDeleteDialog(final WorkMachine workMachine, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(R.string.dialogdeletetitel);
        builder.setMessage(R.string.dialogdeletemsg);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Deleting the entry
                DatabaseManager.getInstance().deleteWorkMachine(workMachine);
                data.remove(data.get(position)); //removing the item form the list
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do Nothing
                dialog.dismiss();
            }

        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    static class MachineHolder {
        TextView txtMachine;
        ImageView delIcon;
    }
}
