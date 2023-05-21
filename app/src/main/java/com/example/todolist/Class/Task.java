package com.example.todolist.Class;

import android.content.Context;
import android.net.Uri;

public class Task {
    private String title;
    private String description;
    private Context context;
    private String newDateTime;
    private String createdDateTime;
    private Uri selectedFileUri;

    public Task(String title, String description, String newDateTime, String createdDateTime, Uri selectedFileUri, Context context) {
        this.title = title;
        this.description = description;
        this.context = context;
        this.newDateTime = newDateTime;
        this.createdDateTime = createdDateTime;
        this.selectedFileUri = selectedFileUri;
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
