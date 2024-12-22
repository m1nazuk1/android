package com.example.homework_rmpo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class ReminderService extends Service {
    private static final String CHANNEL_ID = "reminder_channel";
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private PendingIntent secondAlarmIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.d("ReminderService", "onCreate called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ReminderService", "onStartCommand called");

        if (intent != null && intent.hasExtra("REMINDER_TIME") && intent.hasExtra("EVENT_TITLE")) {
            long timeInMillis = intent.getLongExtra("REMINDER_TIME", 0);
            String eventTitle = intent.getStringExtra("EVENT_TITLE");

            // Ранее тут было условие if (!hasReminder). Уберём его для теста.
            // Всегда устанавливаем будильник для проверки.
            setAlarm(timeInMillis, eventTitle, startId);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmManager != null && alarmIntent != null) {
            alarmManager.cancel(alarmIntent);
        }
        if (alarmManager != null && secondAlarmIntent != null) {
            alarmManager.cancel(secondAlarmIntent);
        }
        Log.d("ReminderService", "onDestroy called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private PendingIntent createPendingIntent(String action, String eventTitle, long reminderTime, int requestCode) {
        Intent intent = new Intent(action);
        intent.putExtra("EVENT_TITLE", eventTitle);
        intent.putExtra("REMINDER_TIME", reminderTime);
        return PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void setAlarm(long timeInMillis, String eventTitle, int startId) {
        createNotificationChannel();

        // Проверим, что время в будущем
        long currentTime = System.currentTimeMillis();
        if (timeInMillis <= currentTime) {
            Log.d("ReminderService", "Время напоминания уже прошло или равно текущему! Уведомление не будет установлено.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                // Нужно перенаправить пользователя в настройки
                // или как минимум уведомить, что без разрешения точные напоминания не сработают
                Log.d("ReminderService", "Нет разрешения на точные будильники!");
            }
        }
        // Первое уведомление
        alarmIntent = createPendingIntent("com.example.homework_rmpo.ALARM_TRIGGERED", eventTitle, timeInMillis, startId);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);

        // Второе уведомление (за 30 секунд до события)
        long notificationTime = timeInMillis - 30 * 1000;
        if (notificationTime > currentTime) {
            secondAlarmIntent = createPendingIntent("com.example.homework_rmpo.SECOND_ALARM_TRIGGERED", eventTitle, timeInMillis, startId + 1);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, secondAlarmIntent);
        } else {
            Log.d("ReminderService", "Слишком мало времени для второго уведомления за 30 секунд до события.");
        }

        Log.d("ReminderService", "Будильник установлен на " + timeInMillis);
    }
}