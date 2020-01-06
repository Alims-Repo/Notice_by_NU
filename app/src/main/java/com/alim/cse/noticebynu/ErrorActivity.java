package com.alim.cse.noticebynu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Process.HTML;
import com.alim.cse.noticebynu.Services.Downloader;

import java.io.File;
import java.util.Objects;

public class ErrorActivity extends AppCompatActivity implements HTML.Callbacks {

    String error_text;
    AppSettings appSettings;
    TextView error;
    Button report;
    ImageView back;
    Button copy;

    String URL = "https://www.facebook.com/";
    //String URL="http://www.nu.ac.bd/recent-news-notice.php";
    HTML html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSettings = new AppSettings(this);
        if (appSettings.getTHEME() == 1) {
            setTheme(R.style.AppThemeDark);
        } else if (appSettings.getTHEME() == 2) {
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
        setContentView(R.layout.activity_error);

        html = new HTML();
        html.registerClient(this);

        error = findViewById(R.id.error);
        back = findViewById(R.id.back);
        report = findViewById(R.id.bug);
        copy = findViewById(R.id.copy);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            error_text = Objects.requireNonNull(bundle).getString("ERROR", "");
            error.setText(error_text);
        }

        html.new ParseURL().execute(URL);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Error", error_text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ErrorActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void updateClient(String call) {
        error.setText(call);
    }
}