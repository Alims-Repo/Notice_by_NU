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

import com.airbnb.lottie.utils.Logger;
import com.alim.cse.noticebynu.Adapter.Updates;
import com.alim.cse.noticebynu.Config.Final;
import com.alim.cse.noticebynu.Fragment.UpdatesFragment;
import com.alim.cse.noticebynu.Process.Compressor;
import com.alim.cse.noticebynu.R;
import com.alim.cse.noticebynu.Services.PushData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SyllabusProfessionals extends Fragment {

    int start, end;
    String WebData;
    Boolean scroll = false;
    ProgressBar progressBar;
    FloatingActionButton top;
    private RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView.Adapter mAdapter;
    static List<String> mData = new ArrayList<>();
    static List<String> mDate = new ArrayList<>();
    static List<String> mLink = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_syllabus_professionals, container, false);

        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        top = rootView.findViewById(R.id.go_top);
        progressBar = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new Updates(mData, mDate, mLink, false,"Syllabus");
        recyclerView.setVisibility(View.GONE);
        recyclerView.setAdapter(mAdapter);
        if (mData.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(10);
            final long FIVE_MEGABYTE = 1024 * 1024*3;
            StorageReference mountainsRef = storageRef.child("Professional.zip");
            mountainsRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(storageMetadata.getCreationTimeMillis());
                    int date =  Integer.parseInt(formatter.format(calendar.getTime()));
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                    int date_N = Integer.parseInt(sdf.format(currentTime));
                    if (date-date_N>4 | date_N-date>4)
                        new PushData(getActivity()).new ParseURL().execute(Final.PROFS(),"Professional.zip");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new PushData(getActivity()).new ParseURL().execute(Final.PROFS(),"Professional.zip");
                }
            });
            progressBar.setProgress(20);
            mountainsRef.getBytes(FIVE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    new GetArray().execute(new Compressor(bytes).Unzip());
                    progressBar.setProgress(70);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.println(Log.ASSERT,"FAILED", exception.toString());
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else
            Shimmer();

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

    private class GetArray extends AsyncTask<String, Integer, List<String>> {
        @Override
        protected List<String> doInBackground(String... strings) {
            WebData = strings[0];
            for (int x=0;x<200;x++) {
                try {
                    start = WebData.indexOf("<tr class=\"sectiontableentry");
                    end = WebData.indexOf("</tr>", start) + 5;
                    if (start<end) {
                        String Temp = WebData.substring(start,end);
                        if (Temp.contains("href=\"uploads")) {
                            int a = Temp.indexOf("href=\"uploads") + 6;
                            int b = 0;
                            if (Temp.contains(".pdf\">"))
                                b = Temp.indexOf(".pdf\">", a) + 4;
                            else if (Temp.contains(".zip\">"))
                                b = Temp.indexOf(".zip\">") + 4;
                            String title = Temp.substring(b + 2);
                            int c = title.indexOf("</a>");
                            int d = Temp.indexOf("date\">", b) + 6;
                            int e = Temp.indexOf("</td>", d);
                            mLink.add("http://www.nu.ac.bd/" + Temp.substring(a, b));
                            mData.add(title.substring(0, c));
                            mDate.add(Temp.substring(d, e));
                            //Log.println(Log.ASSERT,"TEMP",title.substring(0,c-4));
                            WebData = WebData.substring(end);
                        }
                    }
                } catch (Exception e){
                    Log.println(Log.ASSERT,"ex",e.toString());
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
                    Log.println(Log.ASSERT, "JSONException", e.toString());
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
    }

    private void SetError() {
        shimmerFrameLayout.stopShimmer();
        progressBar.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    public static String convertStreamToString(InputStream is) throws IOException {
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

    public static String getStringFromFile (String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
