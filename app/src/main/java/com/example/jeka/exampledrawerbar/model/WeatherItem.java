package com.example.jeka.exampledrawerbar.model;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class WeatherItem {
    private String mCityName;
    private String mCountry;
    private String mDate;
    private String mTemperature;
    private String mMinTemperature;
    private String mMaxTemperature;
    private String mPressure;
    private String mHumidity;
    private String mDescription;
    private String mIcon;
    private String mClouds;
    private String mWind;
    private String mSunrise;
    private String mSunset;

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate.toString();
    }

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String mTemperature) {
        this.mTemperature = addPlusSymbol(mTemperature);
    }

    public String getPressure() {
        return mPressure;
    }

    public void setPressure(String mPressure) {
        this.mPressure = mPressure;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String mHumidity) {
        this.mHumidity = mHumidity;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public String getClouds() {
        return mClouds;
    }

    public void setClouds(String mClouds) {
        this.mClouds = mClouds;
    }

    public String getWind() {
        return mWind;
    }

    public void setWind(String mWind) {
        this.mWind = mWind;
    }

    public String getMaxTemperature() {
        return mMaxTemperature;
    }

    public void setMaxTemperature(String mMaxTemperature) {
        this.mMaxTemperature = addPlusSymbol(mMaxTemperature);
    }

    public String getMinTemperature() {
        return mMinTemperature;
    }

    public void setMinTemperature(String mMinTemperature) {
        this.mMinTemperature = addPlusSymbol(mMinTemperature);
    }

    public String getSunrise() {
        return mSunrise;
    }

    public void setSunrise(String mSunrise) {
        this.mSunrise = mSunrise;
    }

    public String getSunset() {
        return mSunset;
    }

    public void setSunset(String mSunSet) {
        this.mSunset = mSunSet;
    }


    private String addPlusSymbol(String temp){
        if (temp != null){
            char tempSymbol = temp.charAt(0);
            if (tempSymbol != '-' && tempSymbol != '+'){
                return '+' + temp;
            }
        }
        return temp;
    }
}
