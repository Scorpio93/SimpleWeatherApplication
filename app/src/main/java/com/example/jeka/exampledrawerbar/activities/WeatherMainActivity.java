package com.example.jeka.exampledrawerbar.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.fragments.FragmentCurrentWeather;
import com.example.jeka.exampledrawerbar.fragments.FragmentMap;
import com.example.jeka.exampledrawerbar.fragments.FragmentWeatherMain;
import com.example.jeka.exampledrawerbar.fragments.InfoFragment;
import com.example.jeka.exampledrawerbar.fragments.QueryPreferenceFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class WeatherMainActivity extends SingleFragmentActivity
                                    implements NavigationView.OnNavigationItemSelectedListener,
                                                FragmentWeatherMain.Callbacks{

    private static final String TAG = "WeatherMainActivity";
    private static final int LOCATION_REQUEST_ERROR = 0;
    private static final String DIALOG_INFO = "Dialog_info";

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    public static Intent newIntent(Context context){
        return new Intent(context, WeatherMainActivity.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        if (mDrawerLayout != null){
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close) {

                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    Log.i(TAG, "Drawer close");
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    Log.i(TAG, "Drawer open");
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }

        mNavigationView = (NavigationView)findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected Fragment createFragment() {
        return FragmentWeatherMain.newInstance();
    }

    @Override
    protected void onResume(){
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS){
            Dialog errorDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, this, LOCATION_REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_week) {
            Log.i(TAG, "Navigation Week");
            fragment = FragmentWeatherMain.newInstance();
        } else if (id == R.id.nav_today) {
            Log.i(TAG, "Navigation today");
            fragment = FragmentCurrentWeather.newInstance();
        }else if (id == R.id.nav_map){
            Log.i(TAG, "Navigation map");
            fragment = FragmentMap.newInstance();
        }else if (id == R.id.nav_settings) {
            Log.i(TAG, "Navigation settings");
            fragment = QueryPreferenceFragment.newInstance();
        }else if(id == R.id.nav_share){
            Log.i(TAG, "Navigation share");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.test_body_text));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.test_subject));
            startActivity(intent);
        }else if (id == R.id.nav_comment){
            Log.i(TAG, "Navigation comment");
            Uri uri = Uri.parse("https://mylittlepony.hasbro.com/en-us");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else if (id == R.id.nav_about){
            Log.i(TAG, "Navigation about");
            InfoFragment infoDialog = new InfoFragment();
            infoDialog.show(fragmentManager, DIALOG_INFO);
        }

        if (mDrawerLayout != null){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        return true;
    }

    @Override
    public void onDrawerToggleClick(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout != null){
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });
        if (mDrawerToggle != null){
            mDrawerToggle.syncState();
        }
    }
}
