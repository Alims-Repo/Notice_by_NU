package com.alim.cse.noticebynu.Fragment.SubFragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.airbnb.lottie.L;
import com.alim.cse.noticebynu.Adapter.Updates;
import com.alim.cse.noticebynu.Config.Final;
import com.alim.cse.noticebynu.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SyllabusDegree extends Fragment {

    int start, end;
    String WebData;
    Boolean scroll = false;
    ProgressBar progressBar;
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
        View rootView = inflater.inflate(R.layout.fragment_syllabus_degree, container, false);

        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        top = rootView.findViewById(R.id.go_top);
        refreshLayout = rootView.findViewById(R.id.refresh);
        progressBar = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new Updates(mData, mDate, mLink, false);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setAdapter(mAdapter);
        if (mData.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            new ParseURL().execute(Final.DEGREE());

            /*try {
                //new ParseURL().execute(Final.HONS());
                new GetArray().execute(getStringFromFile("/sdcard/Notice by NU/html/12.txt"));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            //new ParseURL().execute(Final.HONS());
        } else
            Shimmer();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                progressBar.setVisibility(View.VISIBLE);
                //new ParseURL().execute(Final.DEGREE());
            }
        });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
                scroll = true;
                top.hide();
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

    public class ParseURL extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                publishProgress(0);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(strings[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                publishProgress(30);
                return EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    new GetArray().execute(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                SetError();
        }
    }

    private class GetArray extends AsyncTask<String, Integer, List<String>> {
        @Override
        protected List<String> doInBackground(String... strings) {
            WebData = strings[0];
            for (int x=0;x<100;x++) {
                try {
                    start = WebData.indexOf("<tr class=\"sectiontableentry");
                    end = WebData.indexOf("</tr>", start) + 5;
                    if (start<end) {
                        String table = WebData.substring(start, end);
                        int b = 0;
                        if (table.contains("href=\"uploads")) {
                            int a = table.indexOf("href=\"uploads") + 6;
                            if (table.contains(".pdf"))
                                b = table.indexOf(".pdf") + 4;
                            else if (table.contains(".zip"))
                                b = table.indexOf(".zip") + 4;
                            int c = table.indexOf("</a>",b);
                            String title = table.substring(b+2, c);
                            Log.println(Log.ASSERT,"TABLE",table);
                            String Date = table.substring(c);
                            int d = Date.indexOf("<td class=\"jsn-table-column-date\">")+34;
                            //Date = Date.substring(d);
                            int e = Date.indexOf("</td>",d);
                            mLink.add("http://www.nu.ac.bd/" + table.substring(a, b));
                            mData.add(title);
                            mDate.add(Date.substring(d,e));
                            WebData = WebData.substring(end);
                        }
                    }
                } catch (Exception e){
                    //Log.println(Log.ASSERT,"ex",e.toString());
                }
            }
            return mData;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<String> response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    Shimmer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                SetError();
            }
        }
    }

    private void Shimmer() {
        shimmerFrameLayout.stopShimmer();
        progressBar.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

    private void SetError() {
        shimmerFrameLayout.stopShimmer();
        progressBar.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if(firstLine){
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
