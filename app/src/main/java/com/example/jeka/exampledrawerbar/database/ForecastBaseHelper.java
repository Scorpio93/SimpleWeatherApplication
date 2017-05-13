package com.example.jeka.exampledrawerbar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.jeka.exampledrawerbar.database.ForecastDbSchema.CurrentWeatherTable;
import static com.example.jeka.exampledrawerbar.database.ForecastDbSchema.DetailTable;
import static com.example.jeka.exampledrawerbar.database.ForecastDbSchema.ForecastTable;


public class ForecastBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "forecast.db";


    public ForecastBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ForecastTable.NAME + "(" +
        " _id integer primary key autoincrement, " +
                ForecastTable.Cols.DATE + ", " +
                ForecastTable.Cols.CITY_NAME + ", " +
                ForecastTable.Cols.COUNTRY + ", " +
                ForecastTable.Cols.TEMPERATURE + ", " +
                ForecastTable.Cols.MIN_TEMPERATURE + ", " +
                ForecastTable.Cols.MAX_TEMPERATURE + ", " +
                ForecastTable.Cols.PRESSURE + ", " +
                ForecastTable.Cols.HUMIDITY + ", " +
                ForecastTable.Cols.DESCRIPTION + ", " +
                ForecastTable.Cols.ICON + ", " +
                ForecastTable.Cols.CLOUDS + ", " +
                ForecastTable.Cols.WIND +
                ")"
        );
        Log.i(TAG, "Forecast table created");

        db.execSQL("create table " + CurrentWeatherTable.CURRENT_NAME + "(" +
        " _id integer primary key autoincrement, " +
                CurrentWeatherTable.Cols.DATE + ", " +
                CurrentWeatherTable.Cols.CITY_NAME + ", " +
                CurrentWeatherTable.Cols.COUNTRY + ", " +
                CurrentWeatherTable.Cols.TEMPERATURE + ", " +
                CurrentWeatherTable.Cols.MIN_TEMPERATURE + ", " +
                CurrentWeatherTable.Cols.MAX_TEMPERATURE + ", " +
                CurrentWeatherTable.Cols.PRESSURE + ", " +
                CurrentWeatherTable.Cols.HUMIDITY + ", " +
                CurrentWeatherTable.Cols.DESCRIPTION + ", " +
                CurrentWeatherTable.Cols.ICON + ", " +
                CurrentWeatherTable.Cols.CLOUDS + ", " +
                CurrentWeatherTable.Cols.SUNRISE + ", " +
                CurrentWeatherTable.Cols.SUNSET + ", " +
                CurrentWeatherTable.Cols.WIND +
                ")");
        Log.i(TAG, "Table for current weather created");

        db.execSQL("create table " + DetailTable.DETAIL_NAME + "(" +
                " _id integer primary key autoincrement, " +
                DetailTable.Cols.DATE + ", " +
                DetailTable.Cols.TEMPERATURE + ", " +
                DetailTable.Cols.ICON + ")");
        Log.i(TAG, "Detail table created");
        Log.i(TAG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
