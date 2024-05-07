package com.oasis.todo.adapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.oasis.todo.HomeActivity;
import com.oasis.todo.R;

import java.util.ArrayList;

// Custom ArrayAdapter for tasks
public class TaskAdapter extends ArrayAdapter<Task> {
    TaskAdapter(Activity context, int resource, ArrayList<Task> data) {
        super(context, resource, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TaskHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.list_item_task, parent, false);

            holder = new TaskHolder();
            holder.checkBox = row.findViewById(R.id.taskCheckBox);
            holder.titleTextView = row.findViewById(R.id.taskTitleTextView);
            row.setTag(holder);
        } else {
            holder = (TaskHolder) row.getTag();
        }

        final Task task = getItem(position);
        holder.titleTextView.setText(task.title);
        holder.checkBox.setChecked(task.isChecked);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.isChecked = ((CheckBox) v).isChecked();
                // Handle checkbox state change here
            }
        });

        return row;
    }

    static class TaskHolder {
        CheckBox checkBox;
        TextView titleTextView;
    }
}
