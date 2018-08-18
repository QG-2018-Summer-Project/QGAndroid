package com.mobile.qg.qgtaxi.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.qg.qgtaxi.R;


/**
 * Created by 93922 on 2018/8/17.
 * 描述：空的Fragment，到时候删了
 */

public class FirstFragment extends Fragment {

     public static Fragment newInstance(){
         FirstFragment fragment = new FirstFragment();
         return fragment;
     }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment_layout,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
