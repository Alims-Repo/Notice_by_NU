package com.alim.cse.noticebynu.Process;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alim.cse.noticebynu.BuildConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTML {

    Callbacks callbacks;

    public class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.println(Log.ASSERT, "HTML","Connecting to [" + strings[0] + "]");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(strings[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                Log.println(Log.ASSERT, "HTML","Connected");
                return EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                Log.println(Log.ASSERT, "ERROR", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    //JSONArray jsonArray = new JSONArray(response);
                    //JSONObject jsonObject = jsonArray.getJSONObject(0);
                    callbacks.updateClient(response);
                    Log.println(Log.ASSERT,"Response",response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.println(Log.ASSERT,"JSONException",e.toString());
                }
            }
        }
    }

    public interface Callbacks{
        void updateClient(String call);
    }

    public void registerClient(Activity activity){
        this.callbacks = (Callbacks) activity;
    }
}
