package com.alim.cse.noticebynu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Services.Downloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import java.io.File;

public class ViewerActivity extends AppCompatActivity implements Downloader.Callbacks {

    boolean night;
    String Link;
    String Name;
    AppSettings appSettings;
    FrameLayout frameLayout;
    ProgressBar progressBar;
    Downloader downloader;
    PDFView pdfView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSettings = new AppSettings(this);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        pdfView = findViewById(R.id.pdf_show);
        back = findViewById(R.id.back);
        frameLayout = findViewById(R.id.progress);
        progressBar = findViewById(R.id.progressbar);
        pdfView.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            Link = bundle.getString("PDF_LINK");
            Name = bundle.getString("PDF_NAME");
            File file;
            if (bundle.getBoolean("OFFLINE")) {
                file = new File(Link);
                Log.println(Log.ASSERT,"LINK",file.toString());
                ViewPDF(file);
            } else {
                downloader = new Downloader(this, Link,"pdf",Name);
                downloader.registerClient(this);
                downloader.new DownloadTask().execute();
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