package com.alim.cse.noticebynu.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alim.cse.noticebynu.Adapter.Timeline;
import com.alim.cse.noticebynu.Config.Final;
import com.alim.cse.noticebynu.Interfaces.FetchData;
import com.alim.cse.noticebynu.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment{

    int pos = 40;
    int start, end;
    String WebData;
    Boolean scroll = false;
    FloatingActionButton top;
    SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView.Adapter mAdapter;
    static List<String> mData = new ArrayList<>();
    static List<String> mDate = new ArrayList<>();
    static List<String> mLink = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        top = rootView.findViewById(R.id.go_top);
        refreshLayout = rootView.findViewById(R.id.refresh);
        recyclerView = rootView.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new Timeline(mData, mDate, mLink);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setAdapter(mAdapter);
        if (mData.isEmpty())
            new ParseURL().execute(Final.LINK());
        else
            Shimmer();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                new ParseURL().execute(Final.LINK());
            }
        });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
                scroll = true;
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scroll & dy>0)
                    scroll = false;
                if (dy<0 & !scroll)
                    top.show();
                else
                    top.hide();
            }
        });

        return rootView;
    }

    public class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.println(Log.ASSERT, "HTML","Connecting to [" + strings[0] + "]");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(strings[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                Log.println(Log.ASSERT, "HTML","Connected");
                return EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                Log.println(Log.ASSERT, "ERROR", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    new GetArray().execute(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.println(Log.ASSERT,"JSONException",e.toString());
                }
            }
        }
    }

    private class GetArray extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... strings) {
            WebData = strings[0];
            try {
                for (int x=0;x<200;x++) {
                    start = WebData.indexOf("<tr>");
                    end = WebData.indexOf("</tr>",start)+5;
                    String table = WebData.substring(start,end);
                    int a = table.indexOf("uploads/");
                    int b = table.indexOf(".pdf",a)+4;
                    int c = table.indexOf("title=")+7;
                    int d = table.indexOf("\">",c);
                    int e = table.lastIndexOf("solid;\">")+8;
                    int f = table.lastIndexOf("</td>");
                    if (a<b & c<d & e<f) {
                        mLink.add("http://www.nu.ac.bd/"+table.substring(a,b));
                        mData.add(table.substring(c,d));
                        mDate.add(table.substring(e,f));
                    } else if (a>b){
                        b = table.indexOf(".txt",a);
                        Log.println(Log.ASSERT,"Value",a+" - "+b);
                        if (a<b) {
                            mLink.add("http://www.nu.ac.bd/"+table.substring(a,b));
                            mData.add(table.substring(c,d));
                            mDate.add(table.substring(e,f));
                        }
                    } else  {
                        mData.add("Error");
                        mDate.add("Error");
                        mLink.add("Error");
                    }
                    WebData = WebData.substring(end);
                }
                return mData;
            } catch (Exception e) {
                Log.println(Log.ASSERT, "ERROR", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    Shimmer();
                    Log.println(Log.ASSERT,"Response",response.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.println(Log.ASSERT,"JSONException",e.toString());
                }
            }
        }
    }
    private void Shimmer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

}