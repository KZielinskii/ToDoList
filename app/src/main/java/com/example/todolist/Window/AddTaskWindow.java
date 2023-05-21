package com.example.todolist.Window;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.todolist.Class.Task;
import com.example.todolist.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskWindow extends DialogFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<Task> taskArrayList;
    private Context context;
    private String newDateTime;
    private TextView dateTimeView;
    private Uri selectedImageUri;
    private ImageView taskImage;

    public AddTaskWindow(ArrayList<Task> taskArrayList, Context context) {
        this.taskArrayList = taskArrayList;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.add_task_window, null);
        newDateTime = null;

        EditText editText = ll.findViewById(R.id.editText);
        Button buttonSelectDateTime = ll.findViewById(R.id.buttonSelectDateTime);
        dateTimeView = ll.findViewById(R.id.date_text);
        Button buttonSelectImage = ll.findViewById(R.id.buttonSelectImage);
        taskImage = ll.findViewById(R.id.taskImage);


        builder.setView(ll)
                .setPositiveButton("Dodaj", (dialog, id) -> {
                    if(newDateTime.isEmpty())
                    {
                        Toast.makeText(context, "Wybierz wszystkie opcje!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String newTitle = editText.getText().toString();
                        taskArrayList.add(new Task(newTitle, newDateTime, context));
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

        buttonSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                InputStream imageStream = context.getContentResolver().openInputStream(selectedImageUri);
                Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 128, 128, false);

                taskImage.setImageBitmap(scaledBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
