package com.alim.cse.noticebynu.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alim.cse.noticebynu.BuildConfig;
import com.alim.cse.noticebynu.Config.Final;
import com.alim.cse.noticebynu.ErrorActivity;
import com.alim.cse.noticebynu.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("SetTextI18n , StaticFieldLeak")
public class Updater {

    private Final config;
    private String BUG = null;
    private boolean push = true;
    private Notification notification;
    private Callbacks callbacks;
    private Context context;

    public Updater(Context context) {
        this.context = context;
        notification = new Notification(context,"Download",
                "Updater","Downloading Notification");
        config = new Final();
    }

    public class Version extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(config.URL());
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                Log.println(Log.ASSERT,"Exception",e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String VERSION_NAME = jsonObject.getJSONObject("apkData").getString("versionName");
                    int VERSION_CODE = jsonObject.getJSONObject("apkData").getInt("versionCode");
                    int versionCode = BuildConfig.VERSION_CODE;
                    if (VERSION_CODE> versionCode)
                        About(VERSION_NAME);
                    else  {
                        callbacks.updateClient(1);
                        Toast.makeText(context, "No Update available.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.println(Log.ASSERT,"JSONException",e.toString());
                }
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private PowerManager.WakeLock mWakeLock;

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(config.APK_URL());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                Log.println(Log.ASSERT, "SIZE","file_size = " + fileLength);
                input = connection.getInputStream();

                output = new FileOutputStream(config.ApkPath());

                byte[] data = new byte[4096];
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

                    Log.println(Log.ASSERT, "SIZE","TOTAL = " + total);
                    Log.println(Log.ASSERT, "SIZE","LENGTH = " + fileLength);
                    //Toast.makeText(context, String.valueOf(fileLength), Toast.LENGTH_SHORT).show();
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                notification.Cancel(100);
                BUG = "Exception : "+e.toString();
                Log.println(Log.ASSERT,"ERROR",e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    BUG = BUG+"\n"+"IOException : "+ignored.toString();
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
            File dir = new File(Environment.getDataDirectory().getPath()+"/Notice by NU/Application/");
            if(!dir.exists())
                dir.mkdirs();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            if (progress[0]==100) {
                notification.Cancel(100);
            } else if (push)
                ProgressNotification(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
            notification = new Notification(context
                    ,"Download","Updater","Downloading Notification");
            if (mWakeLock.isHeld())
                mWakeLock.release();
            if (result != null) {
                Intent intent = new Intent(context, ErrorActivity.class);
                intent.putExtra("ERROR",BUG);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
                notification.PushNotification(101,"Failed","Download error: "+result
                        ,R.drawable.ic_error_black_24dp,0,pendingIntent,false,false,false,1,true);
            } else {
                Intent intent = null;
                PendingIntent pendingIntent = null;
                try {
                    File PATH = new File("/sdcard/Notice by NU/Application/Notice by NU.apk");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", PATH);
                        intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(apkUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        Uri apkUri = Uri.fromFile(PATH);
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                } catch (Exception e) {}
                context.startActivity(intent);
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
                notification.PushNotification(101,"Downloaded","Click in update application."
                        ,R.drawable.ic_checked,0,pendingIntent,false,false,false,1,true);

            }
        }
    }

    private void ProgressNotification(int progress) {
        push = false;
        notification.PushNotification(100,"Downloading...","Downloading apk to update."
                , R.drawable.ic_file_download_black_24dp,progress, null,true,false,true,1,false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                push = true;
            }
        },1250);
    }

    public interface Callbacks{
        void updateClient(int call);
    }

    public void registerClient(Activity activity){
        this.callbacks = (Callbacks) activity;
    }

    private void About(String LatestVersion_Name) {
        Log.println(Log.ASSERT,"UIProcess", "got it");
        final Dialog dialog = new Dialog(context, R.style.AlertDialogLight);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.update_dialog);

        Button update_btn = dialog.findViewById(R.id.update_btn);
        TextView current_version = dialog.findViewById(R.id.current_version);
        TextView latest_version = dialog.findViewById(R.id.latest_version);

        String versionName = BuildConfig.VERSION_NAME;
        current_version.setText("Current Version : "+ versionName);
        latest_version.setText("Latest Version : "+LatestVersion_Name);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notification.PushNotification(100,"Downloading...","Downloading apk to update."
                                ,R.drawable.ic_file_download_black_24dp,0, null,true,true,true,1,true);
                    }
                    new DownloadTask().execute();
                } else
                    callbacks.updateClient(0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}