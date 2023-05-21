package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static TaskListAdapter taskListAdapter;
    private static ArrayList<Task> taskArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskArrayList = addSavedTasks();

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

    public ArrayList<Task> addSavedTasks() {
        ArrayList<Task> taskList = new ArrayList<>();

        TaskDBHelper dbHelper = new TaskDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "title",
                "description",
                "newDateTime",
                "createdDateTime",
                "selectedFileUri"
        };

        Cursor cursor = db.query(
                "tasks",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String newDateTime = cursor.getString(cursor.getColumnIndexOrThrow("newDateTime"));
            String createdDateTime = cursor.getString(cursor.getColumnIndexOrThrow("createdDateTime"));
            String selectedFileUriString = cursor.getString(cursor.getColumnIndexOrThrow("selectedFileUri"));
            Uri selectedFileUri = (selectedFileUriString != null) ? Uri.parse(selectedFileUriString) : null;

            Task task = new Task(title, description, newDateTime, createdDateTime, selectedFileUri, getApplicationContext(), false);
            taskList.add(task);
        }

        cursor.close();
        db.close();

        return taskList;
    }
}