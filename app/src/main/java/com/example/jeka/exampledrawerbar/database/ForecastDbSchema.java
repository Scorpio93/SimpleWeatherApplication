package com.example.jeka.exampledrawerbar.database;


public class ForecastDbSchema {

    public static final class ForecastTable{
        /*
          Table for weather forecast data
        */
        public static final String NAME = "forecast_table";

        public static final class Cols{
            public static final String DATE = "date";
            public static final String CITY_NAME = "cityname";
            public static final String COUNTRY = "country";
            public static final String TEMPERATURE = "temperature";
            public static final String MIN_TEMPERATURE = "min_temperature";
            public static final String MAX_TEMPERATURE = "max_temperature";
            public static final String PRESSURE = "pressure";
            public static final String HUMIDITY = "humidity";
            public static final String DESCRIPTION = "description";
            public static final String ICON = "icon";
            public static final String CLOUDS = "clouds";
            public static final String WIND = "wind";
        }
    }

    public static final class CurrentWeatherTable{
        /*
            Table for current weather data
        */
        public static final String CURRENT_NAME = "current_weather_table";

        public static final class Cols{
            public static final String DATE = "current_date";
            public static final String CITY_NAME = "current_cityname";
            public static final String COUNTRY = "current_country";
            public static final String TEMPERATURE = "current_temperature";
            public static final String MIN_TEMPERATURE = "current_min_temperature";
            public static final String MAX_TEMPERATURE = "current_max_temperature";
            public static final String PRESSURE = "current_pressure";
            public static final String HUMIDITY = "current_humidity";
            public static final String DESCRIPTION = "current_description";
            public static final String ICON = "current_icon";
            public static final String CLOUDS = "current_clouds";
            public static final String WIND = "current_wind";
            public static final String SUNRISE = "current_sunrise";
            public static final String SUNSET = "current_sunset";
        }
    }

    public static final class DetailTable{
        /*
           Table for detail forecast data (hourly weather forecast)
        */
        public static final String DETAIL_NAME = "detail_table";

        public static final class Cols{
            public static final String DATE = "detail_date";
            public static final String TEMPERATURE = "detail_temperature";
            public static final String ICON = "icon";
        }
    }
}
