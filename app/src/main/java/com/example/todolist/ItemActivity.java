package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        readIntent();
        setAllStrings();
        setAttachmentPreview();
        setChechBox();
        setButtons();
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
        notificationId = getIntent().getIntExtra("task_notification_id", -1);
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
        ImageButton fileView = findViewById(R.id.attachmentPreview);
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
                taskDBHelper.updateTaskById(id, title, description, category, notificationDateTime, selectedFileUri, isDone, isChecked, notificationId);
                if(isChecked) {
                    scheduleNotification(MainActivity.taskArrayList.get(position));
                } else {
                    cancelNotification();
                }
                MainActivity.updateData();
            }
        });
    }
    private void setButtons() {
        Button editTask = findViewById(R.id.buttonEdit);
        editTask.setOnClickListener(view -> {
            EditTaskWindow dialogFragment = new EditTaskWindow(getApplicationContext(), position, id, title, category, description, notificationDateTime, selectedFileUri, notificationId);
            dialogFragment.show(getSupportFragmentManager(), "show_edit_window_dialog");
            //todo odświeżanie ItemActivity po edycji
        });

        Button deleteTask = findViewById(R.id.buttonDelete);
        deleteTask.setOnClickListener(view -> {
           cancelNotification();
           MainActivity.taskArrayList.remove(position);
           taskListAdapter.notifyDataSetChanged();
           MainActivity.taskDBHelper.deleteTaskById(id);
           //todo przechodzenie do MainActivity
        });
    }
    private void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void scheduleNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        notificationIntent.putExtra("title", task.getTitle());
        notificationIntent.putExtra("description", task.getDescription());

        notificationId = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(task.getNotificationDateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minutesToSubtract = MainActivity.notificationTime;
        calendar.add(Calendar.MINUTE, -minutesToSubtract);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}

//todo włączanie i wyłączenie powiadomień