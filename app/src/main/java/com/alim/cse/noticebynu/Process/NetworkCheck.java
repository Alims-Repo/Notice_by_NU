package com.alim.cse.noticebynu.Process;

import android.app.Activity;
import android.util.Log;

import com.alim.cse.noticebynu.Services.Updater;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;

public class NetworkCheck {

    private Callbacks callbacks;
    public void hostAvailable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = "https://www.google.com";
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(URL);
                    HttpResponse response = httpClient.execute(httpGet);
                    callbacks.UpdateClient(true);
                } catch (IOException e) {
                    callbacks.UpdateClient(false);
                }
            }
        }).start();
    }

    public interface Callbacks{
        void UpdateClient(boolean call);
    }

    public void registerClient(Activity activity){
        this.callbacks = (Callbacks) activity;
    }

}