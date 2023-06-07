package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class TaskDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public TaskDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "description TEXT," +
                "category TEXT," +
                "newDateTime TEXT," +
                "createdDateTime TEXT," +
                "selectedFileUri TEXT," +
                "isDone INTEGER DEFAULT 0," +
                "isNotification INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<Task> getTasks(boolean hidenDone, String selectedCategory) {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM tasks";
        String[] selectionArgs = null;

        if(hidenDone && selectedCategory.equals(context.getResources().getString(R.string.all)))
        {
            query = "SELECT * FROM tasks WHERE isDone = ?";
            selectionArgs = new String[]{"0"};
        }
        else if(hidenDone)
        {
            query = "SELECT * FROM tasks WHERE category = ? AND isDone = ?";
            selectionArgs = new String[]{selectedCategory, "0"};
        }
        else if(!selectedCategory.equals(context.getResources().getString(R.string.all)))
        {
            query = "SELECT * FROM tasks WHERE category = ?";
            selectionArgs = new String[]{selectedCategory};
        }


        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String notificationDateTime = cursor.getString(cursor.getColumnIndexOrThrow("newDateTime"));
                String createdDateTime = cursor.getString(cursor.getColumnIndexOrThrow("createdDateTime"));
                String selectedFileUriString = cursor.getString(cursor.getColumnIndexOrThrow("selectedFileUri"));
                Uri selectedFileUri = (selectedFileUriString != null) ? Uri.parse(selectedFileUriString) : null;
                int isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone"));
                int isNotification = cursor.getInt(cursor.getColumnIndexOrThrow("isNotification"));
                boolean isTaskDone = (isDone == 1);
                boolean isTaskNotification = (isNotification == 1);

                Task task = new Task(id, title, description, category, notificationDateTime, createdDateTime, selectedFileUri, isTaskDone, isTaskNotification, context, false);
                taskList.add(task);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    public void updateTaskById(Long taskId, String newTitle, String newDescription, String newCategory, String newDateTime, Uri newSelectedFileUri, boolean newIsDone, boolean newIsNotification) {
        int putIntDone = 0;
        int putIntNotification = 0;
        if(newIsDone) putIntDone = 1;
        if(newIsNotification) putIntNotification = 1;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("description", newDescription);
        values.put("category", newCategory);
        values.put("newDateTime", newDateTime);
        if(newSelectedFileUri!=null) {
            values.put("selectedFileUri", newSelectedFileUri.toString()); // todo uri
        }
        values.put("isDone", putIntDone);
        values.put("isNotification", putIntNotification);

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(taskId)};

        db.update("tasks", values, whereClause, whereArgs);

        db.close();
    }



}
