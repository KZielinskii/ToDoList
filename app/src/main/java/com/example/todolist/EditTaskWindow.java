package com.example.todolist;

import static android.app.Activity.RESULT_OK;
import static com.example.todolist.MainActivity.notificationTime;
import static com.example.todolist.MainActivity.taskListAdapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTaskWindow extends DialogFragment {
    private static final int PICK_FILE_REQUEST = 1;
    private Context context;
    private Long id;
    private int position;
    private String title;
    private String category;
    private String description;
    private String notificationDateTime;
    private Uri selectedFileUri;
    private int notificationId;
    private TextView dateTimeView;
    private TextView attachmentView;


    public EditTaskWindow(Context context, int position,Long id, String title, String category, String description, String notificationDateTime, Uri selectedFileUri, int notificationId) {
        this.context = context;
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.notificationDateTime = notificationDateTime;
        this.selectedFileUri = selectedFileUri;
        this.notificationId = notificationId;
        this.position = position;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.add_task_window, null);

        EditText editText = ll.findViewById(R.id.editText);
        editText.setText(title);
        EditText editText2 = ll.findViewById(R.id.editText2);
        editText2.setText(description);
        Button buttonSelectDateTime = ll.findViewById(R.id.buttonSelectDateTime);
        dateTimeView = ll.findViewById(R.id.date_text);
        Button buttonSelectAttachment = ll.findViewById(R.id.buttonSelectImage);
        attachmentView = ll.findViewById(R.id.attachment);
        dateTimeView.setText(notificationDateTime);

        Spinner categorySpinner = ll.findViewById(R.id.categorySpinner);
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


        builder.setView(ll).setPositiveButton("Edytuj", (dialog, id) -> {
                    if (notificationDateTime.isEmpty()) {
                        Toast.makeText(context, "Nie wybrano daty!", Toast.LENGTH_SHORT).show();
                    } else {
                        cancelNotification();
                        String newTitle = editText.getText().toString();
                        String newDescription = editText2.getText().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
                        String createdDateTime = dateFormat.format(Calendar.getInstance().getTime());

                        Task task = new Task(0, newTitle, newDescription, category, notificationDateTime, createdDateTime, selectedFileUri, false , true, notificationId, context, true);
                        MainActivity.taskArrayList.set(position ,task);
                        taskListAdapter.notifyDataSetChanged();
                        scheduleNotification(task);
                    }
                })
                .setNegativeButton("Anuluj", (dialog, id) -> dialog.cancel());


        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        int buttonSize = getResources().getDimensionPixelSize(R.dimen.min_size);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonSize);
        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonSize);

        buttonSelectDateTime.setOnClickListener(v -> showDateTimePickerDialog());

        buttonSelectAttachment.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, PICK_FILE_REQUEST);

        });

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();
            attachmentView.setText(selectedFileUri.toString());
            copyFileToExternalStorage(selectedFileUri);
        }
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                Date selectedDate = calendar.getTime();
                                Date currentDate = new Date();

                                if (selectedDate.before(currentDate)) {
                                    Toast.makeText(context, "Wybrana data i godzina muszą być późniejsze niż obecna.", Toast.LENGTH_SHORT).show();
                                } else {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
                                    notificationDateTime = dateFormat.format(selectedDate);
                                    dateTimeView.setText(notificationDateTime);
                                }
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void copyFileToExternalStorage(Uri sourceUri) {
        try {
            String sourceFileName = getFileNameFromUri(sourceUri);

            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            File externalDir = Environment.getExternalStorageDirectory();
            File appDir = new File(externalDir, "ToDoList");
            if (!appDir.exists()) {
                if (!appDir.mkdirs()) {

                    return;
                }
            }
            File destinationFile = new File(appDir, sourceFileName);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            selectedFileUri = Uri.fromFile(destinationFile);

            attachmentView.setText(sourceFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                fileName = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }
    private void scheduleNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("title", task.getTitle());
        notificationIntent.putExtra("description", task.getDescription());

        notificationId = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

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

    private void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
