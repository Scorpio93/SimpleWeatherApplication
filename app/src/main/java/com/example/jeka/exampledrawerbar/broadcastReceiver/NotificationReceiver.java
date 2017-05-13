package com.example.jeka.exampledrawerbar.broadcastReceiver;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.jeka.exampledrawerbar.services.AlarmService;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receiver result: " + getResultCode());

        if (getResultCode() != Activity.RESULT_OK){
            return;
        }

        int requestCode = intent.getIntExtra(AlarmService.REQUEST_CODE, 0);
        Notification notification = (Notification) intent.getParcelableExtra(AlarmService.NOTIFICATION);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(requestCode, notification);
    }
}
