package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            TaskDBHelper taskDBHelper = new TaskDBHelper(context);
            ArrayList<Task> taskList = taskDBHelper.getTasks(false, context.getResources().getString(R.string.all));

            for (Task task : taskList) {
                if (task.isNotification()) {
                    scheduleNotification(task);
                }
            }
        }
    }

    private void scheduleNotification(Task task) {
        // Umieść kod planujący powiadomienie
    }
}

