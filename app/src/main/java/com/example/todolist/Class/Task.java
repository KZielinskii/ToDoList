package com.example.todolist.Class;

import android.content.Context;
import android.net.Uri;

public class Task {
    private String title;
    private String date;
    private Context context;

    public Task(String title, String date, String newDateTime, String createdDateTime, Uri selectedImageUri, Context context)
    {
        this.title = title;
        this.date = date;
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
