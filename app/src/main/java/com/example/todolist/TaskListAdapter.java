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
            intent.putExtra("task_title", task.getTitle());
            //todo inne elementy

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

}
