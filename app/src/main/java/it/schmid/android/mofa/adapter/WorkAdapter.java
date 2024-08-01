package it.schmid.android.mofa.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
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
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.SoilFertilizer;
import it.schmid.android.mofa.model.SprayFertilizer;
import it.schmid.android.mofa.model.SprayPesticide;
import it.schmid.android.mofa.model.Spraying;
import it.schmid.android.mofa.model.Task;
import it.schmid.android.mofa.model.VQuarter;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.model.WorkFertilizer;
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
        DatabaseManager.init(context);
    }

    public WorkAdapter(Context context, int layoutResourceId) { //constructor for the loader
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        DatabaseManager.init(context);
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

    private SpannableStringBuilder getFertInfos(Work work) {
        SpannableStringBuilder fertBuilder = new SpannableStringBuilder();
        if (DatabaseManager.getInstance().getWorkFertilizerByWorkId(work.getId()).size() != 0) {
            boolean first = true;
            List<WorkFertilizer> wF = DatabaseManager.getInstance().getWorkFertilizerByWorkId(work.getId());
            for (WorkFertilizer f : wF) {
                if (first) {
                    first = false;
                } else {
                    fertBuilder.append("\n");
                }
                SoilFertilizer soilFertilizer = DatabaseManager.getInstance().getSoilFertilizerWithId(f.getSoilFertilizer().getId());
                String fertStr = soilFertilizer.getProductName();
                fertStr += " (" + f.getAmount() + " kg)";
                fertBuilder.append(fertStr);
                fertBuilder.setSpan(new BulletSpan(10), fertBuilder.length() - fertStr.length(), fertBuilder.length(), 17);

            }
        }
        return fertBuilder;
    }

    private SpannableStringBuilder getSprayInfos(Work work) {

        SpannableStringBuilder sprayBuilder = new SpannableStringBuilder();


        //getting the spray pesticides, if a spray work
        if (DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).size() != 0) { //its a spraying work
            Spraying sprayInfo = DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).get(0);
            //start formatting output using SpannableStringBuilder
            String str1 = context.getResources().getString(R.string.concentrationcaption) + ": ";
            sprayBuilder.append(str1);
            String str2 = sprayInfo.getConcentration() + " x ";
            sprayBuilder.append(str2);
            sprayBuilder.setSpan(new StyleSpan(1), sprayBuilder.length() - str2.length(), sprayBuilder.length(), 17);
            str1 = context.getResources().getString(R.string.sumwatercaption) + ": ";
            sprayBuilder.append(str1);
            str2 = sprayInfo.getWateramount().toString();
            sprayBuilder.append(str2);
            sprayBuilder.setSpan(new StyleSpan(1), sprayBuilder.length() - str2.length(), sprayBuilder.length(), 17);
            int msprayId;

            msprayId = DatabaseManager.getInstance().getSprayingByWorkId(work.getId()).get(0).getId();
            List<SprayPesticide> selectedPesticides = DatabaseManager.getInstance().getSprayPesticideBySprayId(msprayId);
            for (SprayPesticide sp : selectedPesticides) {
                Pesticide pesticide = DatabaseManager.getInstance().getPesticideWithId(sp.getPesticide().getId());
                String pestStr = pesticide.getProductName();
                pestStr += "(" + sp.getDose() + " )";
                sprayBuilder.append("\n");
                sprayBuilder.append(pestStr);
                sprayBuilder.setSpan(new BulletSpan(10), sprayBuilder.length() - pestStr.length(), sprayBuilder.length(), 17);


            }
            List<SprayFertilizer> selectedFertilizers = DatabaseManager.getInstance().getSprayFertilizerBySprayId(msprayId);
            for (SprayFertilizer sf : selectedFertilizers) {
                Fertilizer fertilizer = DatabaseManager.getInstance().getFertilizerWithId(sf.getFertilizer().getId());
                String fertStr = fertilizer.getProductName();
                fertStr += " (" + sf.getDose() + " )";
                sprayBuilder.append("\n");
                sprayBuilder.append(fertStr);
                sprayBuilder.setSpan(new BulletSpan(10), sprayBuilder.length() - fertStr.length(), sprayBuilder.length(), 17);

            }
        }
        return sprayBuilder;
    }
}
