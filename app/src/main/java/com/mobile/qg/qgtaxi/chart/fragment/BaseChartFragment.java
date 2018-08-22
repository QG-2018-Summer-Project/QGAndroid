package com.mobile.qg.qgtaxi.chart.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.mobile.qg.qgtaxi.entity.LatLngFactory;

import java.util.ArrayList;

/**
 * Created by 11234 on 2018/8/23.
 */
public abstract class BaseChartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private boolean isReady = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isReady) {
            isReady = true;
            readyRequest();
        }
    }

    protected abstract void readyRequest();

    protected abstract void setData(ArrayList<Entry> data);

    protected ArrayList<Entry> createEntryData(ArrayList<Float> yData) {
        int numTime = Integer.valueOf(LatLngFactory.INSTANCE.getHour());
        ArrayList<Entry> data = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            data.add(new Entry(numTime + i, yData.get(i)));
        }
        return data;
    }

    protected ArrayList<BarEntry> createBarEntryData(ArrayList<Float> yData) {
        int numTime = Integer.valueOf(LatLngFactory.INSTANCE.getHour());
        ArrayList<BarEntry> data = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            data.add(new BarEntry(numTime + i, yData.get(i)));
        }
        return data;
    }

    protected abstract void endRefresh();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
