package com.alim.cse.noticebynu.Services;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.Database.OfflineData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {

    String Name;
    private String URL;
    private String type;
    private Context context;
    private Callbacks callbacks;

    public Downloader(Context context, String URL, String type, String Name) {
        this.context = context;
        this.type = type;
        this.Name = Name;
        this.URL = URL;
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private PowerManager.WakeLock mWakeLock;
        AppSettings appSettings = new AppSettings(context);
        OfflineData offlineData = new OfflineData(context);

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                Log.println(Log.ASSERT,"CON",connection.toString());

                File myDirectory = new File(Environment.getExternalStorageDirectory(), "/Notice by NU/"+type);
                if(!myDirectory.exists())
                    myDirectory.mkdir();

                if (appSettings.getAUTOSAVE()) {
                    int last = offlineData.getLast();
                    output = new FileOutputStream(myDirectory+"/"+last+"."+type);
                    offlineData.setNAME(Name);
                }
                else
                    output = new FileOutputStream(myDirectory+"/temp."+type);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                Log.println(Log.ASSERT,"URL",e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            callbacks.updateClient(false,progress[0],null);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            File file;
            if (appSettings.getAUTOSAVE()) {
                int last = offlineData.getLast()-1;
                file = new File("/sdcard/Notice by NU/" + type + "/" + last + "." + type);
            } else
                file = new File("/sdcard/Notice by NU/"+type+"/temp."+type);
            callbacks.updateClient(true,100,file);
        }
    }

    public interface Callbacks{
        void updateClient(boolean done, int pro ,File file);
    }

    public void registerClient(Activity activity){
        this.callbacks = (Callbacks) activity;
    }

}
