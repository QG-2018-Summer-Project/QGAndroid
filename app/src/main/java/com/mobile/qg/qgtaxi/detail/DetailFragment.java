package com.mobile.qg.qgtaxi.detail;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.amap.api.maps.model.Poi;
import com.mobile.qg.qgtaxi.R;

/**
 * Created by 11234 on 2018/7/27.
 * 点击兴趣点后的底部Fragment
 */
public class DetailFragment extends DialogFragment {

    public interface OnBottomItemClickListener {
        void onChart();

        void onRoute();
    }

    private OnBottomItemClickListener mItemClickListener;
    private Poi mPoi;

    public DetailFragment poi(Poi poi) {
        this.mPoi = poi;
        return this;
    }

    public DetailFragment listener(OnBottomItemClickListener listener) {
        mItemClickListener = listener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        int color = ContextCompat.getColor(getActivity(), android.R.color.transparent);
        window.setBackgroundDrawable(new ColorDrawable(color));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.widget_bottom_dialog, container, false);
        startUpAnimation(view);

        ((TextView) view.findViewById(R.id.tv_name)).setText(mPoi.getName());
        view.findViewById(R.id.cd_best).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onRoute();
            }
        });

        view.findViewById(R.id.cd_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onChart();
                }
            }
        });

        return view;
    }

    /**
     * 升起动画
     *
     * @param view view
     */
    private void startUpAnimation(View view) {
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(200);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
    }

    /**
     * 降下动画
     *
     * @param view view
     */
    private void startDownAnimation(View view) {
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        slide.setDuration(200);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(slide);
    }

}
