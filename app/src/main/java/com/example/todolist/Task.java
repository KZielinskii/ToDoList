package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Task {
    private String title;
    private String description;
    private Context context;
    private String newDateTime;
    private String createdDateTime;
    private Uri selectedFileUri;

    public Task(String title, String description, String newDateTime, String createdDateTime, Uri selectedFileUri, Context context, boolean save) {
        this.title = title;
        this.description = description;
        this.context = context;
        this.newDateTime = newDateTime;
        this.createdDateTime = createdDateTime;
        this.selectedFileUri = selectedFileUri;
        if(save)saveTask();
    }

    private void saveTask() {
        TaskDBHelper dbHelper = new TaskDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("newDateTime", newDateTime);
        values.put("createdDateTime", createdDateTime);
        if (selectedFileUri != null) {
            values.put("selectedFileUri", selectedFileUri.toString());
        }

        db.insert("tasks", null, values);
        db.close();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getNewDateTime() {
        return newDateTime;
    }

    public void setNewDateTime(String newDateTime) {
        this.newDateTime = newDateTime;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Uri getSelectedFileUri() {
        return selectedFileUri;
    }

    public void setSelectedFileUri(Uri selectedFileUri) {
        this.selectedFileUri = selectedFileUri;
    }
}
