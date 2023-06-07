package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static String selectedCategory;
    public static TaskDBHelper taskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskDBHelper = new TaskDBHelper(getApplicationContext());

        addSavedSettings();
        taskArrayList = taskDBHelper.getTasks(hidenDone, selectedCategory);


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

        EditText searchEditText = findViewById(R.id.searchEditText);
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
                TaskListAdapter.updateData(filteredTasks);
            }
        });
    }

    private void addSavedSettings() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefrences", Context.MODE_PRIVATE);
        notificationTime = sharedPreferences.getInt("savedNotificationTime", 0);
        hidenDone = sharedPreferences.getBoolean("savedHidenDone", false);
        selectedCategory = sharedPreferences.getString("savedSelectedCategory", getResources().getString(R.string.all));
    }

    private ArrayList<Task> filterTasksByTitle(String searchText) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        updateData();
        for (Task task : taskArrayList) {
            if (task.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public static void updateData() {
        taskArrayList = taskDBHelper.getTasks(hidenDone,selectedCategory);
        TaskListAdapter.updateData(taskArrayList);
        taskListAdapter.notifyDataSetChanged();
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

}