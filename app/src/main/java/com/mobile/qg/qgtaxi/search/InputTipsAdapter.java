package com.mobile.qg.qgtaxi.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.mobile.qg.qgtaxi.R;

import java.util.List;


/**
 * Created by 11234 on 2018/8/15.
 * 输入提示列表的适配器
 */
public class InputTipsAdapter extends RecyclerView.Adapter<InputTipsAdapter.ViewHolder> {

    private List<Tip> mTips;
    private Context mContext;

    public interface TipsCallback {
        void onClickTip(Tip tip);
    }

    private TipsCallback callback;

    public void setCallback(TipsCallback callback) {
        this.callback = callback;
    }

    InputTipsAdapter(List<Tip> mTips) {
        this.mTips = mTips;
    }

    public void refresh(List<Tip> tips) {
        mTips = tips;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tips, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onClickTip(mTips.get(holder.getAdapterPosition()));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tip tip = mTips.get(position);
        holder.mNameTv.setText(tip.getName());
        holder.mAddressTv.setText(tip.getAddress());
    }

    @Override
    public int getItemCount() {
        return mTips.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTv;
        TextView mAddressTv;

        ViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.poi_field_id);
            mAddressTv = itemView.findViewById(R.id.poi_value_id);
        }
    }

}
