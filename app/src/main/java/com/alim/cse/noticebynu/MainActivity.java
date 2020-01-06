package com.alim.cse.noticebynu;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import com.alim.cse.noticebynu.Fragment.TimelineFragment;
import com.alim.cse.noticebynu.Services.Background;
import com.alim.cse.noticebynu.Services.Downloader;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;

public class MainActivity extends AppCompatActivity implements Downloader.Callbacks {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, Background.class));
        fragment = new TimelineFragment();
        FragmentStarter();
        Downloader downloader = new Downloader(this,"http://www.nu.ac.bd","html");
        downloader.registerClient(this);
        //downloader.new DownloadTask().execute();
    }

    private void FragmentStarter() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void updateClient(File file) {
        Snackbar.make(this.findViewById(R.id.main),
                Html.fromHtml("<font color=\"#ffffff\">Downloaded</font>"),
                Snackbar.LENGTH_INDEFINITE).show();
    }
}