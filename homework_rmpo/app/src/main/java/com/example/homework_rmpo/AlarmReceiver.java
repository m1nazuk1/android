package com.example.homework_rmpo;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String ALARM_TRIGGERED_ACTION = "com.example.homework_rmpo.ALARM_TRIGGERED";
    private static final String SECOND_ALARM_TRIGGERED_ACTION = "com.example.homework_rmpo.SECOND_ALARM_TRIGGERED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive triggered with action: " + intent.getAction());

        String action = intent.getAction();
        if ("com.example.homework_rmpo.ALARM_TRIGGERED".equals(action)) {
            String eventTitle = intent.getStringExtra("EVENT_TITLE");
            long reminderTime = intent.getLongExtra("REMINDER_TIME", 0);
            showNotification(context, eventTitle, "Напоминание", reminderTime);
        } else if ("com.example.homework_rmpo.SECOND_ALARM_TRIGGERED".equals(action)) {
            String eventTitle = intent.getStringExtra("EVENT_TITLE");
            long reminderTime = intent.getLongExtra("REMINDER_TIME", 0);
            showNotification(context, "Ближайшее событие", eventTitle, reminderTime);
        }
    }

    private void showNotification(Context context, String title, String content, long reminderTime) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content + " " + formatTime(reminderTime))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int uniqueNotificationId = (int) System.currentTimeMillis();
        notificationManager.notify(uniqueNotificationId, builder.build());
    }


    private void showNotification(Context context, String title, long reminderTime) {
        showNotification(context, title, "Напоминание", reminderTime);
    }

    private void showSecondNotification(Context context, String title, long reminderTime) {
        long currentTime = System.currentTimeMillis();
        if (reminderTime > currentTime) {
            showNotification(context, "Ближайшее событие", title, reminderTime);
        }
    }

    private String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm (dd.MM.yyyy)", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }
}