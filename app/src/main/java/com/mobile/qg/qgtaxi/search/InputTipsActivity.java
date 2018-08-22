package com.mobile.qg.qgtaxi.search;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.history.History;
import com.mobile.qg.qgtaxi.history.HistoryAdapter;
import com.mobile.qg.qgtaxi.history.HistoryDatabase;
import com.mobile.qg.qgtaxi.history.HistoryFactory;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.KEY_ROUTE;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.VALUE_ROUTE_END;
import static com.mobile.qg.qgtaxi.constant.PreferenceConstant.VALUE_ROUTE_START;

/**
 * 输入提示功能实现
 */
public class InputTipsActivity extends AppCompatActivity implements
        TextWatcher,
        InputtipsListener,
        InputTipsAdapter.TipsCallback,
        HistoryAdapter.HistoriesCallback {

    private final static String CITY = "中国";

    @BindView(R.id.inputlist)
    protected RecyclerView mTipList;

    @BindView(R.id.pro)
    protected ProgressBar mProgressBar;

    @BindView(R.id.input_edittext)
    AutoCompleteTextView mKeywordText;

    private InputTipsAdapter mAdapter;
    private int mPurpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputtip);
        ButterKnife.bind(this);

        mPurpose = getIntent().getIntExtra(KEY_ROUTE, 0);

        mTipList.setLayoutManager(new LinearLayoutManager(this));
        mKeywordText.addTextChangedListener(this);

        getHistory();


    }

    private void getHistory() {
        mProgressBar.setVisibility(View.VISIBLE);
        Room.databaseBuilder(this, HistoryDatabase.class, "history.db")
                .build()
                .historyDao()
                .getFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<History>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(List<History> histories) {
                        HistoryAdapter adapter = new HistoryAdapter(histories);
                        adapter.setCallback(InputTipsActivity.this);
                        mTipList.setAdapter(adapter);
                        mProgressBar.setVisibility(View.GONE);
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

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mProgressBar.setVisibility(View.VISIBLE);

        String newText = s.toString().trim();
        InputtipsQuery inputQuery = new InputtipsQuery(newText, CITY);
        inputQuery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(InputTipsActivity.this, inputQuery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 输入提示结果的回调
     *
     * @param tipList tip列表
     * @param rCode   返回码
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        mProgressBar.setVisibility(View.GONE);

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
    public void onClickTip(final Tip tip) {

        Observable.create(new ObservableOnSubscribe<Tip>() {
            @Override
            public void subscribe(ObservableEmitter<Tip> emitter) {
                Room.databaseBuilder(InputTipsActivity.this, HistoryDatabase.class, "history.db")
                        .build()
                        .historyDao()
                        .insert(HistoryFactory.formTip(tip));
                emitter.onNext(tip);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Tip>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Tip t) {
                        sendTip(t);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onClickHistory(Tip tip) {
        sendTip(tip);
        finish();
    }

    private void sendTip(Tip tip) {
        if (mPurpose == 0) {
            EventBus.getDefault().post(tip);
        } else if (mPurpose == VALUE_ROUTE_START) {
            SearchEvent event = SearchEvent.builder()
                    .purpose(SearchEvent.Purpose.START)
                    .name(tip.getName())
                    .point(tip.getPoint())
                    .build();
            EventBus.getDefault().post(event);
        } else if (mPurpose == VALUE_ROUTE_END) {
            SearchEvent event = SearchEvent.builder()
                    .purpose(SearchEvent.Purpose.END)
                    .name(tip.getName())
                    .point(tip.getPoint())
                    .build();
            EventBus.getDefault().post(event);
        }
    }

    @Override
    public void onDeleteHistory(final History history) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(InputTipsActivity.this, HistoryDatabase.class, "history.db")
                        .build()
                        .historyDao()
                        .delete(history);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
