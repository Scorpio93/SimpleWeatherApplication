package com.example.jeka.exampledrawerbar.model;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherFetch {

    private static final String TAG = "OpenWeatherFetch";
    private static final String API_KEY = "3798141df4bc230c87920ed24304f246";
    private static final Uri ENDPOINT_FORECAST = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily")
            .buildUpon()
            .appendQueryParameter("units", "metric")
            .appendQueryParameter("APPID", API_KEY)
            .build();

    private static final Uri ENDPOINT_CURRENT_WEATHER = Uri.parse("http://api.openweathermap.org/data/2.5/weather")
            .buildUpon()
            .appendQueryParameter("units", "metric")
            .appendQueryParameter("APPID", API_KEY)
            .build();

    private static final Uri ENDPOINT_DETAIL_WEATHER = Uri.parse("http://api.openweathermap.org/data/2.5/forecast")
            .buildUpon()
            .appendQueryParameter("units", "metric")
            .appendQueryParameter("cnt", "8")
            .appendQueryParameter("APPID", API_KEY)
            .build();

    public byte[] getUrlBytes(String urlSpect) throws IOException{
        URL url = new URL(urlSpect);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpect );
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpect) throws IOException{
        return new String(getUrlBytes(urlSpect));
    }

    private List<WeatherItem> downloadItems(String url){

        List<WeatherItem> items = new ArrayList<>();

        try{
            String jsonString = getUrlString(url);
            Log.i(TAG, "URL - " + url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }catch (IOException e){
            Log.e(TAG, "Failed to fetch items", e);
            Log.i(TAG,"Error - " +  e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        return items;
    }

    private List<WeatherItem> downloadDetailDataItems(String url){

        List<WeatherItem> detailDataItems = new ArrayList<>();

        try{
            String jsonString = getUrlString(url);
            Log.i(TAG, "URL - " + url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseDetailDataItem(detailDataItems, jsonBody);
        }catch (IOException e){
            Log.e(TAG, "Failed to fetch items", e);
            Log.i(TAG,"Error - " +  e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        return detailDataItems;
    }

    private WeatherItem downloadItem(String url){

        WeatherItem weatherItem = new WeatherItem();

        try{
            String jsonString = getUrlString(url);
            Log.i(TAG, "URL - " + url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItem(weatherItem, jsonBody);
            Log.i(TAG, weatherItem.getCityName());
        }catch (IOException e){
            Log.e(TAG, "Failed to fetch item", e);
            Log.i(TAG,"Error - " +  e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        return weatherItem;
    }

    public List<WeatherItem> downloadForecastQueryCity(String queryCity, String forecastCount){
        Uri.Builder builder = ENDPOINT_FORECAST.buildUpon()
                .appendQueryParameter("q", queryCity)
                .appendQueryParameter("cnt", forecastCount);
        return  downloadItems(builder.build().toString());
    }

    public List<WeatherItem> downloadForecastLocation(String lat, String lon, String forecastCount){
        Uri.Builder builder = ENDPOINT_FORECAST.buildUpon()
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .appendQueryParameter("cnt", forecastCount);
        return  downloadItems(builder.build().toString());
    }

    public WeatherItem downloadCurrentWeather(String queryCity){
        Uri.Builder builder = ENDPOINT_CURRENT_WEATHER.buildUpon()
                .appendQueryParameter("q", queryCity);
        return downloadItem(builder.build().toString());
    }

    public List<WeatherItem> downloadDetailWeather(String queryCity){
        Uri.Builder builder = ENDPOINT_DETAIL_WEATHER.buildUpon()
                .appendQueryParameter("q", queryCity);
        return downloadDetailDataItems(builder.build().toString());
    }

    private void parseItems(List<WeatherItem> items, JSONObject jsonBody)
                                        throws IOException, JSONException {
        JSONObject cityJsonObject = jsonBody.getJSONObject("city");
        JSONArray listJsonArray = jsonBody.getJSONArray("list");

        for (int i = 0; i < listJsonArray.length(); i++) {
            JSONObject itemListJsonObject = listJsonArray.getJSONObject(i);
            JSONObject tempJsonObject = itemListJsonObject.getJSONObject("temp");
            JSONArray weatherJsonArray = itemListJsonObject.getJSONArray("weather");
            JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);

            WeatherItem item = new WeatherItem();

            item.setCountry(cityJsonObject.getString("country"));
            item.setCityName(cityJsonObject.getString("name"));
            item.setDate(itemListJsonObject.getString("dt"));
            item.setTemperature(tempJsonObject.getString("day"));
            item.setMinTemperature(tempJsonObject.getString("min"));
            item.setMaxTemperature(tempJsonObject.getString("max"));
            item.setPressure(itemListJsonObject.getString("pressure"));
            item.setHumidity(itemListJsonObject.getString("humidity"));
            item.setDescription(weatherJsonObject.getString("description"));
            item.setIcon(weatherJsonObject.getString("icon"));
            item.setClouds(itemListJsonObject.getString("clouds"));
            item.setWind(itemListJsonObject.getString("speed"));

            items.add(item);
        }
    }

    private void parseDetailDataItem(List<WeatherItem> items, JSONObject jsonBody)
                                                throws IOException, JSONException {
        JSONObject cityJsonObject = jsonBody.getJSONObject("city");
        JSONArray listJsonArray = jsonBody.getJSONArray("list");

        for (int i = 0; i < listJsonArray.length(); i++) {
            JSONObject itemListJsonObject = listJsonArray.getJSONObject(i);
            JSONObject mainJsonObject = itemListJsonObject.getJSONObject("main");
            JSONObject windJsonObject = itemListJsonObject.getJSONObject("wind");
            JSONObject cloudsJsonObject = itemListJsonObject.getJSONObject("clouds");
            JSONArray weatherJsonArray = itemListJsonObject.getJSONArray("weather");
            JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);

            WeatherItem item = new WeatherItem();

            item.setCountry(cityJsonObject.getString("country"));
            item.setCityName(cityJsonObject.getString("name"));
            item.setDate(itemListJsonObject.getString("dt"));
            item.setTemperature(mainJsonObject.getString("temp"));
            item.setPressure(mainJsonObject.getString("pressure"));
            item.setHumidity(mainJsonObject.getString("humidity"));
            item.setDescription(weatherJsonObject.getString("description"));
            item.setIcon(weatherJsonObject.getString("icon"));
            item.setClouds(cloudsJsonObject.getString("all"));
            item.setWind(windJsonObject.getString("speed"));

            items.add(item);
        }
    }

    private void parseItem(WeatherItem weatherItem ,JSONObject jsonBody)
            throws IOException, JSONException {

        JSONArray weatherJsonArray = jsonBody.getJSONArray("weather");
        JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);
        JSONObject windJsonObject = jsonBody.getJSONObject("wind");
        JSONObject mainJsonObject = jsonBody.getJSONObject("main");
        JSONObject cloudJsonObject = jsonBody.getJSONObject("clouds");
        JSONObject sysJsonObject = jsonBody.getJSONObject("sys");

        weatherItem.setCityName(jsonBody.getString("name"));
        weatherItem.setDate(jsonBody.getString("dt"));
        weatherItem.setDescription(weatherJsonObject.getString("description"));
        weatherItem.setTemperature(mainJsonObject.getString("temp"));
        weatherItem.setIcon(weatherJsonObject.getString("icon"));
        weatherItem.setWind(windJsonObject.getString("speed"));
        weatherItem.setHumidity(mainJsonObject.getString("humidity"));
        weatherItem.setClouds(cloudJsonObject.getString("all"));
        weatherItem.setSunrise(sysJsonObject.getString("sunrise"));
        weatherItem.setSunset(sysJsonObject.getString("sunset"));
        weatherItem.setCountry(sysJsonObject.getString("country"));
    }
}
