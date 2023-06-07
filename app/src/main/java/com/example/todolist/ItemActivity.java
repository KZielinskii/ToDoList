package com.example.todolist;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ItemActivity extends AppCompatActivity {
    private int position;
    private Long id;
    private String title;
    private String category;
    private String description;
    private String notificationDateTime;
    private String createdDateTime;
    private Uri selectedFileUri;
    private boolean isDone;
    private boolean isOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        readIntent();
        setAllStrings();
        setAttachmentPreview();
        setChechBox();
    }

    private void readIntent()
    {
        position = getIntent().getIntExtra("item_index", -1);
        id = getIntent().getLongExtra("task_id", -1);
        title = getIntent().getStringExtra("task_title");
        description = getIntent().getStringExtra("task_description");
        category = getIntent().getStringExtra("task_category");
        notificationDateTime = getIntent().getStringExtra("task_notification_date");
        createdDateTime = getIntent().getStringExtra("task_create_date");
        selectedFileUri = getIntent().getParcelableExtra("task_file");
        isDone = getIntent().getBooleanExtra("task_isDone", true);
        isOn = getIntent().getBooleanExtra("task_isOn", true);
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
    private void setChechBox() {
        CheckBox checkBox = findViewById(R.id.checkbox);
        checkBox.setChecked(isOn);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = checkBox.isChecked();
                TaskDBHelper taskDBHelper = MainActivity.taskDBHelper;
                taskDBHelper.updateTaskById(id, title, description, category, notificationDateTime, selectedFileUri, isDone, isChecked);
                MainActivity.updateData();
            }
        });
    }
}