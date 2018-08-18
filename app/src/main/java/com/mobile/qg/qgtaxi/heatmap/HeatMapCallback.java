package com.mobile.qg.qgtaxi.heatmap;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 11234 on 2018/8/12.
 * 热力图Async回调
 */
public abstract class HeatMapCallback implements Callback {
    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        result(500, null);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (response.code() != 200) {
            result(response.code(), null);
            return;
        }

        String responseData = Objects.requireNonNull(response.body()).string();
        HeatMapResponse heatMapResponse = new Gson().fromJson(responseData, HeatMapResponse.class);
        Log.e(TAG, "onResponse: " + heatMapResponse);
        result(heatMapResponse.getStatus(), heatMapResponse.getPointSet());//null point？

    }

    public abstract void result(int status, List<HeatMapLatLng> pointSet);

}
