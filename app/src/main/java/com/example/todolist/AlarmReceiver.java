package com.example.todolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        Long taskId = intent.getLongExtra("taskId", -1);

        Intent notificationIntent = new Intent(context, ItemActivity.class);
        notificationIntent.putExtra("task_id", taskId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, taskId.intValue(), notificationIntent, PendingIntent.FLAG_MUTABLE);

        String channelId = "channel_id";
        CharSequence channelName = "Nazwa kanału";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(context, "Nie ma uprawnień!", Toast.LENGTH_SHORT).show();
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}


