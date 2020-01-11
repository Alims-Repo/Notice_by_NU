package com.alim.cse.noticebynu.Fragment.SubFragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alim.cse.noticebynu.Adapter.Updates;
import com.alim.cse.noticebynu.Config.Final;
import com.alim.cse.noticebynu.R;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavedSyllabus extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    static List<String> mData = new ArrayList<>();
    static List<String> mDate = new ArrayList<>();
    static List<String> mLink = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_syllabus, container, false);


        swipeRefreshLayout = rootView.findViewById(R.id.refresh);

        recyclerView = rootView.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new Updates(mData, mDate, mLink, true,"Syllabus");
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new TASK().execute();
            }
        });
        new TASK().execute();
        return rootView;

    }

    private class TASK extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mData.clear();
            mDate.clear();
            mLink.clear();
            String path = Final.Path() +"/pdf/Syllabus";
            File directory = new File(path);
            if (directory.exists()) {
                File[] folder = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                File[] files = directory.listFiles();
                for (File str : folder) {
                    File[] fold = str.listFiles();
                    for (File file : fold) {
                        try {
                            Log.println(Log.ASSERT,"FOLDER",file.toString());
                            String name = file.getName();
                            if (!name.equals("temp") | name.equals("")) {
                                Log.println(Log.ASSERT, "Name", name);
                                mData.add(name);
                                Date date = new Date(file.lastModified());
                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                String strDate = formatter.format(date);
                                mDate.add(strDate);
                                mLink.add(file.getPath());
                                Log.println(Log.ASSERT, "File Path", file.getPath());
                            }
                        } catch (Exception e) {
                            Log.println(Log.ASSERT, "Saved Fragment", e.toString());
                        }
                    }
                }
                for (File file : files) {
                    try {
                        int pos = file.getName().indexOf(".pdf");
                        String na = file.getName().substring(0, pos);
                        int n = Integer.parseInt(na);
                        String name = file.getName();
                        if (!name.equals("temp") | name.equals("")) {
                            Log.println(Log.ASSERT, "Name", name);
                            mData.add(name);
                            Date date = new Date(file.lastModified());
                            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                            String strDate = formatter.format(date);
                            mDate.add(strDate);
                            mLink.add(file.getPath());
                            Log.println(Log.ASSERT, "File Path", file.getPath());
                        }
                    } catch (Exception e) {
                        Log.println(Log.ASSERT, "Saved Fragment", e.toString());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }
}
