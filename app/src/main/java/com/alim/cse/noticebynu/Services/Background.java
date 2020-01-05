package com.alim.cse.noticebynu.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class Background extends Service {

    private AlarmManager serviceStarterAlarmManager = null;
    private MyTask asyncTask = null;
    private static final int FIRST_RUN_TIMEOUT_MILISEC = 5 * 1000;
    private static final int SERVICE_STARTER_INTERVAL_MILISEC = 1 * 1000;
    private static final int SERVICE_TASK_TIMEOUT_SEC = 10;
    private final int REQUEST_CODE = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceStarter();
        serviceTask();
        Toast.makeText(this, "Service Started!", Toast.LENGTH_LONG).show();
    }

    private void StopPerformingServiceTask() {
        asyncTask.cancel(true);
    }

    private void GoToDesktop() {
        Intent homeIntent= new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StopPerformingServiceTask();
        GoToDesktop();
    }

    private void serviceTask() {
        asyncTask = new MyTask();
        asyncTask.execute();
    }


    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (;;) {
                    TimeUnit.SECONDS.sleep(SERVICE_TASK_TIMEOUT_SEC);
                    if(isCancelled()) { break;}
                    publishProgress();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), "Ooops!!! Try to kill me :)", Toast.LENGTH_LONG).show();
        }
    }
    private void startServiceStarter() {
        Intent intent = new Intent(this, Starter.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, this.REQUEST_CODE, intent, 0);

        if (pendingIntent == null) {
            Toast.makeText(this, "Some problems with creating of PendingIntent", Toast.LENGTH_LONG).show();
        } else {
            if (serviceStarterAlarmManager == null) {
                serviceStarterAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                serviceStarterAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + FIRST_RUN_TIMEOUT_MILISEC,
                        SERVICE_STARTER_INTERVAL_MILISEC, pendingIntent);
            }
        }
    }
}
