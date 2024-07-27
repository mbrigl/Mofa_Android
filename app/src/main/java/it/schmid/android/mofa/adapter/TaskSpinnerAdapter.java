package it.schmid.android.mofa.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import it.schmid.android.mofa.R;
import it.schmid.android.mofa.model.Task;

public class TaskSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private final List<Task> tasks;
    private final Context context;

    public TaskSpinnerAdapter(List<Task> tasks, Context context) {
        super();
        this.context = context;
        this.tasks = tasks;

    }

    public int getCount() {
        return tasks.size();

    }

    public Object getItem(int position) {
        return tasks.get(position);
    }

    public long getItemId(int position) {
        return position;

    }

    public int getPosition(Task task) { // used, getPosition, overriden equals in Task.java
        int i;
        i = tasks.indexOf(task);

        return i;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context, R.layout.work_spinner_task_item, null);
        textView.setText(tasks.get(position).getTask());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context, R.layout.spinner_layout, null);
        textView.setText(tasks.get(position).getTask());
        return textView;
    }

}
