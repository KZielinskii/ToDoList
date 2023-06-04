package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;

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
    private AppCompatToggleButton toggle;
    private EditText notificationTime;
    private Button btnEdit;
    private Button btnSave;
    private Spinner categorySpinner;

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
    private void toggleService()
    {
        toggle = findViewById(R.id.toggle);
        toggle.setChecked(MainActivity.hidenDone);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.hidenDone = isChecked;
                
                if(MainActivity.selectedCategory.equals(getResources().getString(R.string.all))) {
                    MainActivity.getDataAllCategory(getApplicationContext());
                }else {
                    MainActivity.getDataByCategory(getApplicationContext());
                }
            }
        });
    }
    private void spinnerService() {
        categorySpinner = findViewById(R.id.categorySpinner);

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
                String selectedCategory = parent.getItemAtPosition(position).toString();
                MainActivity.selectedCategory = selectedCategory;
                if(selectedCategory.equals(getResources().getString(R.string.all))) {
                    MainActivity.getDataAllCategory(getApplicationContext());
                }else {
                    MainActivity.getDataByCategory(getApplicationContext());
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

