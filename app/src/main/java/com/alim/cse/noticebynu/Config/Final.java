package com.alim.cse.noticebynu.Config;

import android.annotation.SuppressLint;
import java.io.File;

@SuppressLint("SdCardPath")
public class Final {

    public static File Path() {
        return new File("/sdcard/Android/data/com.alim.cse.noticebynu");
    }

    public File ApkPath() {
        return new File("/sdcard/Android/data/com.alim.cse.noticebynu/Application/Notice by NU.apk");
    }

    public String APK_URL() {
        return "https://github.com/Hacker-0/Notice_by_NU/raw/master/app/release/app-release.apk";
    }

    public String URL() {
        return "https://raw.githubusercontent.com/Hacker-0/Notice_by_NU/master/app/release/output.json";
    }

    public static String LINK() {
        return "http://www.nu.ac.bd";
    }

    public static String HONS() {
        return "http://www.nu.ac.bd/syllabus-honours.php";
    }

    public static String PROFS() {
        return "http://www.nu.ac.bd/syllabus-professionals.php";
    }

    public static String DEGREE() {
        return  "http://www.nu.ac.bd/syllabus-degree-pass.php";
    }

    public static String MASTERS() {
        return "http://www.nu.ac.bd/syllabus-masters.php";
    }
}
