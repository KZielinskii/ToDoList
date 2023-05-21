package com.example.todolist;

import static android.app.Activity.RESULT_OK;

import static com.example.todolist.MainActivity.taskListAdapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskWindow extends DialogFragment {
    private static final int PICK_FILE_REQUEST = 1;
    private ArrayList<Task> taskArrayList;
    private Context context;
    private String newDateTime;
    private TextView dateTimeView;
    private Uri selectedFileUri;
    private TextView attachmentView;

    public AddTaskWindow(ArrayList<Task> taskArrayList, Context context) {
        this.taskArrayList = taskArrayList;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.add_task_window, null);
        newDateTime = "";

        EditText editText = ll.findViewById(R.id.editText);
        EditText editText2 = ll.findViewById(R.id.editText2);
        Button buttonSelectDateTime = ll.findViewById(R.id.buttonSelectDateTime);
        dateTimeView = ll.findViewById(R.id.date_text);
        Button buttonSelectAttachment = ll.findViewById(R.id.buttonSelectImage);
        attachmentView = ll.findViewById(R.id.attachment);


        builder.setView(ll).setPositiveButton("Dodaj", (dialog, id) -> {
                    if (newDateTime.isEmpty()) {
                        Toast.makeText(context, "Nie wybrano daty!", Toast.LENGTH_SHORT).show();
                    } else {
                        String newTitle = editText.getText().toString();
                        String newDescription = editText2.getText().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm", Locale.getDefault());
                        String createdDateTime = dateFormat.format(Calendar.getInstance().getTime());

                        taskArrayList.add(new Task(newTitle, newDescription, newDateTime, createdDateTime, selectedFileUri, context, true));
                        taskListAdapter.notifyDataSetChanged();
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
                                    newDateTime = dateFormat.format(selectedDate);
                                    dateTimeView.setText(newDateTime);
                                    Toast.makeText(context, "Wybrana data i godzina: " + newDateTime, Toast.LENGTH_SHORT).show();
                                }
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

}
