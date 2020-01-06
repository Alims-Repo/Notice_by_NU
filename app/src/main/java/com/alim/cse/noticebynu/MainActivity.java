package com.alim.cse.noticebynu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Fragment.NotificationFragment;
import com.alim.cse.noticebynu.Fragment.SettingsFragment;
import com.alim.cse.noticebynu.Fragment.TimelineFragment;
import com.alim.cse.noticebynu.Process.UIProcess;
import com.alim.cse.noticebynu.Services.Background;
import com.alim.cse.noticebynu.Services.Downloader;
import com.alim.cse.noticebynu.Services.Updater;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;

public class MainActivity extends AppCompatActivity implements Downloader.Callbacks, Updater.Callbacks {

    static int pos = 0;
    AppSettings appSettings;
    BottomNavigationView bottomNavigationView;
    Fragment fragment = null;
    TextView top;
    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSettings = new AppSettings(this);
        if (appSettings.getTHEME()==1) {
            setTheme(R.style.AppThemeDark);
        } else if (appSettings.getTHEME()==2) {
            setTheme(R.style.AppTheme);
        } else {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    setTheme(R.style.AppThemeDark);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    setTheme(R.style.AppTheme);
                    break;
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, Background.class));
        fragment = new TimelineFragment();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        menu = findViewById(R.id.menu);
        top = findViewById(R.id.app_bar_text);
        Downloader downloader = new Downloader(this,"http://www.nu.ac.bd","html");
        downloader.registerClient(this);
        //downloader.new DownloadTask().execute();

        switch (pos) {
            case 0:
                top.setText("Timeline");
                fragment = new TimelineFragment();
                bottomNavigationView.setSelectedItemId(R.id.timeline);
                break;
            case 1:
                top.setText("Notifications");
                fragment = new NotificationFragment();
                bottomNavigationView.setSelectedItemId(R.id.notification);
                break;
            case 2:
                top.setText("Settings");
                fragment = new SettingsFragment();
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.timeline:
                                fragment = new TimelineFragment();
                                top.setText("Timeline");
                                pos = 0;
                                break;
                            case R.id.notification:
                                fragment = new NotificationFragment();
                                top.setText("Notifications");
                                pos = 1;
                                break;
                            case R.id.settings:
                                fragment = new SettingsFragment();
                                top.setText("Settings");
                                pos = 2;
                                break;
                        }
                        FragmentStarter();
                        return true;
                    }
                });

        bottomNavigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                        //Do Noting Here...
                    }
                });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.about:
                                new UIProcess(MainActivity.this).ShowAbout();
                                break;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.top_menu);
                popup.show();
            }
        });
        FragmentStarter();
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

    @Override
    public void updateClient(int call) {

    }
}