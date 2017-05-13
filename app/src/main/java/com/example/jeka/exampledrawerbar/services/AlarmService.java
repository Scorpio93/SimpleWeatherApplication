package com.example.jeka.exampledrawerbar.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.activities.WeatherMainActivity;
import com.example.jeka.exampledrawerbar.fragments.QueryPreferenceFragment;
import com.example.jeka.exampledrawerbar.model.WeatherItem;
import com.example.jeka.exampledrawerbar.model.OpenWeatherFetch;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";
    private static final String DATE_FORMAT = "d MM HH:mm";
    private static final long UPDATE_DATA_INTERVAL = 60;
    public static final String ACTION_SHOW_NOTIFICATION = "com.example.jeka.exampledrawerbar.SHOW_REPORT";
    public static final String PERM_PRIVATE = "com.example.jeka.exampledrawerbar.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    private static final String ICON_URL = "http://openweathermap.org/img/w/";
    private static final String IMAGE_FORMAT = ".png";

    public static Intent newIntent(Context context){
        return new Intent(context, AlarmService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent intent = AlarmService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), UPDATE_DATA_INTERVAL, pendingIntent);
        }else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        QueryPreferenceFragment.setAlarmOn(context, isOn);
    }

    public AlarmService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()){
            return;
        }

        String query = QueryPreferenceFragment.getStoredQuery(this);
        WeatherItem item = new OpenWeatherFetch().downloadCurrentWeather(query);

        if (item == null){
            return;
        }


        Resources resources = getResources();
        Intent i = WeatherMainActivity.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        Bitmap icon = getBitmapFromURL(new StringBuffer()
                .append(ICON_URL)
                .append(item.getIcon())
                .append(IMAGE_FORMAT).toString());
        Log.i(TAG, "bitmap is " + icon.toString());

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.notif_weather_forecast_title))
                .setSmallIcon(R.drawable.ic_notification_icon_cloud)
                .setLargeIcon(icon)
                .setContentTitle(String.format(resources.getString(R.string.notif_weather_content_title),
                        item.getTemperature(), item.getDescription()))
                .setContentText(String.format(resources.getString(R.string.notif_weather_content_title),
                        item.getCityName(), convertTimeStamp(item.getDate(),DATE_FORMAT)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        showBackgroundNotification(0, notification);
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    public Bitmap getBitmapFromURL(String url) {
        try {
            byte[] bitmapBytes = new OpenWeatherFetch().getUrlBytes(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private String convertTimeStamp(String timeStamp, String timeFormat) {
        String sBuffer = new String();
        if (timeStamp != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(timeStamp) * 1000);
            TimeZone timeZone = TimeZone.getDefault();
            calendar.add(Calendar.MILLISECOND,
                    timeZone.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
            sBuffer = simpleDateFormat.format(calendar.getTime());
        }
        return sBuffer;
    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
