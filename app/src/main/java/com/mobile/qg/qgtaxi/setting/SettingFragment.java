package com.mobile.qg.qgtaxi.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.mobile.qg.qgtaxi.R;

import java.util.regex.Pattern;


/**
 * Created by 93922 on 2018/8/13.
 * 描述：
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private static final String TAG = "SettingFragment";
    private EditTextPreference urlEt;
    private EditTextPreference timeoutEt;
    private EditTextPreference timeEt;
    private static final String KEY_URL     ="KEY_URL";/*URL的键*/
    private static final String KEY_TIMEOUT ="KEY_TIMEOUT";/*超时时长*/
    private static final String KEY_TIME    ="KEY_TIME";/*轮询时间*/
    private SharedPreferences sharedPreferences;
    private static final String DEFAULT_URL ="http://23333";
    private static final String regex       ="[a-zA-z]+://[^\\s]*";/*验证url的正则表达式，准确度未知*/

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
        urlEt.setSummary("当前的服务器地址:"+sharedPreferences.getString(KEY_URL," "));
        timeoutEt.setSummary(sharedPreferences.getString(KEY_TIMEOUT,"默认为5")+"秒(请输入数字)");
        timeEt.setSummary(sharedPreferences.getString(KEY_TIME,"默认为5")+"秒(请输入数字)");
    }

    @Override
    public void onResume() {
        super.onResume();
        setSummary();
        Log.e(TAG, "onResume: "+ sharedPreferences.getString(KEY_URL,""));
    }



    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initPreferences() {
        urlEt= (EditTextPreference) findPreference(KEY_URL);
        timeoutEt= (EditTextPreference) findPreference(KEY_TIMEOUT);
        timeEt= (EditTextPreference) findPreference(KEY_TIME);

        urlEt.setOnPreferenceChangeListener(this);
        timeoutEt.setOnPreferenceChangeListener(this);
        timeEt.setOnPreferenceChangeListener(this);

    }




    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case KEY_URL:
                urlEt.setSummary("当前的服务器地址:"+sharedPreferences.getString(KEY_URL," "));
                break;
            case KEY_TIMEOUT:
                timeoutEt.setSummary(sharedPreferences.getString(KEY_TIMEOUT,"默认为5")+"秒(请输入数字)");
                break;
            case KEY_TIME:
                timeEt.setSummary(sharedPreferences.getString(KEY_TIME,"默认为5")+"秒(请输入数字)");
                break;
            default:
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }
    }

   @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case KEY_URL:
                if (Pattern.matches(regex,newValue.toString())){
                    Toast.makeText(getActivity(), "修改服务器地址成功", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(), "IP地址不合法", Toast.LENGTH_SHORT).show();
                    return false;
                    /* */
                }
                urlEt.setSummary("当前的服务器地址:"+sharedPreferences.getString(KEY_URL," "));
                break;
            case KEY_TIMEOUT:
                /*输入框限制只能输入数字了，所以这里暂时没有判断*/
                break;
            case KEY_TIME:
                /*输入框限制只能输入数字了，所以这里暂时没有判断*/
                break;
        }
        return true;
    }
}
