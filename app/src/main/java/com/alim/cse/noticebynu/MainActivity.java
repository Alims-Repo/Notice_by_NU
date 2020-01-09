package com.alim.cse.noticebynu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Fragment.SavedFragment;
import com.alim.cse.noticebynu.Fragment.SettingsFragment;
import com.alim.cse.noticebynu.Fragment.SyllabusFragment;
import com.alim.cse.noticebynu.Fragment.UpdatesFragment;
import com.alim.cse.noticebynu.Services.Updater;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements Updater.Callbacks {

    static int pos = 0;
    AppSettings appSettings;
    BottomNavigationView bottomNavigationView;
    Fragment fragment = null;

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

        //startService(new Intent(this, Background.class));
        fragment = new UpdatesFragment();
        bottomNavigationView = findViewById(R.id.bottom_nav);

        switch (pos) {
            case 0:
                fragment = new UpdatesFragment();
                bottomNavigationView.setSelectedItemId(R.id.updates);
                break;
            case 1:
                fragment = new SyllabusFragment();
                bottomNavigationView.setSelectedItemId(R.id.syllabus);
                break;
            case 2:
                fragment = new SavedFragment();
                bottomNavigationView.setSelectedItemId(R.id.saved);
                break;
            case 3:
                fragment = new SettingsFragment();
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.updates:
                                fragment = new UpdatesFragment();
                                pos = 0;
                                break;
                            case R.id.syllabus:
                                fragment = new SyllabusFragment();
                                pos = 1;
                                break;
                            case R.id.saved:
                                fragment = new SavedFragment();
                                pos = 2;
                                break;
                            case R.id.settings:
                                fragment = new SettingsFragment();
                                pos = 3;
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

        FragmentStarter();
    }

    private void FragmentStarter() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void updateClient(int call) {

    }
}