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

import java.util.Objects;

public class ErrorActivity extends AppCompatActivity {

    String error_text;
    AppSettings appSettings;
    Button report;
    ImageView back;
    TextView error;
    Button copy;

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

        back = findViewById(R.id.back);
        error = findViewById(R.id.error);
        report = findViewById(R.id.bug);
        copy = findViewById(R.id.copy);
        Bundle bundle = getIntent().getExtras();
        
        if (bundle!=null) {
            error_text = Objects.requireNonNull(bundle).getString("ERROR", "");
            error.setText(error_text);
        }

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
}