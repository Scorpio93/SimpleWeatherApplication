package com.example.jeka.exampledrawerbar.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import com.example.jeka.exampledrawerbar.R;
import com.example.jeka.exampledrawerbar.fragments.FragmentWeatherMain;


public abstract class SingleFragmentActivity extends AppCompatActivity{
    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle saveStateInstance){
        super.onCreate(saveStateInstance);
        setContentView(getLayoutResId());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null){
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
