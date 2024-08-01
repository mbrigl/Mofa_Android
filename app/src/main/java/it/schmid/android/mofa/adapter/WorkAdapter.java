package it.schmid.android.mofa.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkWorker;
import it.schmid.android.mofa.model.Worker;

public class WorkAdapter extends ArrayAdapter<Work> {
    private static final String TAG = "ArrayAdapter";
    Context context;
    int layoutResourceId;
    List<Work> data = null;

    public WorkAdapter(Context context, int layoutResourceId, List<Work> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final String DATE_FORMAT = "dd.MM.yyyy";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final View row;
        WorkHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            // setViewHolder(row);
            holder = new WorkHolder();
            holder.imgIcon = convertView.findViewById(R.id.icon);
            holder.txtDate = convertView.findViewById(R.id.dateLabel);
            holder.txtWork = convertView.findViewById(R.id.workLabel);

            holder.delIcon = convertView.findViewById(R.id.delete_icon);
            convertView.setTag(holder);

        } else {
            // row=convertView;
            holder = (WorkHolder) convertView.getTag();
        }
        row = convertView;
        final Work work = data.get(position);
        String myDate = dateFormat.format(work.getDate());
        //  Log.d(TAG,myDate);
        //  Log.d(TAG,work.getDate().toString());
        holder.txtDate.setText(myDate);

        Task task = work.getTask(); // getting the task
        if (task != null) {
            holder.txtWork.setText(task.getTask());
        }
        if (work.getValid()) {
            holder.imgIcon.setImageResource(R.drawable.ic_ok_icon);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_alerts_and_states_warning);
        }

        holder.imgIcon.setClickable(true);

        holder.delIcon.setImageResource(R.drawable.ic_trash_empty);
        holder.delIcon.setClickable(true);
        holder.delIcon.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showYesNoDeleteDialog(work, position);
            }
        });
        return convertView;
    }

    static class WorkHolder {
        ImageView imgIcon;
        TextView txtDate;
        TextView txtWork;
        ImageView delIcon;
    }


    private void showYesNoDeleteDialog(final Work work, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(R.string.dialogdeletetitel);
        builder.setMessage(R.string.dialogdeletemsg);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Deleting the entry
                DatabaseManager.getInstance().deleteCascWork(work);
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

    private String getVquarters(Work work) {
        String txtVquarters = "";
        Boolean first = true;
        try { // get vquarters for current work
            List<VQuarter> selectedQuarters = DatabaseManager.getInstance().lookupVQuarterForWork(work);
            for (VQuarter vq : selectedQuarters) {
                if (first) {
                    txtVquarters = vq.getLand().getName() + " " + vq.getVariety();
                    first = false;
                } else {
                    txtVquarters += ", " + vq.getLand().getName() + " " + vq.getVariety();
                }

            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return txtVquarters;
    }

    private SpannableStringBuilder getWorkers(Work work) {
        SpannableStringBuilder workerBuilder = new SpannableStringBuilder();
        boolean first = true;
        List<WorkWorker> selectedWorkers = DatabaseManager.getInstance().getWorkWorkerByWorkId(work.getId());
        for (WorkWorker w : selectedWorkers) {
            String txtWorkers;
            if (first) {
                first = false;
            } else {
                workerBuilder.append("\n");
            }

            Worker worker = DatabaseManager.getInstance().getWorkerWithId(w.getWorker().getId());
            txtWorkers = worker.getFirstName() + " " + worker.getLastname() + ": " + w.getHours() + " h";
            workerBuilder.append(txtWorkers);
            workerBuilder.setSpan(new BulletSpan(10), workerBuilder.length() - txtWorkers.length(), workerBuilder.length(), 17);


        }

        return workerBuilder;
    }
}
