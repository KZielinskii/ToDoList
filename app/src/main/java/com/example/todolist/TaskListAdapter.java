package com.example.todolist;

import static com.example.todolist.MainActivity.taskListAdapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {
    private static ArrayList<Task> taskArrayList;
    private Context context;

    public TaskListAdapter(Context context, ArrayList<Task> taskArrayList) {
        this.context = context;
        this.taskArrayList = taskArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView descriptionView;
        TextView dateView;
        ImageView imageView;
        CheckBox checkBox;
        LinearLayout listItem;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.editText);
            descriptionView = itemView.findViewById(R.id.editText2);
            dateView = itemView.findViewById(R.id.editText3);
            imageView = itemView.findViewById(R.id.editImage);
            checkBox = itemView.findViewById(R.id.checkbox);
            listItem = itemView.findViewById(R.id.list_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = taskArrayList.get(position);

        holder.titleView.setText(task.getTitle());
        holder.descriptionView.setText(task.getDescription());
        holder.dateView.setText(task.getNotificationDateTime());
        holder.checkBox.setChecked(task.isDone());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDBHelper taskDBHelper = MainActivity.taskDBHelper;
                if (holder.checkBox.isChecked()) {
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
        holder.itemView.setBackgroundColor(backgroundColor);

        if (task.getSelectedFileUri() != null) {
            holder.imageView.setImageResource(R.drawable.baseline_file_download_24);
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_file_download_off_24);
        }

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemActivity.class);
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

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
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
        return context.getResources().getColor(colorResId);
    }

    public static void updateData(ArrayList<Task> tasks) {
        taskArrayList.clear();
        taskArrayList.addAll(tasks);
        taskListAdapter.notifyDataSetChanged();
    }

    private void cancelNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getNotificationId(), notificationIntent, PendingIntent.FLAG_MUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
