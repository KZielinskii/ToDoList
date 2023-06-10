package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ItemActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
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
        setSpinner();
    }

    private void setSpinner()
    {
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        String[] categories = getResources().getStringArray(R.array.category_spinner);
        int categoryIndex = -1;
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                categoryIndex = i;
                break;
            }
        }
        if (categoryIndex != -1) {
            categorySpinner.setSelection(categoryIndex);
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = "Inne";
            }
        });
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
        TextView textDescription = findViewById(R.id.editDescription);
        TextView textNotificationDate = findViewById(R.id.editDate);
        TextView textCreateDate = findViewById(R.id.editDateCreate);

        textTitle.setText(title);
        textDescription.setText(description);
        textNotificationDate.setText(notificationDateTime);
        textCreateDate.setText(createdDateTime);
    }

    private void saveEditTask()
    {
        /*cancelNotification();
        String newTitle = editText.getText().toString();
        String newDescription = editText2.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
        String createdDateTime = dateFormat.format(Calendar.getInstance().getTime());

        Task task = new Task(taskId, newTitle, newDescription, category, notificationDateTime, createdDateTime, selectedFileUri, false , true, notificationId, context, true);
        MainActivity.taskArrayList.set(position ,task);
        TaskDBHelper taskDBHelper = MainActivity.taskDBHelper;
        taskDBHelper.updateTaskById(taskId, newTitle, newDescription, category, notificationDateTime, selectedFileUri, isDone, isNotification, notificationId);
        taskListAdapter.notifyDataSetChanged();
        scheduleNotification(task);*/
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

        Button editTitle = findViewById(R.id.buttonNewTitle);
        editTitle.setOnClickListener(view -> {
            EditText text = findViewById(R.id.editTitle);
            if(text.isEnabled())
            {
                text.setEnabled(false);
                editTitle.setText(R.string.editTitle);

            }
            else
            {
                text.setEnabled(true);
                editTitle.setText(R.string.done);
            }

        });

        Button editDescription = findViewById(R.id.buttonNewDescription);
        editDescription.setOnClickListener(view -> {
            EditText text = findViewById(R.id.editDescription);
            if(text.isEnabled())
            {
                text.setEnabled(false);
                editDescription.setText(R.string.editTitle);

            }
            else
            {
                text.setEnabled(true);
                editDescription.setText(R.string.done);
            }
        });

        Button buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime);
        buttonSelectDateTime.setOnClickListener(v -> showDateTimePickerDialog());

        Button save = findViewById(R.id.buttonSave);
        save.setOnClickListener(view -> {
            //todo usuń starą notifikacje
            //todo ustaw nową notifikacje
            //todo zamknij aktywność
        });

        Button deleteTask = findViewById(R.id.buttonDelete);
        deleteTask.setOnClickListener(view -> {
            cancelNotification();
            MainActivity.taskArrayList.remove(position);
            taskListAdapter.notifyDataSetChanged();
            MainActivity.taskDBHelper.deleteTaskById(id);
            this.finish();
        });
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(ItemActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(ItemActivity.this,
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                Date selectedDate = calendar.getTime();
                                Date currentDate = new Date();

                                if (selectedDate.before(currentDate)) {
                                    Toast.makeText(getApplicationContext(), "Wybrana data i godzina muszą być późniejsze niż obecna.", Toast.LENGTH_SHORT).show();
                                } else {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
                                    notificationDateTime = dateFormat.format(selectedDate);
                                    TextView editDate = findViewById(R.id.editDate);
                                    editDate.setText(notificationDateTime);
                                }
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
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