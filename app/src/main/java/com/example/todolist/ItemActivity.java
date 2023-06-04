package com.example.todolist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;


public class ItemActivity extends AppCompatActivity {
    private int position;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        position = getIntent().getIntExtra("item_index", -1);
        title = getIntent().getStringExtra("task_title");
        //TODO do zrobienia 2
    }
}