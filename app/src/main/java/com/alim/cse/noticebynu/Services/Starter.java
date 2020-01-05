package com.alim.cse.noticebynu.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Starter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceLauncher = new Intent(context, Background.class);
        context.startService(serviceLauncher);
    }
}
