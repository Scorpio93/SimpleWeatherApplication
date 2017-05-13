package com.example.jeka.exampledrawerbar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.jeka.exampledrawerbar.model.WeatherItem;

import java.util.ArrayList;
import java.util.List;


public class ForecastDatabaseQuery {
    private static ForecastDatabaseQuery sInstance;
    private static Context sContext;
    private static SQLiteDatabase sDatabase;

    public static synchronized ForecastDatabaseQuery getInstance(Context context){
        if (sInstance == null){
            sContext = context.getApplicationContext();
            sDatabase = new ForecastBaseHelper(sContext).getWritableDatabase();
            sInstance = new ForecastDatabaseQuery();
        }
        return sInstance;
    }

    private static ContentValues getForecastContentValue(WeatherItem item){
        ContentValues values = new ContentValues();
        values.put(ForecastDbSchema.ForecastTable.Cols.DATE, item.getDate());
        values.put(ForecastDbSchema.ForecastTable.Cols.CITY_NAME, item.getCityName());
        values.put(ForecastDbSchema.ForecastTable.Cols.COUNTRY, item.getCountry());
        values.put(ForecastDbSchema.ForecastTable.Cols.TEMPERATURE, item.getTemperature());
        values.put(ForecastDbSchema.ForecastTable.Cols.MIN_TEMPERATURE, item.getMinTemperature());
        values.put(ForecastDbSchema.ForecastTable.Cols.MAX_TEMPERATURE, item.getMaxTemperature());
        values.put(ForecastDbSchema.ForecastTable.Cols.PRESSURE, item.getPressure());
        values.put(ForecastDbSchema.ForecastTable.Cols.HUMIDITY, item.getHumidity());
        values.put(ForecastDbSchema.ForecastTable.Cols.DESCRIPTION, item.getDescription());
        values.put(ForecastDbSchema.ForecastTable.Cols.ICON, item.getIcon());
        values.put(ForecastDbSchema.ForecastTable.Cols.CLOUDS, item.getClouds());
        values.put(ForecastDbSchema.ForecastTable.Cols.WIND, item.getWind());

        return values;
    }

    private static ContentValues getCurrentWeatherContentValue(WeatherItem item){
        ContentValues values = new ContentValues();
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.DATE, item.getDate());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.CITY_NAME, item.getCityName());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.COUNTRY, item.getCountry());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.TEMPERATURE, item.getTemperature());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.MIN_TEMPERATURE, item.getMinTemperature());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.MAX_TEMPERATURE, item.getMaxTemperature());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.PRESSURE, item.getPressure());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.HUMIDITY, item.getHumidity());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.DESCRIPTION, item.getDescription());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.ICON, item.getIcon());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.CLOUDS, item.getClouds());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.WIND, item.getWind());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.SUNRISE, item.getSunrise());
        values.put(ForecastDbSchema.CurrentWeatherTable.Cols.SUNSET, item.getSunset());

        return values;
    }

    private static ContentValues getDetailForecastContentValue(WeatherItem item){
        ContentValues values = new ContentValues();
        values.put(ForecastDbSchema.DetailTable.Cols.DATE, item.getDate());
        values.put(ForecastDbSchema.DetailTable.Cols.TEMPERATURE, item.getTemperature());
        values.put(ForecastDbSchema.DetailTable.Cols.ICON, item.getIcon());

        return values;
    }

    public long getDatabaseRowCount(){
        return DatabaseUtils.queryNumEntries(sDatabase, ForecastDbSchema.ForecastTable.NAME);
    }

    public void addForecastValuesDatabase(List<WeatherItem> items){
        if (!items.isEmpty()){
            sDatabase.delete(ForecastDbSchema.ForecastTable.NAME, null, null);
        }
        for (WeatherItem item:items){
            ContentValues values = getForecastContentValue(item);
            sDatabase.insert(ForecastDbSchema.ForecastTable.NAME, null, values);
        }
    }

    public void addCurrentValuesDatabase(WeatherItem item){
        if (!item.getTemperature().equals("")){
            sDatabase.delete(ForecastDbSchema.CurrentWeatherTable.CURRENT_NAME, null, null);
        }
        ContentValues currentValues = getCurrentWeatherContentValue(item);
        sDatabase.insert(ForecastDbSchema.CurrentWeatherTable.CURRENT_NAME, null, currentValues);
    }

    public void addDetailValuesDatabase(List<WeatherItem> items){
        if (!items.isEmpty()){
            sDatabase.delete(ForecastDbSchema.DetailTable.DETAIL_NAME, null, null);
        }
        for (WeatherItem item:items){
            ContentValues detailsValues = getDetailForecastContentValue(item);
            sDatabase.insert(ForecastDbSchema.DetailTable.DETAIL_NAME, null, detailsValues);
        }
    }

    public List<WeatherItem> getForecastFromDatabase(){

        List<WeatherItem> items = new ArrayList<>();

        ForecastCursorWrapper cursor = queryForecast(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                items.add(cursor.getForecastWeatherItem());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return items;
    }

    public WeatherItem getCurrentWeatherFromDatabase(){
        WeatherItem item = new WeatherItem();

        ForecastCursorWrapper cursorWrapper = queryCurrentWeather(null, null);

        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                item = cursorWrapper.getCurrentWeatherItem();
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return item;
    }

    public List<WeatherItem> getDetailFromDatabase(){

        List<WeatherItem> items = new ArrayList<>();

        ForecastCursorWrapper cursor = queryDetailForecast(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                items.add(cursor.getDetailWeatherItem());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return items;
    }

    private ForecastCursorWrapper queryForecast(String whereClause, String[] whereArgs){
        Cursor cursor = sDatabase.query(
                ForecastDbSchema.ForecastTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ForecastCursorWrapper(cursor);
    }

    private ForecastCursorWrapper queryCurrentWeather(String whereClause, String[] whereArgs){
        Cursor cursor = sDatabase.query(
                ForecastDbSchema.CurrentWeatherTable.CURRENT_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ForecastCursorWrapper(cursor);
    }

    private ForecastCursorWrapper queryDetailForecast(String whereClause, String[] whereArgs){
        Cursor cursor = sDatabase.query(
                ForecastDbSchema.DetailTable.DETAIL_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ForecastCursorWrapper(cursor);
    }
}
