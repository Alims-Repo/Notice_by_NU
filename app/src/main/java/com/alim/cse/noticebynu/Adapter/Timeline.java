package com.alim.cse.noticebynu.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alim.cse.noticebynu.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Timeline extends RecyclerView.Adapter<Timeline.MyViewHolder>  {

    TextView text;
    ArrayList<String> mDataset;

    public Timeline(ArrayList<String> mDataset) {
        this.mDataset = mDataset;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView textView;
        MyViewHolder(CardView v) {
            super(v);
            textView = v;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_layout_text, parent, false);
        text = v.findViewById(R.id.text);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.text.findViewById(R.id.text);
        text.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
