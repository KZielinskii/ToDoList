package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;
import static com.example.todolist.MainActivity.updateData;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import java.util.ArrayList;

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
    private void toggleService()
    {
        AppCompatToggleButton toggle = findViewById(R.id.toggle);
        toggle.setChecked(MainActivity.hidenDone);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.hidenDone = isChecked;

                updateData(getApplicationContext());
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
                updateData(getApplicationContext());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

