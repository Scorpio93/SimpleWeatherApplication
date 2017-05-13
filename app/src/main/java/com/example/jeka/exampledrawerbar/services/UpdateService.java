package com.example.jeka.exampledrawerbar.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import com.example.jeka.exampledrawerbar.database.ForecastDatabaseQuery;
import com.example.jeka.exampledrawerbar.fragments.QueryPreferenceFragment;
import com.example.jeka.exampledrawerbar.model.OpenWeatherFetch;
import com.example.jeka.exampledrawerbar.model.WeatherItem;

import java.util.List;

public class UpdateService extends IntentService {
private static final String TAG = "UpdateService";
    private static final long UPDATE_DATA_INTERVAL = 60;

    private static ForecastDatabaseQuery sDatabaseQuery;

    public UpdateService() {
        super(TAG);

    }

    public static Intent newIntent(Context context){
        return new Intent(context, UpdateService.class);
    }

    public static void setServiceUpdate(Context context, boolean isOn){
        Intent intent = UpdateService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        sDatabaseQuery = new ForecastDatabaseQuery().getInstance(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), UPDATE_DATA_INTERVAL, pendingIntent);
        }else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        QueryPreferenceFragment.setUpdateOn(context, isOn);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()){
            return;
        }

        String query = QueryPreferenceFragment.getStoredQuery(this);
        String forecastDays = QueryPreferenceFragment.getForecastDays(this);
        String lastResultId = QueryPreferenceFragment.getForecastLastResultId(this);

        List<WeatherItem> items = new OpenWeatherFetch().downloadForecastQueryCity(query, forecastDays);

        if (items.size() == 0){
            return;
        }

        WeatherItem item = items.get(0);
        String resultId = item.getDate();
        Log.i(TAG, "is work");

        if (resultId.equals(lastResultId)){
            Log.i(TAG, "Got an old date: " + resultId);
        }else {
            Log.i(TAG, "Got a new date: " + resultId);

            if (!items.isEmpty()){
                sDatabaseQuery.addForecastValuesDatabase(items);
            }
        }
        QueryPreferenceFragment.setForecastLastResultId(this, resultId);
    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
