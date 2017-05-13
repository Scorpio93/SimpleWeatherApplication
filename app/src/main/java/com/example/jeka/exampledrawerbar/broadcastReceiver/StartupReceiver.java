package com.example.jeka.exampledrawerbar.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jeka.exampledrawerbar.fragments.QueryPreferenceFragment;
import com.example.jeka.exampledrawerbar.services.AlarmService;
import com.example.jeka.exampledrawerbar.services.UpdateService;

public class StartupReceiver extends BroadcastReceiver{
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAlarmOn = QueryPreferenceFragment.isAlarm(context);
        boolean isUpdateOn = QueryPreferenceFragment.isUpdate(context);
        AlarmService.setServiceAlarm(context, isAlarmOn);
        UpdateService.setServiceUpdate(context, isUpdateOn);
    }
}
