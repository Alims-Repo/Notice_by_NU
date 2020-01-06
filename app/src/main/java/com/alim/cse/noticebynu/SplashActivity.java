package com.alim.cse.noticebynu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Process.Connectivity;
import com.alim.cse.noticebynu.Services.Updater;
import com.google.android.material.snackbar.Snackbar;

public class SplashActivity extends AppCompatActivity implements
        Updater.Callbacks, Connectivity.Callbacks {

    AppSettings appSettings;
    Connectivity networkCheck;
    Updater updater;
    int Storage_Perm = 4337;

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
        setContentView(R.layout.activity_splash);

        networkCheck = new Connectivity();
        updater = new Updater(this);
        networkCheck.registerClient(this);
        updater.registerClient(this);
        networkCheck.hostAvailable();
        //Will be deleted latter...
        //startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void updateClient(int TaskCode) {
        if (TaskCode==0) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Storage_Perm);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==Storage_Perm) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updater.new Version().execute();
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void UpdateClient(boolean call) {
        if (call) {
            updater.new Version().execute();
        }
        else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.println(Log.ASSERT,"Update Client","Knock");
                    Snackbar.make(findViewById(R.id.splash),
                            Html.fromHtml("<font color=\"#ffffff\">No internet connection</font>"),
                            Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            networkCheck.hostAvailable();
                        }
                    }).show();
                }
            }, 250);
        }
    }
}