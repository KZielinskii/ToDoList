package com.example.todolist.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.todolist.Adapter.TaskListAdapter;
import com.example.todolist.Class.Task;
import com.example.todolist.R;
import com.example.todolist.Window.AddTaskWindow;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static TaskListAdapter taskListAdapter;
    private static ArrayList<Task> taskArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskArrayList = new ArrayList<>();
        addSavedTasks();

        taskListAdapter = new TaskListAdapter(this, taskArrayList);
        ListView listView = findViewById(R.id.localities_list);
        listView.setAdapter(taskListAdapter);

        ImageButton addTask = findViewById(R.id.imageButton);
        addTask.setOnClickListener(view -> {
            AddTaskWindow dialogFragment = new AddTaskWindow(taskArrayList, getApplicationContext());
            dialogFragment.show(getSupportFragmentManager(), "show_add_window_dialog");
            taskListAdapter.notifyDataSetChanged();
        });

    }

    private void addSavedTasks() {
    }
}