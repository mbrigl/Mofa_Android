package it.schmid.android.mofa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

    private List<Work> data;
    private Context context;
    private OnClickListener onClickListener;

    public class WorkViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIcon;
        private TextView mDate;
        private TextView mTitle;

        public WorkViewHolder(View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.icon);
            mDate = itemView.findViewById(R.id.dateLabel);
            mTitle = itemView.findViewById(R.id.workLabel);
        }
    }

    public WorkAdapter(List<Work> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public WorkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.worklist_row, parent, false);
        return new WorkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WorkViewHolder holder, int position) {
        final String DATE_FORMAT = "dd.MM.yyyy";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        Work work = data.get(position);
        holder.mIcon.setImageResource(work.getValid() ? R.drawable.ic_ok_icon : R.drawable.ic_alerts_and_states_warning);
        holder.mDate.setText(dateFormat.format(work.getDate()));
        holder.mTitle.setText(work.getTask().getTask());

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(work);
            }
        });
    }

    // Setter for the click listener
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // Interface for the click listener
    public interface OnClickListener {
        void onClick(Work work);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int position) {
        // Deleting the entry
        Work item = data.get(position);
        DatabaseManager.getInstance().deleteCascWork(item);
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Work item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Work> getData() {
        return data;
    }
}
