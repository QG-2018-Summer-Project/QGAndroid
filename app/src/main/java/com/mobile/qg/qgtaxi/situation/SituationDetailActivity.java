package com.mobile.qg.qgtaxi.situation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.qg.qgtaxi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mobile.qg.qgtaxi.constant.IntentConstant.KEY_SITUATION_DETAIL;

/**
 * 异常情况详情页的activity
 */
public class SituationDetailActivity extends AppCompatActivity {

    private static final String TAG = "SituationDetailActivity";
    private static final int INCREASE = 1;/*剧增*/
    private static final int DECREASE = 2;/*剧减*/
    private static final String[] textCase = {"数量骤增", "数量骤减"};

    private PointSet mPointSet;

    @BindView(R.id.iv_situation_rv_case)
    ImageView mIvCase;

    @BindView(R.id.tv_situation_rv_case)
    TextView mTvCase;

    @BindView(R.id.tv_situation_rv_location)
    TextView mTvLocation;

    @BindView(R.id.tv_situation_reason)
    TextView mTvReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situation_detail);
        ButterKnife.bind(this);

        mPointSet = (PointSet) getIntent().getSerializableExtra(KEY_SITUATION_DETAIL);
        initView();
    }

    private void initView() {
        switch (mPointSet.getType()) {
            case INCREASE:
                mIvCase.setImageResource(R.drawable.icon_increase);
                mTvCase.setText(textCase[0]);
                break;
            case DECREASE:
                mIvCase.setImageResource(R.drawable.icon_reduction);
                mTvCase.setText(textCase[1]);
                break;
        }

        String location = "位置：(" + mPointSet.getLon() + "," + mPointSet.getLat() + ")";
        mTvLocation.setText(location);

        String reason = "  " + mPointSet.getReason();
        mTvReason.setText(reason);

    }
}
