package com.alim.cse.noticebynu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alim.cse.noticebynu.Config.Final;
import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Fragment.SavedFragment;
import com.alim.cse.noticebynu.Fragment.SettingsFragment;
import com.alim.cse.noticebynu.Fragment.SyllabusFragment;
import com.alim.cse.noticebynu.Fragment.UpdatesFragment;
import com.alim.cse.noticebynu.Services.Downloader;
import com.alim.cse.noticebynu.Services.Updater;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class MainActivity extends AppCompatActivity implements Updater.Callbacks {

    int click = 0;
    private RewardedAd rewardedAd;
    private InterstitialAd mInterstitialAd;
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
        MobileAds.initialize(this, "ca-app-pub-9098610474673834~6595925952");
        mInterstitialAd = new InterstitialAd(this);
        rewardedAd = new RewardedAd(this, "ca-app-pub-9098610474673834/8245238910");
        mInterstitialAd.setAdUnitId("ca-app-pub-9098610474673834/2254545634");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        LoadVideoAd();

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
                        if (click%5==0 & rewardedAd.isLoaded()) {
                            RewardedAdCallback adCallback = new RewardedAdCallback() {
                                @Override
                                public void onRewardedAdOpened() {
                                    // Ad opened.
                                }

                                @Override
                                public void onRewardedAdClosed() {
                                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                                        @Override
                                        public void onRewardedAdLoaded() {
                                            // Ad successfully loaded.
                                        }

                                        @Override
                                        public void onRewardedAdFailedToLoad(int errorCode) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LoadVideoAd();
                                                }
                                            },5000);
                                        }
                                    };
                                    rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                                }

                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                                }

                                @Override
                                public void onRewardedAdFailedToShow(int errorCode) {
                                    // Ad failed to display.
                                }
                            };
                            rewardedAd.show(MainActivity.this, adCallback);
                            click++;
                        } else
                            click++;
                        if (mInterstitialAd.isLoaded())
                            mInterstitialAd.show();
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

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Toast.makeText(MainActivity.this, "Clicked...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                },5000);
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

    private void LoadVideoAd() {
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }
}