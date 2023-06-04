package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static TaskListAdapter taskListAdapter;
    private static ArrayList<Task> taskArrayList;
    public static int notificationTime;
    public static boolean hidenDone;
    private static EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskArrayList = addSavedTasks();
        addSavedSettings();

        taskListAdapter = new TaskListAdapter(this, taskArrayList);
        ListView listView = findViewById(R.id.localities_list);
        listView.setAdapter(taskListAdapter);

        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        ImageButton addTask = findViewById(R.id.imageButton);
        addTask.setOnClickListener(view -> {
            AddTaskWindow dialogFragment = new AddTaskWindow(taskArrayList, getApplicationContext());
            dialogFragment.show(getSupportFragmentManager(), "show_add_window_dialog");
            taskListAdapter.notifyDataSetChanged();
        });

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString();
                ArrayList<Task> filteredTasks = filterTasksByTitle(searchText);
                taskListAdapter.updateData(filteredTasks);
            }
        });
    }

    private void addSavedSettings() {
        notificationTime = 0;
        hidenDone = false;
        //TODO
    }

    public ArrayList<Task> addSavedTasks() {
        ArrayList<Task> taskList = new ArrayList<>();

        TaskDBHelper dbHelper = new TaskDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "id",
                "title",
                "description",
                "category",
                "newDateTime",
                "createdDateTime",
                "selectedFileUri",
                "isDone"
        };

        if(hidenDone)
        {
            String selection = "isDone = ?";
            String[] selectionArgs = { "0" };
        }

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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String newDateTime = cursor.getString(cursor.getColumnIndexOrThrow("newDateTime"));
            String createdDateTime = cursor.getString(cursor.getColumnIndexOrThrow("createdDateTime"));
            String selectedFileUriString = cursor.getString(cursor.getColumnIndexOrThrow("selectedFileUri"));
            Uri selectedFileUri = (selectedFileUriString != null) ? Uri.parse(selectedFileUriString) : null;
            int isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone"));
            boolean isTaskDone = (isDone == 1);

            Task task = new Task(id, title, description, category , newDateTime, createdDateTime, selectedFileUri, isTaskDone, getApplicationContext(), false);
            taskList.add(task);
        }

        cursor.close();
        db.close();

        return taskList;
    }

    private void deleteDatabase() {
        TaskDBHelper dbHelper = new TaskDBHelper(getApplicationContext());
        dbHelper.close();

        boolean isDatabaseDeleted = getApplicationContext().deleteDatabase("task.db");

        if (isDatabaseDeleted) {
            Toast.makeText(this, "Baza danych została usunięta", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nie udało się usunąć bazy danych", Toast.LENGTH_SHORT).show();
        }

    }

    public static void updateData(Context context) {
        taskArrayList.clear();

        TaskDBHelper dbHelper = new TaskDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "id",
                "title",
                "description",
                "category",
                "newDateTime",
                "createdDateTime",
                "selectedFileUri",
                "isDone"
        };
        Cursor cursor;
        if(hidenDone)
        {
            String selection = "isDone = ?";
            String[] selectionArgs = { "0" };
            cursor = db.query(
                    "tasks",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
        }
        else
        {
            cursor = db.query(
                    "tasks",
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
        
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String newDateTime = cursor.getString(cursor.getColumnIndexOrThrow("newDateTime"));
            String createdDateTime = cursor.getString(cursor.getColumnIndexOrThrow("createdDateTime"));
            String selectedFileUriString = cursor.getString(cursor.getColumnIndexOrThrow("selectedFileUri"));
            Uri selectedFileUri = (selectedFileUriString != null) ? Uri.parse(selectedFileUriString) : null;
            int isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone"));
            boolean isTaskDone = (isDone == 1);

            Task task = new Task(id, title, description, category , newDateTime, createdDateTime, selectedFileUri, isTaskDone, context, false);
            taskArrayList.add(task);
        }

        cursor.close();
        db.close();
        taskListAdapter.notifyDataSetChanged();
    }
    private ArrayList<Task> filterTasksByTitle(String searchText) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        updateData(getApplicationContext());
        for (Task task : taskArrayList) {
            if (task.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    private void updateSearchData()
    {
        if (!searchEditText.getText().toString().isEmpty()) {
            ArrayList<Task> taskList = new ArrayList<>();
            String searchText = searchEditText.getText().toString();
            for (Task task : taskArrayList) {
                if (task.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    taskList.add(task);
                }
            }
            taskArrayList = taskList;
        }
        taskListAdapter.notifyDataSetChanged();
    }

}