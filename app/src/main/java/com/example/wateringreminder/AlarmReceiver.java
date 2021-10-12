package com.example.wateringreminder;

import static android.content.Context.POWER_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.wateringreminder.entity.Reminder;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class AlarmReceiver extends BroadcastReceiver {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra(Constants.NOTIFICATION_INTENT, -1);
        long startTime = intent.getLongExtra("000", 0L);
        showNotification(context, notificationId, startTime);

    }

    private void showNotification(Context context, int notificationId, long startTime) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_plant_logo)
                .setContentTitle("Water reminder")
                .setContentText("Некоторым растениям нужен уход")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "waterReminderChannel";
            String description = "Channel for AlarmManager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    public boolean isAlarmSet(Context context, int notificationId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    public void setAlarm(Context context, int id, long  time, int period, long last) {
        long startTime = last + time + (long) (24 * 60 * 60 * 1000) * period
                + TimeZone.getDefault().getOffset(new Date().getTime());
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_INTENT, id);
        intent.putExtra("000", startTime);
        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent i = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                i.setData(Uri.parse("package:" + packageName));
                context.startActivity(i);
            }
        }

        if (alarmManager != null ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
            else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        }
    }

    public void cancelAlarm(Context context, int notificationId) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);
    }
}
