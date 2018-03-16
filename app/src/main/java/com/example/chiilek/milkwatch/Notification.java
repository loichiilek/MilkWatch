package com.example.chiilek.milkwatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import java.util.Calendar;
import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.app.PendingIntent.getActivity;
import static android.content.Context.*;

public class Notification extends Activity{
    private static final String CHANNEL_NAME = "default";
    private static final String CHANNEL_ID = "com.example.chiilek.milkwatch.notif";
    private NotificationManager manager;

    private void createChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "name", IMPORTANCE_DEFAULT);
            // Register the channel with the system
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            manager.createNotificationChannel(channel);
            channel.setLockscreenVisibility(VISIBILITY_PUBLIC);
        }
    }

    private void setIntent(){
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, InformationDisplay.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentIntent(resultPendingIntent);
    }


    public void createNotification(){
        createChannels();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cow_black)
                .setContentTitle("MILKWATCH")
                .setContentText("Bacteria levels are safe.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        setIntent();
    }

    private void createThresholdNotification(){
        createChannels();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cow_black)
                .setContentTitle("MILKWATCH")
                .setContentText("Bacteria threshold has been reached.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        setIntent();
    }

    private void setNotif(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
//      calendar.add(Calendar.HOUR_OF_DAY,  24);
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getBaseContext().getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
