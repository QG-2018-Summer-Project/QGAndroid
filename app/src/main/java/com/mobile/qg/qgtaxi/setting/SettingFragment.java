package com.mobile.qg.qgtaxi.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.base.BaseApi;

import java.util.regex.Pattern;

import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_PERIOD;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_PORT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_TIMEOUT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.DEFAULT_URL;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_PERIOD;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_PORT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_TIMEOUT;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_URL;


/**
 * Created by 93922 on 2018/8/13.
 * 描述：
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private EditTextPreference urlEt;
    private EditTextPreference portEt;
    private EditTextPreference timeoutEt;
    private EditTextPreference timeEt;

    private SharedPreferences sharedPreferences;
    private static final String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
    private static final String PORT_REGEX = "[0-9]{1,5}";


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
        urlEt.setSummary("当前的服务器地址：" + sharedPreferences.getString(KEY_URL, DEFAULT_URL));
        portEt.setSummary("当前的端口号：" + sharedPreferences.getString(KEY_PORT, DEFAULT_PORT));
        timeoutEt.setSummary(sharedPreferences.getString(KEY_TIMEOUT, DEFAULT_TIMEOUT) + "秒(请输入数字)");
        timeEt.setSummary(sharedPreferences.getString(KEY_PERIOD, DEFAULT_PERIOD) + "秒(请输入数字)");
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
        portEt = (EditTextPreference) findPreference(KEY_PORT);
        timeoutEt = (EditTextPreference) findPreference(KEY_TIMEOUT);
        timeEt = (EditTextPreference) findPreference(KEY_PERIOD);

        urlEt.setOnPreferenceChangeListener(this);
        portEt.setOnPreferenceChangeListener(this);
        timeoutEt.setOnPreferenceChangeListener(this);
        timeEt.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_URL:
                String ip = sharedPreferences.getString(KEY_URL, DEFAULT_URL);
                urlEt.setSummary("当前的服务器地址:" + ip);
                BaseApi.edit()
                        .ip(ip)
                        .accept();
                break;
            case KEY_PORT:
                String port = sharedPreferences.getString(KEY_PORT, DEFAULT_PORT);
                portEt.setSummary("当前的端口号：" + port);
                BaseApi.edit()
                        .port(port)
                        .accept();
                break;
            case KEY_TIMEOUT:
                String timeOut = sharedPreferences.getString(KEY_TIMEOUT, DEFAULT_TIMEOUT);
                timeoutEt.setSummary(timeOut + "秒(请输入数字)");
                BaseApi.edit()
                        .timeout(Integer.parseInt(timeOut))
                        .accept();
                break;
            case KEY_PERIOD:
                String period = sharedPreferences.getString(KEY_PERIOD, DEFAULT_PERIOD);
                timeEt.setSummary(period + "秒(请输入数字)");
                BaseApi.edit()
                        .period(Integer.parseInt(period))
                        .accept();
                break;
            default:
                toast("修改失败，请重新尝试");
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();
        switch (preference.getKey()) {
            case KEY_URL:
                toast(Pattern.matches(regex, value) ? "修改服务器地址成功" : "IP地址不合法");
                return Pattern.matches(regex, value);
            case KEY_PORT:
                toast(Pattern.matches(PORT_REGEX, value) ? "修改端口成功" : "端口不合法");
                return Pattern.matches(PORT_REGEX, value);
            case KEY_TIMEOUT:
                toast(Integer.parseInt(value) < 5 ? "连接超时不能低于5秒" : "修改连接超时成功");
                return Integer.parseInt(value) >= 5;
            case KEY_PERIOD:
                toast(Integer.parseInt(value) < 5 ? "轮询间隔不能低于5秒" : "修改轮询时间成功");
                return Integer.parseInt(value) >= 5;
            default:
                return false;
        }
    }

    private void toast(String what) {
        Toast.makeText(getActivity(), what, Toast.LENGTH_SHORT).show();
    }

}
