package com.alim.cse.noticebynu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alim.cse.noticebynu.Interfaces.FetchData;
import com.alim.cse.noticebynu.R;

import java.util.List;

public class Timeline extends RecyclerView.Adapter<Timeline.MyViewHolder>  {

    TextView text;
    TextView text_date;
    List<String> mDataset;
    List<String> mDataDate;
    List<String> mDataLink;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View view) {
            super(view);
        }
    }

    public Timeline(List<String> Data, List<String> Date, List<String> Link) {
        this.mDataset = Data;
        this.mDataDate = Date;
        this.mDataLink = Link;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final View rootView = holder.itemView;
        final Context context = holder.itemView.getContext();

        text = rootView.findViewById(R.id.text);
        text_date = rootView.findViewById(R.id.text_date);
        text.setText(mDataset.get(position));
        text_date.setText(mDataDate.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}