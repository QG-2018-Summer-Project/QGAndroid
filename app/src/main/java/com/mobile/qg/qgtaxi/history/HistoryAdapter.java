package com.mobile.qg.qgtaxi.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.mobile.qg.qgtaxi.R;

import java.util.List;

import static com.mobile.qg.qgtaxi.history.HistoryFactory.fromHistory;

/**
 * Created by 11234 on 2018/8/15.
 * 历史纪录列表的适配器
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> mHistories;
    private Context mContext;

    public interface HistoriesCallback {
        void onClickHistory(Tip tip);

        void onDeleteHistory(History history);
    }

    private HistoriesCallback callback;

    public void setCallback(HistoriesCallback callback) {
        this.callback = callback;
    }

    public HistoryAdapter(List<History> mHistories) {
        this.mHistories = mHistories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onClickHistory(fromHistory(mHistories.get(holder.getAdapterPosition())));
                }
            }
        });

        holder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    History history = mHistories.get(holder.getAdapterPosition());
                    mHistories.remove(history);
                    notifyDataSetChanged();
                    callback.onDeleteHistory(history);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = mHistories.get(position);
        holder.mNameTv.setText(history.getName());
        holder.mAddressTv.setText(history.getAddress());
    }

    @Override
    public int getItemCount() {
        return mHistories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTv;
        TextView mAddressTv;
        ImageView mDeleteIv;

        ViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.history_name);
            mAddressTv = itemView.findViewById(R.id.history_address);
            mDeleteIv = itemView.findViewById(R.id.history_delete);
        }
    }

}
