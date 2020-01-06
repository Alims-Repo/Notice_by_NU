package com.alim.cse.noticebynu.Fragment;

import android.os.Bundle;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alim.cse.noticebynu.Adapter.Timeline;
import com.alim.cse.noticebynu.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class TimelineFragment extends Fragment {

    SwipeRefreshLayout refreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> mData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        for (int a=0;a<10;a++) {
                mData.add(getResources().getString(R.string.changelog)+a);
        }

        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        refreshLayout = rootView.findViewById(R.id.refresh);
        recyclerView = rootView.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new Timeline(mData);
        recyclerView.setVisibility(View.GONE);
        Shimmer(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                Shimmer(true);
            }
        });

        recyclerView.setAdapter(mAdapter);

        for (int a = 10;a<20;a++) {
            mData.add(getResources().getString(R.string.changelog)+a);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        },3000);

        return rootView;
    }

    private void Shimmer(final boolean refresh) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (refresh)
                    refreshLayout.setRefreshing(false);
            }
        },3000);
    }
}