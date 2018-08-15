package com.mobile.qg.qgtaxi.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.mobile.qg.qgtaxi.R;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 输入提示功能实现
 */
public class InputTipsActivity extends AppCompatActivity implements
        TextWatcher,
        InputtipsListener,
        InputTipsAdapter.TipsCallback {

    private String city = "广州";
    private AutoCompleteTextView mKeywordText;
    private RecyclerView mTipList;
    private InputTipsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputtip);
        mTipList = findViewById(R.id.inputlist);
        mTipList.setLayoutManager(new LinearLayoutManager(this));
        mKeywordText = findViewById(R.id.input_edittext);
        mKeywordText.addTextChangedListener(this);


    }

    private void getHistory() {
        HistoryDatabase
                .getInstance(this)
                .historyDao()
                .getFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<History>>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(ArrayList<History> searchHistories) {


                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        InputtipsQuery inputQuery = new InputtipsQuery(newText, city);
        inputQuery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(InputTipsActivity.this, inputQuery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    /**
     * 输入提示结果的回调
     *
     * @param tipList tip列表
     * @param rCode   返回码
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {

        if (tipList == null) {
            tipList = new ArrayList<>();
        }

        if (mAdapter == null) {
            mAdapter = new InputTipsAdapter(tipList);
            mAdapter.setCallback(this);
            mTipList.setAdapter(mAdapter);
        } else {
            mAdapter.refresh(tipList);
        }

    }

    @Override
    public void onClickTip(Tip tip) {
        HistoryDatabase.getInstance(this).historyDao().insert(HistoryFactory.formTip(tip));
        EventBus.getDefault().post(tip);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
