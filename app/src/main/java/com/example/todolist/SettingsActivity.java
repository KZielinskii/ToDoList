package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String selectedTime;
    private AppCompatToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner notificationTimeSpinner = findViewById(R.id.notificationTimeSpinner);
        notificationTimeSpinner.setOnItemSelectedListener(this);

        toggle = findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Przycisk jest włączony (zaznaczony)
                } else {
                    // Przycisk jest wyłączony (odznaczony)
                }
            }
        });

        selectedTime = notificationTimeSpinner.getSelectedItem().toString();
        // Pierwszy wybór
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedTime = parent.getItemAtPosition(position).toString();

        // Nowy wybór
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}

