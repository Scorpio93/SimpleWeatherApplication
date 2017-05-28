package com.example.jeka.exampledrawerbar.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jeka.exampledrawerbar.Utils.AnimationUtils;
import com.example.jeka.exampledrawerbar.database.ForecastDatabaseQuery;
import com.example.jeka.exampledrawerbar.network.IconDownloader;
import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.model.OpenWeatherFetch;
import com.example.jeka.exampledrawerbar.model.WeatherItem;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class FragmentCurrentWeather extends VisibleFragment {

    private static final String TAG = "FragmentCurrentWeather";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "EEEE, d MMMM";
    private static final int BASE_LOADER_ID = 0;
    private static final int DETAIL_LOADER_ID = 1;

    private LinearLayout mLinearLayout;
    private RecyclerView mRecycleViewDetail;
    private List<WeatherItem> mListDetailWeather;
    private ProgressBar mProgressBar;
    private LinearLayout mErrorLinearLayout;
    private TextView mCityTextView;
    private TextView mDataTextView;
    private TextView mDescriptionTextView;
    private TextView mTemperatureTextView;
    private ImageView mIconImageView;
    private TextView mWindTextView;
    private TextView mHumidityTextView;
    private TextView mCloudsTextView;
    private TextView mSunState;
    private IconDownloader<ImageView> mIconDownloader;
    private IconDownloader<DetailWeatherHolder> mDetailIconDownloader;
    private FragmentWeatherMain.Callbacks mCallbacks;

    private LoaderManager.LoaderCallbacks<WeatherItem> mBaseDataLoaderListener = new LoaderManager.LoaderCallbacks<WeatherItem>() {
        @Override
        public Loader<WeatherItem> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader");
            showProgress();
            String query = QueryPreferenceFragment.getStoredQuery(getActivity());
            return new FetchCurrentWeatherLoader(getContext(), query);
        }

        @Override
        public void onLoadFinished(Loader<WeatherItem> loader, WeatherItem data) {
            Log.i(TAG, "onLoadFinished");
            bindingDataForDay(data);
        }

        @Override
        public void onLoaderReset(Loader<WeatherItem> loader) {
            Log.i(TAG, "onLoaderReset");
        }
    };

    private LoaderManager.LoaderCallbacks<List<WeatherItem>> mDetailDataLoaderListener = new LoaderManager.LoaderCallbacks<List<WeatherItem>>() {
        @Override
        public Loader<List<WeatherItem>> onCreateLoader(int id, Bundle args) {
            String query = QueryPreferenceFragment.getStoredQuery(getActivity());
            return new FetchDetailWeatherLoader(getContext(), query);
        }

        @Override
        public void onLoadFinished(Loader<List<WeatherItem>> loader, List<WeatherItem> data) {

            mListDetailWeather = data;

            if (isAdded()) {
                mRecycleViewDetail.setAdapter(new DetailWeatherAdapter());
            }
        }

        @Override
        public void onLoaderReset(Loader<List<WeatherItem>> loader) {

        }
    };

    public static FragmentCurrentWeather newInstance(){
        return new FragmentCurrentWeather();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (FragmentWeatherMain.Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);

        Handler responseHandler = new Handler();

        mDetailIconDownloader = new IconDownloader<>(responseHandler);
        mDetailIconDownloader.setIconDownloadListener(new IconDownloader.IconDownloadListener<DetailWeatherHolder>() {
            @Override
            public void onIconDownloaded(DetailWeatherHolder target, Bitmap icon) {
                Drawable drawable = new BitmapDrawable(getResources(), icon);
                target.bindWeatherIcon(drawable);
            }
        });
        mDetailIconDownloader.start();
        mDetailIconDownloader.getLooper();

        mIconDownloader = new IconDownloader<>(responseHandler);
        mIconDownloader.setIconDownloadListener(new IconDownloader.IconDownloadListener<ImageView>() {
                @Override
                public void onIconDownloaded(ImageView target, Bitmap icon) {
                    if (isAdded()){
                        AnimationUtils.show(mIconImageView);
                        Drawable drawable = new BitmapDrawable(getResources(), icon);
                        target.setImageDrawable(drawable);
                    }
               }
        });
        mIconDownloader.start();
        mIconDownloader.getLooper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        getActivity().setTitle("");

        mErrorLinearLayout = (LinearLayout) view.findViewById(R.id.current_weather_linear_layout_error);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.current_weather_linear_layout);
        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_current_progress);
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView mToolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        mToolbarTitle.setText(String.format(getResources().getString(R.string.current_weather_fragment)));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        mCallbacks.onDrawerToggleClick(mToolbar);

        mCityTextView = (TextView) view.findViewById(R.id.text_fragment_city);
        mDataTextView = (TextView) view.findViewById(R.id.text_fragment_update_time);
        mDescriptionTextView = (TextView) view.findViewById(R.id.text_fragment_description);
        mTemperatureTextView = (TextView) view.findViewById(R.id.text_fragment_temperature);
        mIconImageView = (ImageView) view.findViewById(R.id.image_fragment_icon);

        mWindTextView = (TextView) view.findViewById(R.id.text_fragment_wind);
        mHumidityTextView = (TextView) view.findViewById(R.id.text_fragment_humidity);
        mCloudsTextView = (TextView) view.findViewById(R.id.text_fragment_clouds);
        mSunState = (TextView) view.findViewById(R.id.text_fragment_sun);

        Button mRepeatButton = (Button) view.findViewById(R.id.fragment_current_button_reload);
        mRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });

        mRecycleViewDetail = (RecyclerView) view.findViewById(R.id.recycle_view_detail_weather);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        mRecycleViewDetail.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIconDownloader.clearQueue();
        mDetailIconDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIconDownloader.quit();
        mDetailIconDownloader.quit();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_current_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_reload:
                reloadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(){
        getLoaderManager().initLoader(BASE_LOADER_ID, null, mBaseDataLoaderListener);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, mDetailDataLoaderListener);
    }

    private void reloadData(){
        getLoaderManager().restartLoader(BASE_LOADER_ID, null, mBaseDataLoaderListener);
        getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, mDetailDataLoaderListener);
    }

    private void showProgress(){
            Log.i(TAG, "progress is show");
            mErrorLinearLayout.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
    }

    private void hideProgress(){
            Log.i(TAG, "progress is hide");
            mProgressBar.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
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

    private void bindingDataForDay(WeatherItem data){
        hideProgress();

        if (data.getCityName() == null){
            mErrorLinearLayout.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.INVISIBLE);
            Log.i(TAG, "error layout is visible");
        }else {
            mErrorLinearLayout.setVisibility(View.INVISIBLE);
            Log.i(TAG, "error layout is invisible");

            mIconDownloader.queueIcon(mIconImageView, data.getIcon());
            mCityTextView.setText(String.format(getResources().getString(R.string.current_weather_city),
                    data.getCityName(),
                    data.getCountry()));
            mDataTextView.setText(convertTimeStamp(data.getDate(),DATE_FORMAT));
            mDescriptionTextView.setText(data.getDescription());
            mTemperatureTextView.setText(String.format(getResources().getString(R.string.current_weather_temperature),data.getTemperature()));
            mCloudsTextView.setText(String.format(getResources().getString(R.string.current_weather_clouds),data.getClouds()));
            mHumidityTextView.setText(String.format(getResources().getString(R.string.current_weather_humidity),data.getHumidity()));
            mWindTextView.setText(String.format(getResources().getString(R.string.current_weather_wind), data.getWind()));
            mSunState.setText(String.format(getResources().getString(R.string.current_weather_sun)
                    ,convertTimeStamp(data.getSunrise(), TIME_FORMAT)
                    ,convertTimeStamp(data.getSunset(), TIME_FORMAT)));
        }
    }

    public class DetailWeatherHolder extends RecyclerView.ViewHolder{

        public TextView mTextViewTime;
        public ImageView mImageViewIcon;
        public TextView mTextViewTemperature;

        public DetailWeatherHolder(View itemView) {
            super(itemView);

            mTextViewTime = (TextView) itemView.findViewById(R.id.text_detail_time);
            mImageViewIcon = (ImageView) itemView.findViewById(R.id.image_detail_weather);
            mTextViewTemperature = (TextView) itemView.findViewById(R.id.text_detail_temperature);
        }

        public void bindWeatherIcon(Drawable drawable){
            mImageViewIcon.setImageDrawable(drawable);
        }
    }

    private class DetailWeatherAdapter extends RecyclerView.Adapter<DetailWeatherHolder> {

        @Override
        public DetailWeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_detail, parent, false);
            return new DetailWeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(DetailWeatherHolder holder, int position) {
            WeatherItem item = mListDetailWeather.get(position);
            holder.mTextViewTime.setText(convertTimeStamp(item.getDate(), TIME_FORMAT));
            holder.mTextViewTemperature.setText(String.format(getString(R.string.current_weather_temperature), item.getTemperature()));
            mDetailIconDownloader.queueIcon(holder, item.getIcon());
        }

        @Override
        public int getItemCount() {
            return mListDetailWeather.size();
        }
    }

    private static class FetchCurrentWeatherLoader extends AsyncTaskLoader<WeatherItem> {
        private String mQuery;
        private Context mContext;
        private WeatherItem mWeatherItem;
        private ForecastDatabaseQuery mDatabaseQuery;

        public FetchCurrentWeatherLoader(Context context, String query) {
            super(context);
            mQuery = query;
            mContext = context;
            mDatabaseQuery = new ForecastDatabaseQuery().getInstance(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        protected void onStopLoading(){
            cancelLoad();
        }

        @Override
        public WeatherItem loadInBackground() {
            Log.i(TAG, "Load in background is start");

            mWeatherItem = new OpenWeatherFetch().downloadCurrentWeather(mQuery);
            String lastCurrentResultId = QueryPreferenceFragment.getCurrentLastResultId(mContext);
            String sDateId = mWeatherItem.getDate();

            if (!lastCurrentResultId.equals(sDateId)){
                Log.i(TAG, "Write data in current weather table");
                mDatabaseQuery.addCurrentValuesDatabase(mWeatherItem);
                QueryPreferenceFragment.setCurrentLastResultId(mContext ,sDateId);
            }
            return mDatabaseQuery.getCurrentWeatherFromDatabase();
        }
    }

    private static class FetchDetailWeatherLoader extends AsyncTaskLoader<List<WeatherItem>> {
        private String mQuery;
        private Context mContex;
        private List<WeatherItem> mWeatherDetailList;
        private ForecastDatabaseQuery mDatabaseQuery;

        public FetchDetailWeatherLoader(Context context, String query) {
            super(context);
            mQuery = query;
            mContex = context;
            mDatabaseQuery = new ForecastDatabaseQuery().getInstance(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        protected void onStopLoading(){
            cancelLoad();
        }

        @Override
        public List<WeatherItem> loadInBackground() {
            Log.i(TAG, "Load in background is start");

            mWeatherDetailList = new OpenWeatherFetch().downloadDetailWeather(mQuery);
            String lastDetailResult = QueryPreferenceFragment.getDetailLastResultId(mContex);

            if (!mWeatherDetailList.isEmpty()) {
                String sDateId = mWeatherDetailList.get(0).getDate();

                if (!lastDetailResult.equals(sDateId)) {
                    Log.i(TAG, "Write data in detail table");
                    mDatabaseQuery.addDetailValuesDatabase(mWeatherDetailList);
                    QueryPreferenceFragment.setDetailLastResultId(mContex, sDateId);
                }
            }
            return mDatabaseQuery.getDetailFromDatabase();
        }
    }
}
