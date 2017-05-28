package com.example.jeka.exampledrawerbar.fragments;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.Utils.AnimationUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.stats.StatsEvent;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import java.net.MalformedURLException;
import java.net.URL;


public class FragmentMap extends Fragment implements OnMapReadyCallback,
                                    AdapterView.OnItemSelectedListener {
    private static final String TAG = "FragmentMap";
    private static final int PERMISSION_REQUEST = 0;
    private static final String KEY_MAP_SAVED_STATE = "mapState";
    private static final String MAP_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png?APPID=3798141df4bc230c87920ed24304f246";
    private static final String MAP_TYPE_CLOUDS = "clouds";
    private static final String MAP_TYPE_PRECIPITATION = "precipitation";
    private static final String MAP_TYPE_PRESSURE = "pressure";
    private static final String MAP_TYPE_WIND = "wind";
    private static final String MAP_TYPE_TEMP = "temp";
    private final static int SELECT_CLOUDS = 0;
    private final static int SELECT_PRECIPITATION = 1;
    private final static int SELECT_PRESSURE = 2;
    private final static int SELECT_WIND = 3;
    private final static int SELECT_TEMP = 4;

    private FragmentWeatherMain.Callbacks mCallbacks;
    private GoogleApiClient mClient;
    private GoogleMap mGoogleMap;
    private Spinner mSpinner;
    private Toolbar mToolbar;
    private MapView mMapView;
    private FloatingActionButton mMyLocationButton;
    private TileOverlay mTileOverlay;

    public static FragmentMap newInstance() {
        return new FragmentMap();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (FragmentWeatherMain.Callbacks) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        getActivity().setTitle("");

        mSpinner = (Spinner) view.findViewById(R.id.map_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_style, getResources().getStringArray(R.array.spinner_values));
        adapter.setDropDownViewResource(R.layout.spiner_item_style);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        mMapView = (MapView) view.findViewById(R.id.fragment_map_view);
        Bundle mapState = (savedInstanceState != null)
                ? savedInstanceState.getBundle(KEY_MAP_SAVED_STATE): null;
        mMapView.onCreate(mapState);
        mMapView.getMapAsync(this);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_map_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        mCallbacks.onDrawerToggleClick(mToolbar);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i(TAG, "client is connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i(TAG, "client is suspended");
                    }
                })
                .build();

        mMyLocationButton = (FloatingActionButton) view.findViewById(R.id.fab_my_location);
        AnimationUtils.show(mMyLocationButton);
        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "My locate button pressed");
                if (mClient.isConnected()){
                    getLocation();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);
        outState.putBundle(KEY_MAP_SAVED_STATE, mapState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        updateLayer(MAP_TYPE_CLOUDS);
    }

    private void updateLayer(final String mapType){
        if (mTileOverlay != null){
            mTileOverlay.remove();
            mTileOverlay.clearTileCache();
        }

        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String sUrl = String.format(MAP_URL, mapType, zoom, x, y);
                URL url = null;

                try {
                    url = new URL(sUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return url;
            }
        };
        if (mGoogleMap != null){
            mTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).transparency(0.5f));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case SELECT_CLOUDS:
                updateLayer(MAP_TYPE_CLOUDS);
                break;
            case SELECT_PRECIPITATION:
                updateLayer(MAP_TYPE_PRECIPITATION);
                break;
            case SELECT_PRESSURE:
                updateLayer(MAP_TYPE_PRESSURE);
                break;
            case SELECT_WIND:
                updateLayer(MAP_TYPE_WIND);
                break;
            case SELECT_TEMP:
                updateLayer(MAP_TYPE_TEMP);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST);
            return;
        }

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Location: " + location);
                        LatLng myPoint = new LatLng(location.getLatitude(), location.getLongitude());

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(myPoint)
                                .build();
                        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
                        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
                        mGoogleMap.animateCamera(update);
                    }
                });
    }
}
