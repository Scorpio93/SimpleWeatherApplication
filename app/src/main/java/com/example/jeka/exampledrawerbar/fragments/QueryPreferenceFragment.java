package com.example.jeka.exampledrawerbar.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.fragments.FragmentWeatherMain.Callbacks;
import com.example.jeka.exampledrawerbar.services.AlarmService;
import com.example.jeka.exampledrawerbar.services.UpdateService;


public class QueryPreferenceFragment extends PreferenceFragmentCompat{
    private static final String TAG ="QueryPreferenceFragment";
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_FORECAST_DAYS = "pref_key_list_forecast_days";
    private static final String PREF_FORECAST_LAST_RESULT_ID = "forecastLastResultId";
    private static final String PREF_CURRENT_LAST_RESULT_ID = "currentWeatherLastResultId";
    private static final String DEFAULT_PREFERENCE_VALUE = "0";
    private static final String PREF_DETAIL_LAST_RESULT_ID = "detailLastResultId";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";
    private static final String PREF_IS_UPDATE_ON = "isUpdateOn";

    private CheckBoxPreference isAlarmCheckBox;
    private CheckBoxPreference isUpdateCheckBox;
    private Callbacks mCallbacks;

    public static QueryPreferenceFragment newInstance(){
        return new QueryPreferenceFragment();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_screen);
        getActivity().setTitle("");

        isUpdateCheckBox = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_IS_UPDATE_ON);
        isUpdateCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i(TAG, preference.getKey() +"changed to "+ newValue);
                UpdateService.setServiceUpdate(getActivity(), (Boolean) newValue);
                return true;
            }
        });

        isAlarmCheckBox = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_IS_ALARM_ON);
        isAlarmCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i(TAG, preference.getKey() +"changed to "+ newValue);
                AlarmService.setServiceAlarm(getActivity(), (Boolean) newValue);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.preference_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        TextView toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.pref_title));
        mCallbacks.onDrawerToggleClick(toolbar);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    public static String getForecastDays(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_FORECAST_DAYS, context.getString(R.string.defaultDay));
    }

    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, context.getString(R.string.defaultQuery));
    }

    public static void setStoredQuery(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static String getForecastLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_FORECAST_LAST_RESULT_ID, DEFAULT_PREFERENCE_VALUE);
    }

    public static void setForecastLastResultId(Context context, String lastResult){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_FORECAST_LAST_RESULT_ID, lastResult)
                .apply();
    }

    public static String getCurrentLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CURRENT_LAST_RESULT_ID, DEFAULT_PREFERENCE_VALUE);
    }

    public static void setCurrentLastResultId(Context context, String lastResult){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CURRENT_LAST_RESULT_ID, lastResult)
                .apply();
    }

    public static String getDetailLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_DETAIL_LAST_RESULT_ID, DEFAULT_PREFERENCE_VALUE);
    }

    public static void setDetailLastResultId(Context context, String lastResult){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_DETAIL_LAST_RESULT_ID, lastResult)
                .apply();
    }



    public static boolean isUpdate(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_UPDATE_ON, false);
    }

    public static void setUpdateOn(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_UPDATE_ON, isOn)
                .apply();
    }

    public static boolean isAlarm(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }


}
