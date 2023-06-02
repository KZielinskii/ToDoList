package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

public class SettingsActivity extends AppCompatActivity {
    private AppCompatToggleButton toggle;
    private EditText notificationTime;
    private Button btnEdit;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateNotificationTime();
        toggleService();
        buttonEditService();
        buttonSaveService();

    }
    private void updateNotificationTime()
    {
        notificationTime = findViewById(R.id.editTime);
        notificationTime.setText( String.valueOf(MainActivity.notificationTime));
    }
    private void toggleService()
    {
        toggle = findViewById(R.id.toggle);
        toggle.setChecked(MainActivity.hidenDone);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.hidenDone = isChecked;
                MainActivity.updateData(getApplicationContext());
            }
        });
    }
    private void buttonEditService() {
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationTime.setEnabled(true);
            }
        });
    }

    private void buttonSaveService() {
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationTime.setEnabled(false);
                String newTime = remove0FromTheFront();
                notificationTime.setText(newTime);
                MainActivity.notificationTime = Integer.parseInt(newTime);
            }
        });
    }
    private String remove0FromTheFront()
    {
        String stringTime = String.valueOf(notificationTime.getText());
        int startIndex = 0;
        while (startIndex < stringTime.length() && stringTime.charAt(startIndex) == '0') {
            startIndex++;
        }
        return stringTime.substring(startIndex);
    }

}

