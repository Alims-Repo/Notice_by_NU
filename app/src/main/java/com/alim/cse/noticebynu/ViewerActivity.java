package com.alim.cse.noticebynu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Process.PathFinder;
import com.alim.cse.noticebynu.Services.Downloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class ViewerActivity extends AppCompatActivity implements Downloader.Callbacks {

    static boolean night;
    String Link;
    String Name;
    Switch night_switch;
    AppSettings appSettings;
    FrameLayout frameLayout;
    ProgressBar progressBar;
    Downloader downloader;
    PDFView pdfView;
    ImageView back;
    static File location;
    static boolean run = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSettings = new AppSettings(this);
        if (!run) {
            run = true;
            if (appSettings.getTHEME()==1) {
                night = true;
                setTheme(R.style.AppThemeDark);
            } else if (appSettings.getTHEME()==2) {
                night = false;
                setTheme(R.style.AppTheme);
            } else {
                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        setTheme(R.style.AppThemeDark);
                        night = true;
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        setTheme(R.style.AppTheme);
                        night = false;
                        break;
                }
            }
        } else {
            if (night)
                setTheme(R.style.AppThemeDark);
            else
                setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        pdfView = findViewById(R.id.pdf_show);
        night_switch = findViewById(R.id.night);
        back = findViewById(R.id.back);
        frameLayout = findViewById(R.id.progress);
        progressBar = findViewById(R.id.progressbar);
        pdfView.setVisibility(View.GONE);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (night)
            night_switch.setChecked(true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        night_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                night = night_switch.isChecked();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(ViewerActivity.this, ViewerActivity.class);
                        i.putExtra("FROM","SELF");
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }, 200);
            }
        });
        
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            if (bundle.getString("FROM").equals("SELF")) {
                ViewPDF(location);
            } else {
                Log.println(Log.ASSERT,"bundle","Not Null");
                Link = bundle.getString("PDF_LINK");
                Name = bundle.getString("PDF_NAME");
                File file;
                if (bundle.getBoolean("OFFLINE")) {
                    file = new File(Link);
                    ViewPDF(file);
                } else {
                    downloader = new Downloader(this, Link,"pdf",Name);
                    downloader.registerClient(this);
                    downloader.new DownloadTask().execute();
                }
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            if ("application/pdf".equals(type)) {
                PathFinder realPath = new PathFinder();
                String text = intent.getData().toString();
                int pos = text.indexOf("/file%3A%2F%2F%2Fstorage%2Femulated%2F0%2F")+42;
                ViewPDF(new File(realPath.getPath(text.substring(pos))));
            }
        }
    }

    @Override
    public void updateClient(boolean done, int pro, File file) {
        if (done) {
            Log.println(Log.ASSERT,"LINK",file.toString());
            ViewPDF(file);
        } else {
            progressBar.setProgress(pro);
        }
    }

    private void ViewPDF(File file) {
        location = file;
        pdfView.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(0)
                .nightMode(night)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
    }
}