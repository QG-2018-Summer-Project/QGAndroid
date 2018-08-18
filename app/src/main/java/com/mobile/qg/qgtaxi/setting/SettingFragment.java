package com.mobile.qg.qgtaxi.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.heatmap.PollingEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Pattern;

import static com.mobile.qg.qgtaxi.KeyValueConstant.DEFAULT_PERIOD;
import static com.mobile.qg.qgtaxi.KeyValueConstant.DEFAULT_TIMEOUT;
import static com.mobile.qg.qgtaxi.KeyValueConstant.DEFAULT_URL;
import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_PERIOD;
import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_TIMEOUT;
import static com.mobile.qg.qgtaxi.KeyValueConstant.KEY_URL;


/**
 * Created by 93922 on 2018/8/13.
 * 描述：
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private EditTextPreference urlEt;
    private EditTextPreference timeoutEt;
    private EditTextPreference timeEt;

    private SharedPreferences sharedPreferences;
    private static final String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";/*验证url的正则表达式，准确度未知*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setSummary();
    }

    private void setSummary() {
        urlEt.setSummary("当前的服务器地址:" + sharedPreferences.getString(KEY_URL, DEFAULT_URL));
        timeoutEt.setSummary(sharedPreferences.getString(KEY_TIMEOUT, "默认为10000") + "毫秒(请输入毫秒)");
        timeEt.setSummary(sharedPreferences.getString(KEY_PERIOD, "默认为5000") + "毫秒(请输入毫秒)");
    }

    @Override
    public void onResume() {
        super.onResume();
        setSummary();
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initPreferences() {
        urlEt = (EditTextPreference) findPreference(KEY_URL);
        timeoutEt = (EditTextPreference) findPreference(KEY_TIMEOUT);
        timeEt = (EditTextPreference) findPreference(KEY_PERIOD);

        urlEt.setOnPreferenceChangeListener(this);
        timeoutEt.setOnPreferenceChangeListener(this);
        timeEt.setOnPreferenceChangeListener(this);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_URL:
                String ip = sharedPreferences.getString(KEY_URL, DEFAULT_URL);
                urlEt.setSummary("当前的服务器地址:" + ip);
                EventBus.getDefault().post(new SettingEvent(sharedPreferences));
                break;
            case KEY_TIMEOUT:
                String timeOut = sharedPreferences.getString(KEY_TIMEOUT, DEFAULT_TIMEOUT);
                timeoutEt.setSummary("默认为" + timeOut + "毫秒(请输入毫秒)");
                EventBus.getDefault().post(new SettingEvent(sharedPreferences));
                break;
            case KEY_PERIOD:
                String period = sharedPreferences.getString(KEY_PERIOD, DEFAULT_PERIOD);
                timeEt.setSummary("默认为" + period + "毫秒(请输入毫秒)");
                EventBus.getDefault().post(new PollingEvent(sharedPreferences));
                break;
            default:
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();
        switch (preference.getKey()) {
            case KEY_URL:
                if (Pattern.matches(regex, value)) {
                    Toast.makeText(getActivity(), "修改服务器地址成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "IP地址不合法", Toast.LENGTH_SHORT).show();
                    return false;
                    /* */
                }
                urlEt.setSummary("当前的服务器地址:" + sharedPreferences.getString(KEY_URL, DEFAULT_URL));
                break;
            case KEY_TIMEOUT:
                if (Integer.parseInt(value) < 5000) {
                    Toast.makeText(getActivity(), "连接超时不能低于5秒", Toast.LENGTH_LONG).show();
                    return false;
                }
                /*输入框限制只能输入数字了，所以这里暂时没有判断*/
                break;
            case KEY_PERIOD:
                /*输入框限制只能输入数字了，所以这里暂时没有判断*/
                if (Integer.parseInt(value) < 5000) {
                    Toast.makeText(getActivity(), "轮询间隔不能低于5秒", Toast.LENGTH_LONG).show();
                    return false;
                }
                break;
        }
        return true;
    }

}
