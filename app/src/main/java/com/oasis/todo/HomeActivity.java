package com.oasis.todo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oasis.todo.adapter.Task;

import com.oasis.todo.provider.DatabaseProvider;
import com.oasis.todo.provider.LocalProvider;

import java.util.ArrayList;

public class HomeActivity extends Activity {

    private ListView taskListView;
    private Button signOutBtn;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private DatabaseProvider databaseProvider;
    private CheckBox checkBox;
    private TextView titleTextView;
    private View row;
    private TaskAdapter.TaskHolder holder;
    private View v;
    private Task task;
    private LayoutInflater inflater;
    private ViewGroup parent;
    private int position;
    private View convertView;
    // fab
    private FloatingActionButton fabAddTask;
    private LocalProvider localProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        databaseProvider = new DatabaseProvider(this);
        taskListView = findViewById(R.id.taskListView);
        fabAddTask = findViewById(R.id.fab);
        signOutBtn = findViewById(R.id.signOutButton);
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, R.layout.list_item_task, taskList);
        taskListView.setAdapter(taskAdapter);

        // Load tasks from SQLite database
        loadTasks();
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialog();
            }
        });
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localProvider = new LocalProvider(HomeActivity.this);
                localProvider.logout();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showNoteDialog() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_add_task);

        // Set the width and height of the dialog
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // or specify a specific size
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // or specify a specific size
        dialog.getWindow().setAttributes(layoutParams);

        EditText editTextNote = dialog.findViewById(R.id.editTextNote);
        Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = editTextNote.getText().toString().trim();
                if (!note.isEmpty()) {
                    // Add task to database
                    boolean taskId = databaseProvider.insertTask(note);
                    // Add task to list and refresh adapter
                    taskList.add(new Task(note, false));
                    taskAdapter.notifyDataSetChanged();
                    // Close dialog
                    dialog.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, "Please enter a note", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(databaseProvider.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }

    // Custom ArrayAdapter for tasks
    private class TaskAdapter extends ArrayAdapter<Task> {
        TaskAdapter(Activity context, int resource, ArrayList<Task> data) {
            super(context, resource, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final TaskHolder holder;

            if (row == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                row = inflater.inflate(R.layout.list_item_task, parent, false);

                holder = new TaskHolder();
                holder.checkBox = row.findViewById(R.id.taskCheckBox);
                holder.titleTextView = row.findViewById(R.id.taskTitleTextView);
                // add delete button
                holder.deleteButton = row.findViewById(R.id.deleteButton);
                row.setTag(holder);
            } else {
                holder = (TaskHolder) row.getTag();
            }

            final Task task = getItem(position);
            holder.titleTextView.setText(task.title);

            // Set the initial state of the checkbox based on the task object
            holder.checkBox.setChecked(task.isChecked());

            // Set a listener to update the task object when checkbox state changes
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setChecked(isChecked);
                // Update the task in the database
                databaseProvider.updateTaskStatus(task);
                // Update the task in the list
                taskList.set(position, task);
            });

            // add delete button
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isDeleted = databaseProvider.deleteTask(task);
                    if (!isDeleted) {
                        Toast.makeText(HomeActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    } else {
                    taskList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(HomeActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return row;
        }

        class TaskHolder {
            CheckBox checkBox;
            TextView titleTextView;
            // add ImageButton for delete
            ImageView deleteButton;
        }
    }
}
