package com.alim.cse.noticebynu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Process.Decompressor;
import com.alim.cse.noticebynu.Process.PathFinder;
import com.alim.cse.noticebynu.Services.Downloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.mittsu.markedview.MarkedView;
import java.io.File;

public class ViewerActivity extends AppCompatActivity implements Downloader.Callbacks, Decompressor.Callbacks {

    TextView loading_text;
    MarkedView mdView;
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
        mdView = findViewById(R.id.md_view);
        loading_text = findViewById(R.id.loading_text);

        pdfView.setVisibility(View.GONE);
        mdView.setVisibility(View.GONE);

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
            if (bundle.getString("FROM","").equals("SELF")) {
                if (location.toString().contains(".pdf"))
                    ViewPDF(location);
                else if (location.toString().contains(".text"))
                    TextVIEW(location);
            } else if (bundle.getString("FROM","").equals("OTHER")) {
                String TYPE = bundle.getString("TYPE","");
                Link = bundle.getString("LINK");
                Name = bundle.getString("NAME");
                File file;
                if (bundle.getBoolean("OFFLINE")) {
                    file = new File(Link);
                    ViewPDF(file);
                } else {
                    String From = bundle.getString("LOCATION");
                    downloader = new Downloader(this, Link,TYPE,Name,From);
                    downloader.registerClient(this);
                    downloader.new DownloadTask().execute();
                }
            } else {
                try {
                    Uri uri = intent.getData();
                    Log.println(Log.ASSERT,"URI",uri.toString());
                    //File file =  new File(PathFinder.getPath(this, uri));
                    File file = new File(PathFinder.getPath(this,uri));
                    Log.println(Log.ASSERT,"FILE",file.toString());
                    ViewPDF(file);
                } catch (Exception e) {
                    Log.println(Log.ASSERT,"Exception", e.toString());
                }
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            if ("application/pdf".equals(type)) {
                Uri path = intent.getData();
                //File file =  new File(PathFinder.getPath(this, path));
                File file = new File(PathFinder.getPath(this, path));
                Log.println(Log.ASSERT,"URI",path.toString());
                Log.println(Log.ASSERT,"FILE",file.toString());
                try {
                    ViewPDF(file);
                } catch (Exception e) {

                }
            } else if ("text/plain".equals(type)) {
                Uri path = intent.getData();
                //File file =  new File(PathFinder.getPath(this, path));
                File file = new File(PathFinder.getPath(this, path));
                Log.println(Log.ASSERT,"URI",path.toString());
                Log.println(Log.ASSERT,"FILE",file.toString());
                try {
                    TextVIEW(file);
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public void updateClient(String type, boolean done, int pro, File file) {
        if (done) {
            if (type.equals("pdf"))
                ViewPDF(file);
            else if (type.equals("txt"))
                TextVIEW(file);
            else {
                Bundle bundle = getIntent().getExtras();
                String From = bundle.getString("LOCATION");
                loading_text.setText("Decompressing ZIP...");
                Decompressor compressor = new Decompressor(file,From);
                compressor.registerClient(this);
                compressor.new Unzip().execute();
            }
        } else {
            progressBar.setProgress(pro);
        }
    }

    private void TextVIEW(File file) {
        frameLayout.setVisibility(View.GONE);
        mdView.setVisibility(View.VISIBLE);
        mdView.loadMDFile(file);
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

    @Override
    public void updateClient(boolean done, int pro, File file) {
        if (done) {
            String name = file.toString();
            name = name.substring(name.length()-4);
            if (name.equals("docx")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                    Log.println(Log.ASSERT,"FILE",uri.toString());
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(intent, "Open Word document"));
                    } catch (Exception e) {
                        Log.println(Log.ASSERT,"UPDATES",e.toString());
                    }
                } catch (Exception e) {
                    Log.println(Log.ASSERT,"UPDATES",e.toString());
                }
            }
            ViewPDF(file);
        } else
            progressBar.setProgress(pro);
    }
}