package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
                "notificationDateTime INTEGER," +
                "createdDateTime TEXT," +
                "selectedFileUri TEXT," +
                "isDone INTEGER DEFAULT 0," +
                "isNotification INTEGER DEFAULT 1," +
                "notificationId INTEGER DEFAULT 0" +
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
        query += " ORDER BY notificationDateTime";

        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                long notificationDateTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("notificationDateTime"));
                String createdDateTime = cursor.getString(cursor.getColumnIndexOrThrow("createdDateTime"));
                String selectedFileUriString = cursor.getString(cursor.getColumnIndexOrThrow("selectedFileUri"));
                Uri selectedFileUri = (selectedFileUriString != null) ? Uri.parse(selectedFileUriString) : null;
                int isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone"));
                int isNotification = cursor.getInt(cursor.getColumnIndexOrThrow("isNotification"));
                int notificationId = cursor.getInt(cursor.getColumnIndexOrThrow("notificationId"));
                boolean isTaskDone = (isDone == 1);
                boolean isTaskNotification = (isNotification == 1);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
                String notificationDateTime = dateFormat.format(notificationDateTimeMillis);

                Task task = new Task(id, title, description, category, notificationDateTime.toString(), createdDateTime, selectedFileUri, isTaskDone, isTaskNotification, notificationId, context, false);
                taskList.add(task);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    public void updateTaskById(Long taskId, String newTitle, String newDescription, String newCategory, String notificationDateTime, Uri newSelectedFileUri, boolean newIsDone, boolean newIsNotification, int notificationId) {
        int putIntDone = 0;
        int putIntNotification = 0;
        if(newIsDone) putIntDone = 1;
        if(newIsNotification) putIntNotification = 1;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("description", newDescription);
        values.put("category", newCategory);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
        java.util.Date date = null;
        try {
            date = dateFormat.parse(notificationDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        values.put("notificationDateTime", date.getTime());

        if(newSelectedFileUri!=null) {
            values.put("selectedFileUri", newSelectedFileUri.toString());
        }

        values.put("isDone", putIntDone);
        values.put("isNotification", putIntNotification);
        values.put("notificationId", notificationId);

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(taskId)};

        db.update("tasks", values, whereClause, whereArgs);

        db.close();
    }

    public void deleteTaskById(long taskId) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(taskId)};
        db.delete("tasks", whereClause, whereArgs);
        db.close();
    }

    public Task getTaskById(long taskId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "id",
                "title",
                "description",
                "category",
                "notificationDateTime",
                "createdDateTime",
                "selectedFileUri",
                "isDone",
                "isNotification",
                "notificationId"
        };
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(taskId)};
        Cursor cursor = db.query(
                "tasks",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Task task = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            long notificationDateTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("notificationDateTime"));
            String createdDateTime = cursor.getString(cursor.getColumnIndexOrThrow("createdDateTime"));
            String selectedFileUriString = cursor.getString(cursor.getColumnIndexOrThrow("selectedFileUri"));
            Uri selectedFileUri = (selectedFileUriString != null) ? Uri.parse(selectedFileUriString) : null;
            int isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone"));
            int isNotification = cursor.getInt(cursor.getColumnIndexOrThrow("isNotification"));
            int notificationId = cursor.getInt(cursor.getColumnIndexOrThrow("notificationId"));
            boolean isTaskDone = (isDone == 1);
            boolean isTaskNotification = (isNotification == 1);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
            String notificationDateTime = dateFormat.format(notificationDateTimeMillis);

            task = new Task(id, title, description, category, notificationDateTime, createdDateTime, selectedFileUri, isTaskDone, isTaskNotification, notificationId, context, false);
        }

        cursor.close();
        db.close();
        return task;
    }



}
