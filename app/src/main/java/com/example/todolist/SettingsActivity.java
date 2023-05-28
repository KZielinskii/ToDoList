package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private AppCompatToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner notificationTimeSpinner = findViewById(R.id.notificationTimeSpinner);
        notificationTimeSpinner.setOnItemSelectedListener(this);

        toggle = findViewById(R.id.toggle);
        toggle.setChecked(MainActivity.hidenDone);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.hidenDone = isChecked;
            }
        });

        int notificationTimeIndex = getIndex(notificationTimeSpinner, MainActivity.notificationTime);
        if (notificationTimeIndex != -1) {
            notificationTimeSpinner.setSelection(notificationTimeIndex);
        }

        MainActivity.notificationTime = notificationTimeSpinner.getSelectedItem().toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.notificationTime = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }
}

