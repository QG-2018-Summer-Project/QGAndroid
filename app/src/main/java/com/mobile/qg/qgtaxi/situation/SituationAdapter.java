package com.mobile.qg.qgtaxi.situation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.qg.qgtaxi.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 93922 on 2018/8/19.
 * 描述：异常情况的RecyclerView的适配器
 */
public class SituationAdapter extends RecyclerView.Adapter<SituationAdapter.ViewHolder> {

    private static final int INCREASE = 1;/*剧增*/
    private static final int DECREASE = 2;/*剧减*/
    private static final String[] textCase = {"数量骤增", "数量骤减"};

    private Context mContext;
    private List<PointSet> mPointSets;
    private OnPointClickListener mClickListener;

    public interface OnPointClickListener {
        void onPointSet(PointSet pointSet);
    }

    SituationAdapter(List<PointSet> pointSets) {
        mPointSets = pointSets;
    }

    public void setOnItemClickListener(OnPointClickListener listener) {
        mClickListener = listener;
    }

    public void refresh(List<PointSet> pointSets) {
        mPointSets = pointSets;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_situation_rv_case)
        ImageView mIvCase;
        @BindView(R.id.tv_situation_rv_case)
        TextView mTvCase;
        @BindView(R.id.tv_situation_rv_location)
        TextView mTvLocation;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_situation, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onPointSet(mPointSets.get(holder.getAdapterPosition()));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        PointSet pointSet = mPointSets.get(position);
        switch (pointSet.getType()) {
            case INCREASE:
                holder.mIvCase.setImageResource(R.drawable.icon_increase);
                holder.mTvCase.setText(textCase[0]);
                break;
            case DECREASE:
                holder.mIvCase.setImageResource(R.drawable.icon_reduction);
                holder.mTvCase.setText(textCase[1]);
                break;
        }
        String location = "位置：(" + pointSet.getLon() + "," + pointSet.getLat() + ")";
        holder.mTvLocation.setText(location);
    }


    @Override
    public int getItemCount() {
        return mPointSets.size();
    }


}
