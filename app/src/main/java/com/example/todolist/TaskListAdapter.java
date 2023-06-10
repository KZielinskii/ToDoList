package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskListAdapter extends ArrayAdapter<Task> {
    private static ArrayList<Task> taskArrayList;
    private Context context;

    public TaskListAdapter(Context context, ArrayList<Task> taskArrayList) {
        super(context, 0, taskArrayList);
        this.taskArrayList = taskArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item, parent, false);
        }

        Task task = taskArrayList.get(position);

        TextView titleView = itemView.findViewById(R.id.editText);
        TextView descriptionView = itemView.findViewById(R.id.editText2);
        TextView dateView = itemView.findViewById(R.id.editText3);
        ImageView imageView = itemView.findViewById(R.id.editImage);
        CheckBox checkBox = itemView.findViewById(R.id.checkbox);

        titleView.setText(task.getTitle());
        descriptionView.setText(task.getDescription());

        dateView.setText(task.getNotificationDateTime());
        checkBox.setChecked(task.isDone());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDBHelper taskDBHelper = MainActivity.taskDBHelper;
                if(checkBox.isChecked()) {
                    taskDBHelper.updateTaskById(task.getTaskId(), task.getTitle(), task.getDescription(), task.getCategory(), task.getNotificationDateTime(), task.getSelectedFileUri(), true, false, task.getNotificationId());
                    cancelNotification(task);
                } else {
                    taskDBHelper.updateTaskById(task.getTaskId(), task.getTitle(), task.getDescription(), task.getCategory(), task.getNotificationDateTime(), task.getSelectedFileUri(), false, false, task.getNotificationId());
                }

                MainActivity.updateData();
            }
        });


        String category = task.getCategory();
        int backgroundColor = getCategoryColor(category);
        itemView.setBackgroundColor(backgroundColor);

        if(task.getSelectedFileUri() != null)
        {
           imageView.setImageResource(R.drawable.baseline_file_download_24);
        } else {
            imageView.setImageResource(R.drawable.baseline_file_download_off_24);
        }

        LinearLayout listItem = itemView.findViewById(R.id.list_item);
        listItem.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ItemActivity.class);
            intent.putExtra("item_index", position);
            intent.putExtra("task_id", task.getTaskId());
            intent.putExtra("task_title", task.getTitle());
            intent.putExtra("task_description", task.getDescription());
            intent.putExtra("task_category", task.getCategory());
            intent.putExtra("task_notification_date", task.getNotificationDateTime());
            intent.putExtra("task_create_date", task.getCreatedDateTime());
            intent.putExtra("task_file", task.getSelectedFileUri());
            intent.putExtra("task_isDone", task.isDone());
            intent.putExtra("task_isOn", task.isNotification());
            intent.putExtra("task_notification_id", task.getNotificationId());

            getContext().startActivity(intent);
        });

        return itemView;
    }

    private int getCategoryColor(String category) {
        int colorResId;
        switch (category) {
            case "Praca":
                colorResId = R.color.category_praca;
                break;
            case "Dom":
                colorResId = R.color.category_dom;
                break;
            case "Nauka":
                colorResId = R.color.category_nauka;
                break;
            case "Zdrowie i fitness":
                colorResId = R.color.category_zdrowie;
                break;
            case "Finanse":
                colorResId = R.color.category_finanse;
                break;
            case "Ważne terminy":
                colorResId = R.color.category_terminy;
                break;
            case "Społeczność":
                colorResId = R.color.category_spolecznosc;
                break;
            default:
                colorResId = R.color.category_inne;
                break;
        }
        return getContext().getResources().getColor(colorResId);
    }

    public static void updateData(ArrayList<Task> tasks) {
        taskArrayList.clear();
        taskArrayList.addAll(tasks);
        taskListAdapter.notifyDataSetChanged();
    }

    private void cancelNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getNotificationId() , notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
