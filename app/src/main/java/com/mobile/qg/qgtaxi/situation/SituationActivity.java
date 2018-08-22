package com.mobile.qg.qgtaxi.situation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.entity.CurrentLatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mobile.qg.qgtaxi.constant.IntentConstant.KEY_SITUATION;
import static com.mobile.qg.qgtaxi.constant.IntentConstant.KEY_SITUATION_DETAIL;

/**
 * 异常
 */
public class SituationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SituationAdapter.OnPointClickListener {

    @BindView(R.id.rv_situation)
    RecyclerView mSituationRv;

    @BindView(R.id.sfl_refresh_situation)
    SwipeRefreshLayout swipeRefresh;

    private static final String TAG = "SituationActivity";
    private CurrentLatLng mLatLng;

    private SituationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situation);
        ButterKnife.bind(this);

        mLatLng = (CurrentLatLng) getIntent().getSerializableExtra(KEY_SITUATION);

        mSituationRv.setLayoutManager(new LinearLayoutManager(this));
        mSituationRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setRefreshing(true);
        swipeRefresh.setOnRefreshListener(this);

        requestSituation();

    }

    private void requestSituation() {

        SituationApi.getInstance().requestSituation(new Exception(mLatLng.getCurrentTime()), new SituationCallback() {
            @Override
            public void result(int status, final List<PointSet> pointSetList) {
                Log.e(TAG, "result: " + status + pointSetList);
                if (status == 2000 && pointSetList != null) {
                    Log.e(TAG, "result: " + pointSetList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mAdapter == null) {
                                mAdapter = new SituationAdapter(pointSetList);
                                mAdapter.setOnItemClickListener(SituationActivity.this);
                                mSituationRv.setAdapter(mAdapter);
                            } else {
                                mAdapter.refresh(pointSetList);
                            }

                            swipeRefresh.setRefreshing(false);

                        }
                    });

                }
            }
        });

    }

    @Override
    public void onRefresh() {
        requestSituation();
    }

    @Override
    public void onPointSet(PointSet pointSet) {
        Intent intent = new Intent(this, SituationDetailActivity.class);
        intent.putExtra(KEY_SITUATION_DETAIL, pointSet);
        startActivity(intent);
    }
}
