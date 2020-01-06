package com.alim.cse.noticebynu.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

    private Context context;

    public AppSettings(Context Appcontext) {
        context = Appcontext;
    }

    private final String DATA_NAME = "APP_SETTINGS";
    private final String THEME = "THEME";
    private final String NOTIFICATION = "NOTIFICATION";

    public void setTHEME(int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(DATA_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(THEME, value);
        editor.apply();
    }

    public int getTHEME() {
        SharedPreferences prefs = context.getSharedPreferences(DATA_NAME, 0);
        return prefs.getInt(THEME,0);
    }

    public void setNOTIFICATION(Boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(DATA_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NOTIFICATION, value);
        editor.apply();
    }

    public boolean getNOTIFICATION() {
        SharedPreferences prefs = context.getSharedPreferences(DATA_NAME, 0);
        return prefs.getBoolean(NOTIFICATION,true);
    }
}
