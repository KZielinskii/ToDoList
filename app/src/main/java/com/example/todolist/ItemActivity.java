package com.example.todolist;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ItemActivity extends AppCompatActivity {
    private int position;
    private String title;
    private String category;
    private String description;
    private String notificationDateTime;
    private String createdDateTime;
    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        readIntent();
        setAllStrings();
        setAttachmentPreview();
    }

    private void readIntent()
    {
        position = getIntent().getIntExtra("item_index", -1);
        title = getIntent().getStringExtra("task_title");
        description = getIntent().getStringExtra("task_description");
        category = getIntent().getStringExtra("task_category");
        notificationDateTime = getIntent().getStringExtra("task_notification_date");
        createdDateTime = getIntent().getStringExtra("task_create_date");
        selectedFileUri = getIntent().getParcelableExtra("task_file");
    }

    private void setAllStrings()
    {
        TextView textTitle = findViewById(R.id.editTitle);
        TextView textCategory = findViewById(R.id.editCategory);
        TextView textDescription = findViewById(R.id.editDescription);
        TextView textNotificationDate = findViewById(R.id.editDate);
        TextView textCreateDate = findViewById(R.id.editDateCreate);

        textTitle.setText(title);
        textCategory.setText(category);
        textDescription.setText(description);
        textNotificationDate.setText(notificationDateTime);
        textCreateDate.setText(createdDateTime);
    }
    private void setAttachmentPreview()
    {
        ImageView fileView = findViewById(R.id.attachmentPreview);
        if(selectedFileUri == null)
        {
            fileView.setImageResource(R.drawable.baseline_file_download_off_64);
        } else {
            fileView.setImageResource(R.drawable.baseline_file_download_64);
        }
    }
}