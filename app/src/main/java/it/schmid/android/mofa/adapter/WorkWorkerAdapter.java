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

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.PromptDialog;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

public class WorkWorkerAdapter extends ArrayAdapter<WorkWorker> {
    private static final String TAG = "WorkWorkerAdapter";
    Context context;
    int layoutResourceId;
    List<WorkWorker> data = null;

    public WorkWorkerAdapter(Context context, int layoutResourceId, List<WorkWorker> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        if (data.size() > 0) { //check if we have worker
            MofaApplication mofaApplication = MofaApplication.getInstance();
            mofaApplication.putGlobalVariable("worker", "valid");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WorkerHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new WorkerHolder();
            holder.txtWorker = row.findViewById(R.id.workworkerLabel);
            holder.delIcon = row.findViewById(R.id.delete_worker_icon);
            row.setTag(holder);
        } else {
            holder = (WorkerHolder) row.getTag();
        }
        final WorkWorker workWorker = data.get(position);
        Worker worker = DatabaseManager.getInstance().getWorkerWithId(workWorker.getWorker().getId());
        String strWorker = worker.getFirstName() + " " + worker.getLastname() + " " + workWorker.getHours();
        holder.txtWorker.setText(strWorker);
        holder.txtWorker.setClickable(true);
        holder.txtWorker.setOnClickListener(new OnClickListener() { //handling the change of hours by clicking on the list

            public void onClick(View v) {

                PromptDialog dlg = new PromptDialog(context, R.string.title,
                        R.string.enter_hours, workWorker.getHours()) {
                    @Override
                    public boolean onOkClicked(Double input) {
                        // do something

                        workWorker.setHours(input);
                        DatabaseManager.getInstance().updateWorkWorker(workWorker);
                        notifyDataSetChanged();
                        //	updateState(workId, worker.getId(), input);


                        return true; // true = close dialog

                    }
                };
                dlg.show();


            }
        });

        holder.delIcon.setImageResource(R.drawable.ic_trash_empty);
        holder.delIcon.setClickable(true);
        holder.delIcon.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showYesNoDeleteDialog(workWorker, position);
            }
        });
        return row;
    }

    static class WorkerHolder {
        TextView txtWorker;
        ImageView delIcon;
    }

    private void showYesNoDeleteDialog(final WorkWorker workWorker, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(R.string.dialogdeletetitel);
        builder.setMessage(R.string.dialogdeletemsg);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Deleting the entry
                if (data.size() == 1) { //last one setting the validity to null
                    MofaApplication mofaApplication = MofaApplication.getInstance();
                    mofaApplication.putGlobalVariable("worker", "null");
                }
                DatabaseManager.getInstance().deleteWorkWorker(workWorker);
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

}
