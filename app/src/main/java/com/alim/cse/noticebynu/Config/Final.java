package com.alim.cse.noticebynu.Config;

import android.annotation.SuppressLint;
import java.io.File;

@SuppressLint("SdCardPath")
public class Final {

    public File Path() {
        return new File("/sdcard/Notice by NU/Application/Notice by NU.apk");
    }
    public String APK_URL() {
        return "https://github.com/Hacker-0/Notice_by_NU/raw/master/app/release/app-release.apk";
    }
    public String URL() {
        return "https://raw.githubusercontent.com/Hacker-0/Notice_by_NU/master/app/release/output.json";
    }
}