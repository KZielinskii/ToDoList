package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ItemActivity extends AppCompatActivity {
    private static final int OPEN_FILE_REQUEST_CODE = 1;
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

    private void setSpinner() {
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

    private void readIntent() {
        position = getIntent().getIntExtra("item_index", -1);
        id = getIntent().getLongExtra("task_id", -1);
        title = getIntent().getStringExtra("task_title");
        description = getIntent().getStringExtra("task_description");
        category = getIntent().getStringExtra("task_category");
        notificationDateTime = getIntent().getStringExtra("task_notification_date");
        createdDateTime = getIntent().getStringExtra("task_create_date");
        selectedFileUri = getIntent().getParcelableExtra("task_file");
        isDone = getIntent().getBooleanExtra("task_isDone", false);
        isOn = getIntent().getBooleanExtra("task_isOn", true);
        notificationId = getIntent().getIntExtra("task_notification_id", -1);

        if(id != -1) {
            Task task = getTaskById(id);
            if (task != null) {
                title = task.getTitle();
                description = task.getDescription();
                category = task.getCategory();
                notificationDateTime = task.getNotificationDateTime();
                createdDateTime = task.getCreatedDateTime();
                selectedFileUri = task.getSelectedFileUri();
                isDone = task.isDone();
                isOn = task.isNotification();
                notificationId = task.getNotificationId();
            }
        }
    }

    private Task getTaskById(long id) {
        TaskDBHelper dbHelper = new TaskDBHelper(getApplicationContext());
        return dbHelper.getTaskById(id);
    }

    private void setAllStrings() {
        TextView textTitle = findViewById(R.id.editTitle);
        TextView textDescription = findViewById(R.id.editDescription);
        TextView textNotificationDate = findViewById(R.id.editDate);
        TextView textCreateDate = findViewById(R.id.editDateCreate);

        textTitle.setText(title);
        textDescription.setText(description);
        textNotificationDate.setText(notificationDateTime);
        textCreateDate.setText(createdDateTime);
    }

    private void setAttachmentPreview() {
        ImageButton fileView = findViewById(R.id.attachmentPreview);
        if (selectedFileUri == null) {
            fileView.setImageResource(R.drawable.baseline_file_download_off_64);
        } else {
            fileView.setImageResource(R.drawable.baseline_file_download_64);
        }
    }

    private void setChechBox() {
        CheckBox checkBox = findViewById(R.id.checkbox);
        checkBox.setChecked(isOn);
    }

    private void setButtons() {

        Button editTitle = findViewById(R.id.buttonNewTitle);
        editTitle.setOnClickListener(view -> {
            EditText text = findViewById(R.id.editTitle);
            if (text.isEnabled()) {
                text.setEnabled(false);
                editTitle.setText(R.string.editTitle);
                title = text.getText().toString();

            } else {
                text.setEnabled(true);
                editTitle.setText(R.string.done);
            }

        });

        Button editDescription = findViewById(R.id.buttonNewDescription);
        editDescription.setOnClickListener(view -> {
            EditText text = findViewById(R.id.editDescription);
            if (text.isEnabled()) {
                text.setEnabled(false);
                editDescription.setText(R.string.editTitle);
                description = text.getText().toString();

            } else {
                text.setEnabled(true);
                editDescription.setText(R.string.done);
            }
        });

        Button buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime);
        buttonSelectDateTime.setOnClickListener(v -> showDateTimePickerDialog());

        Button save = findViewById(R.id.buttonSave);
        save.setOnClickListener(view -> {
            CheckBox checkBox = findViewById(R.id.checkbox);
            isOn = checkBox.isChecked();

            TaskDBHelper taskDBHelper = new TaskDBHelper(getApplicationContext());
            taskDBHelper.updateTaskById(id, title, description, category, notificationDateTime, selectedFileUri, isDone, isOn, notificationId);
            cancelNotification();

            MainActivity.updateData();
            if(isOn) scheduleNotification(taskDBHelper.getTaskById(id));

            this.finish();
        });

        Button deleteTask = findViewById(R.id.buttonDelete);
        deleteTask.setOnClickListener(view -> {
            cancelNotification();
            if(selectedFileUri!=null)
            {
                boolean isDeleted = deleteFileFromUri(selectedFileUri);
                if (isDeleted) {
                    Toast.makeText(this, "Pomyślnie usunięto zadanie: "+MainActivity.taskArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Plik nie został usunięty!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Pomyślnie usunięto zadanie: "+MainActivity.taskArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }

            MainActivity.taskArrayList.remove(position);
            MainActivity.taskDBHelper.deleteTaskById(id);
            MainActivity.updateData();
            this.finish();
        });

        ImageButton attachment = findViewById(R.id.attachmentPreview);
        attachment.setOnClickListener(view -> openCopiedFile());
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, notificationIntent, PendingIntent.FLAG_MUTABLE);

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

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, notificationIntent, PendingIntent.FLAG_MUTABLE);

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
    private void openCopiedFile() {
        Uri fileUri = selectedFileUri;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, null);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No application found to open the file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean deleteFileFromUri(Uri fileUri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            int rowsDeleted = contentResolver.delete(fileUri, null, null);

            return rowsDeleted > 0;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

}
