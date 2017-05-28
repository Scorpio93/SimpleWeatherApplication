package com.example.jeka.exampledrawerbar.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.Utils.HeaderView;
import com.example.jeka.exampledrawerbar.database.ForecastDatabaseQuery;
import com.example.jeka.exampledrawerbar.model.OpenWeatherFetch;
import com.example.jeka.exampledrawerbar.model.WeatherItem;
import com.example.jeka.exampledrawerbar.network.IconDownloader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class FragmentWeatherMain extends VisibleFragment
        implements LoaderManager.LoaderCallbacks<List<WeatherItem>>,
                   SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "FragmentWeather";
    private static final String DATE_FORMAT = "EEEE, d MMMM";
    private static final int PERMISSION_REQUEST = 0;

    private GoogleApiClient mClient;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private HeaderView mHeaderView;
    private HeaderView mFloatHeaderView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private FloatingActionButton mFloatingButton;
    private List<WeatherItem> mItems = new ArrayList<>();
    private Location mLocation;
    private IconDownloader<WeatherHolder> mIconDownloader;
    private Callbacks mCallbacks;

    private boolean isLocationSearchEnable = false;
    private boolean isHideToolbarView = false;

    public static FragmentWeatherMain newInstance() {
        return new FragmentWeatherMain();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                        Log.i(TAG, "client is connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast_list, container, false);
        getActivity().setTitle("");

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        mHeaderView = (HeaderView) view.findViewById(R.id.toolbar_header_view);
        mFloatHeaderView = (HeaderView) view.findViewById(R.id.float_header_view);
        mAppBarLayout.addOnOffsetChangedListener(this);

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.fragment_swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        updateItems();

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        mCallbacks.onDrawerToggleClick(mToolbar);

        mFloatingButton = (FloatingActionButton) view.findViewById(R.id.fragment_fab);
        AnimatorSet floatingButtonAnimate= new AnimatorSet();
        floatingButtonAnimate.setDuration(200).playTogether(
                ObjectAnimator.ofFloat(mFloatingButton, View.SCALE_X, 0f, 1f),
                ObjectAnimator.ofFloat(mFloatingButton, View.SCALE_Y, 0f, 1f)
        );
        floatingButtonAnimate.setStartDelay(150);
        floatingButtonAnimate.start();

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClient.isConnected()){
                    isLocationSearchEnable = true;
                    getLocation();
                }
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        updateSubtitle();

        return view;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = mAppBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        mHeaderView.animate()
                .alpha(percentage)
                .setDuration(100)
                .start();

        if (percentage == 1f && isHideToolbarView){
            mHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        }else if (percentage < 1f && !isHideToolbarView){
            mHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIconDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIconDownloader.quit();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_forecast_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                QueryPreferenceFragment.setStoredQuery(getActivity(), query);
                isLocationSearchEnable = false;
                refreshItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferenceFragment.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
                Log.i(TAG, "Query word: " + query);
            }
        });
    }

    private void updateItems() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void refreshItems() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setupAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new WeatherAdapter(mItems));
            Handler responseHandler = new Handler();
            mIconDownloader = new IconDownloader<>(responseHandler);
            mIconDownloader.setIconDownloadListener(new IconDownloader.IconDownloadListener<WeatherHolder>() {
                @Override
                public void onIconDownloaded(WeatherHolder holder, Bitmap icon) {
                    if (isAdded()) {
                        Drawable drawable = new BitmapDrawable(getResources(), icon);
                        holder.bindWeatherIcon(drawable);
                    }
                }
            });
            mIconDownloader.start();
            mIconDownloader.getLooper();

            mRecyclerView.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                            for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                                View item = mRecyclerView.getChildAt(i);
                                AnimatorSet floatingButtonAnimate= new AnimatorSet();
                                floatingButtonAnimate.setDuration(200).playTogether(
                                        ObjectAnimator.ofFloat(item, View.SCALE_X, 0f, 1f),
                                        ObjectAnimator.ofFloat(item, View.SCALE_Y, 0f, 1f)
                                );
                                floatingButtonAnimate.setStartDelay(i * 70);
                                floatingButtonAnimate.start();
                            }
                            return true;
                        }
                    });
        }
    }

    private void updateSubtitle(){
        if (!mItems.isEmpty()){
            WeatherItem item = mItems.get(0);
            StringBuilder subtitle = new StringBuilder().append(item.getCityName())
                    .append(", ")
                    .append(item.getCountry());
            mHeaderView.bindTo(String.format(getResources().getString(R.string.forecast_fragment)), String.valueOf(subtitle));
            mFloatHeaderView.bindTo(String.format(getResources().getString(R.string.forecast_fragment)), String.valueOf(subtitle));
        }
    }

    private void showProgress(){
            Log.i(TAG, "progress is show");
            mSwipeRefresh.setRefreshing(true);
    }

    private void hideProgress(){
            Log.i(TAG, "progress is hide");
            mSwipeRefresh.setRefreshing(false);
    }

    private String convertTimeStamp(String timeStamp, String timeFormat) {
        String sBuffer = new String();
        if (timeStamp != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(timeStamp) * 1000);
            TimeZone timeZone = TimeZone.getDefault();
            calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
            sBuffer = simpleDateFormat.format(calendar.getTime());
        }
        return sBuffer;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh");
        refreshItems();
    }

    private void getLocation() {
        Log.i(TAG, "getLocation is called");
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // Location is enable
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST);
            return;
        }

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Location: " + location);
                        mLocation = location;
                        refreshItems();
                    }
                });
    }

    @Override
    public Loader<List<WeatherItem>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");
        showProgress();
        String query = QueryPreferenceFragment.getStoredQuery(getActivity());
        String queryDays = QueryPreferenceFragment.getForecastDays(getActivity());

        return new FetchWeatherLoader(getActivity(), queryDays, query, mLocation, isLocationSearchEnable);
    }

    @Override
    public void onLoadFinished(Loader<List<WeatherItem>> loader, List<WeatherItem> data) {
        Log.i(TAG, "onLoadFinished");
        mItems = data;
        setupAdapter();
        updateSubtitle();
        hideProgress();

        if (!data.isEmpty()){
            QueryPreferenceFragment.setStoredQuery(getActivity(), data.get(0).getCityName());
        }
    }

    @Override
    public void onLoaderReset(Loader<List<WeatherItem>> loader) {
        Log.i(TAG, "onLoaderReset");
    }


    public interface Callbacks{
        void onDrawerToggleClick(Toolbar toolbar);
    }

    private static class FetchWeatherLoader extends AsyncTaskLoader<List<WeatherItem>> {
        private OpenWeatherFetch mWeatherFetch;
        private Context mContext;
        private String mQuery;
        private String mQueryDays;
        private Location mLocation;
        private Boolean mIsLocationSearchEnable;
        private ForecastDatabaseQuery mDatabaseQuery;
        private List<WeatherItem> mDownloadedList;

        public FetchWeatherLoader(Context context, String queryDays, String query, Location location, Boolean isLocationSearchEnable) {
            super(context);
            mContext = context.getApplicationContext();
            mWeatherFetch = new OpenWeatherFetch();
            mDatabaseQuery = new ForecastDatabaseQuery().getInstance(context);
            mQueryDays = queryDays;
            mQuery = query;
            mLocation = location;
            mIsLocationSearchEnable = isLocationSearchEnable;
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
            if (mIsLocationSearchEnable && mLocation != null){
                String latitude = String.valueOf(mLocation.getLatitude());
                String longitude = String.valueOf(mLocation.getLongitude());
                mDownloadedList = mWeatherFetch.downloadForecastLocation(latitude, longitude, mQueryDays);
            }else {
                mDownloadedList = mWeatherFetch.downloadForecastQueryCity(mQuery, mQueryDays);
            }
            writeNewDataInDatabase(mDownloadedList);
            return mDatabaseQuery.getForecastFromDatabase();
        }

        private void writeNewDataInDatabase(List<WeatherItem> items){
            String lastResultId = QueryPreferenceFragment.getForecastLastResultId(mContext);
            String oldResultQuery = QueryPreferenceFragment.getStoredQuery(mContext);

            if (!items.isEmpty()){
                String sDateId = items.get(0).getDate();
                String sCity = items.get(0).getCityName();

                boolean isDataOld = (mDatabaseQuery.getDatabaseRowCount() != items.size())
                        || (!sDateId.equals(lastResultId))
                        || (!sCity.equals(oldResultQuery));

                if (isDataOld){
                    Log.i(TAG, "Write data in database");
                    mDatabaseQuery.addForecastValuesDatabase(items);
                    QueryPreferenceFragment.setForecastLastResultId(mContext, sDateId);
                }
            }
        }
    }

    private class WeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mItemImageView;
        private TextView mItemDateTextView;
        private TextView mItemDescriptionTextView;
        private TextView mItemTempTextView;
        private TextView mItemTempMinTextView;
        private TextView mItemTempMaxTextView;
        private TextView mItemPressureTextView;
        private TextView mItemWindTextView;
        private TextView mItemCloudTextView;
        private CardView mCardView;
        private LinearLayout mCollapseView;
        private boolean mIsViewExpanded = false;
        private int mOriginalHeight = 0;

        public WeatherHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_item_image_view);
            mItemDateTextView = (TextView) itemView.findViewById(R.id.fragment_date_item_text_view);
            mItemDescriptionTextView = (TextView) itemView.findViewById(R.id.fragment_description_item_text_view);
            mItemTempTextView = (TextView) itemView.findViewById(R.id.fragment_temp_item_text_view);
            mItemTempMinTextView = (TextView) itemView.findViewById(R.id.fragment_temp_min_item_text_view);
            mItemTempMaxTextView = (TextView) itemView.findViewById(R.id.fragment_temp_max_item_text_view);
            mItemPressureTextView = (TextView) itemView.findViewById(R.id.fragment_pressure_item_text_view);
            mItemWindTextView = (TextView) itemView.findViewById(R.id.fragment_wind_item_text_view);
            mItemCloudTextView = (TextView) itemView.findViewById(R.id.fragment_cloud_item_text_view);
            mCardView = (CardView) itemView.findViewById(R.id.list_card_view);
            mCollapseView = (LinearLayout) itemView.findViewById(R.id.collapse_view);
            itemView.setOnClickListener(this);

            if (!mIsViewExpanded){
                mCollapseView.setVisibility(View.GONE);
                mCollapseView.setEnabled(false);
            }
        }

        public void bingWeatherItem(WeatherItem item){
            mItemDateTextView.setText(convertTimeStamp(item.getDate(), DATE_FORMAT));
            mItemDescriptionTextView.setText(item.getDescription());
            mItemTempTextView.setText(String.format(getResources().getString(R.string.current_weather_temperature),item.getTemperature()));
            mItemTempMinTextView.setText(Html.fromHtml(String.format(getResources().getString(R.string.temp_min_format), item.getMinTemperature())));
            mItemTempMaxTextView.setText(Html.fromHtml(String.format(getResources().getString(R.string.temp_max_format),item.getMaxTemperature())));
            mItemPressureTextView.setText(String.format(getResources().getString(R.string.pressure_format),item.getPressure()));
            mItemWindTextView.setText(String.format(getResources().getString(R.string.current_weather_wind),item.getWind()));
            mItemCloudTextView.setText(String.format(getResources().getString(R.string.current_weather_clouds),item.getClouds()));
        }

        public void bindWeatherIcon(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            if (mOriginalHeight == 0){
                mOriginalHeight = mCardView.getHeight();
            }

            ValueAnimator valueAnimator;
            if (!mIsViewExpanded){
                mCollapseView.setVisibility(View.VISIBLE);
                mCollapseView.setEnabled(true);
                mIsViewExpanded = !mIsViewExpanded;
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight, mOriginalHeight + (int) (mOriginalHeight / 2));
                Log.i(TAG, "view is not expanded");
            }else {
                mIsViewExpanded = !mIsViewExpanded;
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight + (int) (mOriginalHeight / 2), mOriginalHeight);

                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mCollapseView, View.ALPHA, 0,1)
                        .setDuration(200);
                alphaAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCollapseView.setVisibility(View.INVISIBLE);
                        mCollapseView.setEnabled(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                alphaAnimation.start();
                Log.i(TAG, "view is expanded");
            }

            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    mCardView.getLayoutParams().height = value.intValue();
                    mCardView.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder>{

        private List<WeatherItem> mWeatherItems;

        public WeatherAdapter(List<WeatherItem> weatherItems){
            mWeatherItems = weatherItems;
        }

        @Override
        public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_forecast, parent, false);
            return new WeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(WeatherHolder holder, int position) {
            WeatherItem weatherItem = mWeatherItems.get(position);
            holder.bingWeatherItem(weatherItem);
            mIconDownloader.queueIcon(holder, weatherItem.getIcon());
        }

        @Override
        public int getItemCount() {
            return mWeatherItems.size();
        }
    }
}
