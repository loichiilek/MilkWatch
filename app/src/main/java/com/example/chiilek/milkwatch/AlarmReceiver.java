package com.example.chiilek.milkwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by felyssechua on 16/3/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notif = new Notification();
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                notif.setNotif(context, AlarmReceiver.class);
                return;
            }
        }
        notif.createNotification(context, InformationDisplay.class);
    }
}
