package com.mobile.qg.qgtaxi.situation;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 93922 on 2018/8/19.
 * 描述：
 */
public abstract class SituationCallback implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {
        result(500,null);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.code()!=200){
            result(response.code(),null);
            return;
        }

        String responseData=response.body().string();
        SituationResponse situationResponse=new Gson().fromJson(responseData,SituationResponse.class);
        result(situationResponse.getStatus(),situationResponse.getPointSet());
    }

    public abstract void result(int status, List<PointSet> pointSetList);
}
