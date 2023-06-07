package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Task {
    private long taskId;
    private String title;
    private String category;
    private String description;
    private Context context;
    private String notificationDateTime;
    private String createdDateTime;
    private Uri selectedFileUri;
    private boolean isDone;
    private boolean isNotification;

    public Task(long id, String title, String description, String category, String notificationDateTime, String createdDateTime, Uri selectedFileUri, boolean isDone , boolean isNotification, Context context, boolean save) {
        this.taskId = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.context = context;
        this.notificationDateTime = notificationDateTime;
        this.createdDateTime = createdDateTime;
        this.selectedFileUri = selectedFileUri;
        this.isDone = isDone;
        this.isNotification = isNotification;
        if(save)saveTask();

    }

    public void saveTask() {
        TaskDBHelper dbHelper = new TaskDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("category", category);
        values.put("newDateTime", notificationDateTime);
        values.put("createdDateTime", createdDateTime);

        if (isDone())
            values.put("isDone", 1);
        else
            values.put("isDone", 0);

        if (selectedFileUri != null) {
            values.put("selectedFileUri", selectedFileUri.toString()); //todo uri
        }

        if (taskId > 0) {
            String[] whereArgs = {String.valueOf(taskId)};
            db.update("tasks", values, "id = ?", whereArgs);
        } else {
            long insertedId = db.insert("tasks", null, values);
            setTaskId(insertedId);
        }

        db.close();
    }


    public long getTaskId() {
        return taskId;
    }
    public void setTaskId(long taskId) {
        this.taskId = taskId;
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

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;

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

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }
}
