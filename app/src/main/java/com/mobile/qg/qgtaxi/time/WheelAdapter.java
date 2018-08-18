package com.mobile.qg.qgtaxi.time;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.qg.qgtaxi.R;

import java.util.List;

/**
 * Created by 11234 on 2018/8/18.
 */
public final class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.ViewHolder> {

    private List<String> strings;
    private Context context;

    WheelAdapter(List<String> strings) {
        this.strings = strings;
    }

    public String getText(int position) {
        return strings.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_wheel, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String what = strings.get(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

}
