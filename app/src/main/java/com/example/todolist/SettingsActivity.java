package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;
import static com.example.todolist.MainActivity.updateData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private EditText notificationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateNotificationTime();
        toggleService();
        buttonEditService();
        buttonSaveService();
        spinnerService();

    }
    private void updateNotificationTime()
    {
        notificationTime = findViewById(R.id.editTime);
        notificationTime.setText( String.valueOf(MainActivity.notificationTime));
    }
    private void buttonEditService() {
        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationTime.setEnabled(true);
            }
        });
    }

    private void buttonSaveService() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationTime.setEnabled(false);
                String newTime = remove0FromTheFront();
                notificationTime.setText(newTime);
                MainActivity.notificationTime = Integer.parseInt(newTime);

                reloadAllNotifications();
                saveNotificationTime();
            }
        });
    }

    private void reloadAllNotifications() {
        for (Task task : MainActivity.taskArrayList) {
            if (task.isNotification()) {
                cancelNotification(task);
                scheduleNotification(task);
            }
        }
    }

    private String remove0FromTheFront()
    {
        String stringTime = String.valueOf(notificationTime.getText());
        int startIndex = 0;
        while (startIndex < stringTime.length() && stringTime.charAt(startIndex) == '0') {
            startIndex++;
        }
        String returnString = stringTime.substring(startIndex);
        if(returnString.equals(""))returnString = "0";
        return returnString;
    }
    private void toggleService()
    {
        AppCompatToggleButton toggle = findViewById(R.id.toggle);
        toggle.setChecked(MainActivity.hidenDone);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.hidenDone = isChecked;
                updateData();
                saceHidenDone();
            }
        });
    }
    private void spinnerService() {
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        int activeCategoryIndex = 0;
        String[] categories = getResources().getStringArray(R.array.category_spinner_settings);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(MainActivity.selectedCategory)) {
                activeCategoryIndex = i;
                break;
            }
        }
        categorySpinner.setSelection(activeCategoryIndex);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.selectedCategory = parent.getItemAtPosition(position).toString();
                updateData();
                saveSelectedCategory();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveNotificationTime()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefrences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedNotificationTime",MainActivity.notificationTime);
        editor.apply();
    }
    private void saceHidenDone()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefrences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("savedHidenDone", MainActivity.hidenDone);
        editor.apply();
    }
    private void saveSelectedCategory()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefrences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedSelectedCategory", MainActivity.selectedCategory);
        editor.apply();
    }

    private void cancelNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), task.getNotificationId(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void scheduleNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        notificationIntent.putExtra("title", task.getTitle());
        notificationIntent.putExtra("description", task.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), task.getNotificationId(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

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