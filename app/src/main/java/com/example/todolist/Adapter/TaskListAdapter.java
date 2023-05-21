package com.example.todolist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.R;

import com.example.todolist.Class.Task;

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

        TextView titleView = itemView.findViewById(R.id.title);
        TextView descriptonView = itemView.findViewById(R.id.description);
        ImageView icon = itemView.findViewById(R.id.taskAttachment);

        titleView.setText(task.getTitle());

        //updateIcon(icon, position);

        /*
        LinearLayout listItem = itemView.findViewById(R.id.list_item);
        listItem.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), WeatherForecastActivity.class);
            intent.putExtra("item_index", position);
            intent.putExtra("locality_name", locality.getName());
            intent.putExtra("temperature", locality.getCurrentTemperature());
            intent.putExtra("is_sunny", locality.getIsSunny());
            intent.putExtra("is_raining", locality.getIsRaining());
            intent.putExtra("latitude", locality.getLatitude());
            intent.putExtra("longitude", locality.getLongitude());
            intent.putExtra("pressure", locality.getPressure());
            intent.putExtra("description", locality.getWeatherDescription());
            intent.putExtra("visibility",locality.getVisibilityInMeters());
            intent.putExtra("humidity",locality.getHumidity());
            intent.putExtra("wind_speed",locality.getWindSpeed());
            intent.putExtra("wind_deg",locality.getWindDegree());
            for(int i=0; i<Locality.FOR_SIZE; i++)
            {
                intent.putExtra("five_days_data_"+i,locality.getDateFiveDays(i));
                intent.putExtra("five_days_temperature_"+i,locality.getTemperatureFiveDays(i));
                intent.putExtra("five_days_description_"+i,locality.getDescriptionFiveDays(i));
            }

            getContext().startActivity(intent);
        });
*/
        return itemView;
    }}
