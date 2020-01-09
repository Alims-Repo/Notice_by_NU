package com.alim.cse.noticebynu.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class OfflineData {

    private Context context;

    public OfflineData(Context Appcontext) {
        context = Appcontext;
    }

    private final String NAME = "NAME";
    private final String LAST = "LAST";
    private final String DATA_NAME = "OFFLINE_DATA";

    public void setNAME(String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(DATA_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME+getLast(), value);
        setLast(getLast()+1);
        editor.apply();
    }

    public String getNAME(int value) {
        SharedPreferences prefs = context.getSharedPreferences(DATA_NAME, 0);
        return prefs.getString(NAME+value,"");
    }

    private void setLast(int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(DATA_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(LAST, value);
        editor.apply();
    }

    public int getLast() {
        SharedPreferences prefs = context.getSharedPreferences(DATA_NAME, 0);
        return prefs.getInt(LAST,0);
    }
}
