package com.mobile.qg.qgtaxi.setting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;

import com.mobile.qg.qgtaxi.R;


/**
 * Created by 93922 on 2018/8/13.
 * 描述：
 */

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            SettingFragment mSettingsFragment = new SettingFragment();
            replaceFragment(R.id.settings_container, mSettingsFragment);

        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }



    
}
