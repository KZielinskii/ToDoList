package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.ItemActivity;
import com.example.todolist.R;

import com.example.todolist.Task;

import java.util.ArrayList;

public class TaskListAdapter extends ArrayAdapter<Task> {
    private ArrayList<Task> taskArrayList;
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

        titleView.setText(task.getTitle());
        descriptionView.setText(task.getDescription());
        dateView.setText(task.getNewDateTime());

        if(task.getSelectedFileUri() != null)
        {
           imageView.setImageResource(R.drawable.ic_positive);
        } else {
            imageView.setImageResource(R.drawable.ic_negative);
        }

        LinearLayout listItem = itemView.findViewById(R.id.list_item);
        listItem.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ItemActivity.class);
            intent.putExtra("item_index", position);
            intent.putExtra("task_title", task.getTitle());
            //todo inne elementy

            getContext().startActivity(intent);
        });

        return itemView;
    }}
