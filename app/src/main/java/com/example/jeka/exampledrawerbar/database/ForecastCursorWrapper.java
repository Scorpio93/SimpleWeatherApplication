package com.example.jeka.exampledrawerbar.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.jeka.exampledrawerbar.model.WeatherItem;


public class ForecastCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ForecastCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public WeatherItem getForecastWeatherItem(){
        String dateString = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.DATE));
        String cityName = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.CITY_NAME));
        String country = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.COUNTRY));
        String temperature = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.TEMPERATURE));
        String minTemperature = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.MIN_TEMPERATURE));
        String maxTemperature = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.MAX_TEMPERATURE));
        String pressure = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.PRESSURE));
        String humidity = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.HUMIDITY));
        String description = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.DESCRIPTION));
        String icon = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.ICON));
        String clouds = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.CLOUDS));
        String wind = getString(getColumnIndex(ForecastDbSchema.ForecastTable.Cols.WIND));

        WeatherItem item = new WeatherItem();
        item.setDate(dateString);
        item.setCityName(cityName);
        item.setCountry(country);
        item.setTemperature(temperature);
        item.setMinTemperature(minTemperature);
        item.setMaxTemperature(maxTemperature);
        item.setPressure(pressure);
        item.setHumidity(humidity);
        item.setDescription(description);
        item.setIcon(icon);
        item.setClouds(clouds);
        item.setWind(wind);

        return item;
    }

    public WeatherItem getCurrentWeatherItem(){
        String dateString = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.DATE));
        String cityName = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.CITY_NAME));
        String country = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.COUNTRY));
        String temperature = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.TEMPERATURE));
        String minTemperature = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.MIN_TEMPERATURE));
        String maxTemperature = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.MAX_TEMPERATURE));
        String pressure = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.PRESSURE));
        String humidity = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.HUMIDITY));
        String description = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.DESCRIPTION));
        String icon = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.ICON));
        String clouds = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.CLOUDS));
        String wind = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.WIND));
        String sunrise = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.SUNRISE));
        String sunset = getString(getColumnIndex(ForecastDbSchema.CurrentWeatherTable.Cols.SUNSET));

        WeatherItem item = new WeatherItem();
        item.setDate(dateString);
        item.setCityName(cityName);
        item.setCountry(country);
        item.setTemperature(temperature);
        item.setMinTemperature(minTemperature);
        item.setMaxTemperature(maxTemperature);
        item.setPressure(pressure);
        item.setHumidity(humidity);
        item.setDescription(description);
        item.setIcon(icon);
        item.setClouds(clouds);
        item.setWind(wind);
        item.setSunrise(sunrise);
        item.setSunset(sunset);

        return item;
    }

    public WeatherItem getDetailWeatherItem(){
        String dateString = getString(getColumnIndex(ForecastDbSchema.DetailTable.Cols.DATE));
        String temperature = getString(getColumnIndex(ForecastDbSchema.DetailTable.Cols.TEMPERATURE));
        String icon = getString(getColumnIndex(ForecastDbSchema.DetailTable.Cols.ICON));

        WeatherItem item = new WeatherItem();
        item.setDate(dateString);
        item.setTemperature(temperature);
        item.setIcon(icon);

        return item;
    }

}
