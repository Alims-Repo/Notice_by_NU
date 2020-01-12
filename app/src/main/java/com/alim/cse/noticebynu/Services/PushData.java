package com.alim.cse.noticebynu.Services;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.alim.cse.noticebynu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class PushData {

    Dialog dialog;
    Context context;
    String LOCATION = "";
    ProgressBar progressBar;

    public PushData(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.AlertDialogLight);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.push_dialog);
        progressBar = dialog.findViewById(R.id.progress);
        dialog.show();
    }

    public class ParseURL extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.println(Log.ASSERT,"DO IN BACK","BACK");
            try {
                LOCATION = strings[1];
                publishProgress(0);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(strings[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                publishProgress(30);
                return EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                Log.println(Log.ASSERT,"DO IN BACK",e.toString());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    new SaveData().execute(response,LOCATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                dialog.dismiss();
        }
    }

    public class SaveData extends AsyncTask<String, Integer, String> {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        @Override
        protected String doInBackground(String... strings) {
            String temp = strings[0];
            try {
                int start = temp.indexOf("<table class=");
                int end = temp.lastIndexOf("</table>")+8;
                temp = temp.substring(start,end);
            } catch (Exception e) {
                Log.println(Log.ASSERT,"Exception",e.toString());
            }
            byte[] bytes = temp.getBytes();
            Log.println(Log.ASSERT, "BYTES", new String(bytes));
            StorageReference mountainsRef = storageRef.child(strings[1]);
            UploadTask uploadTask = mountainsRef.putBytes(bytes);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.println(Log.ASSERT, "TASK", exception.toString());
                    dialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.println(Log.ASSERT, "TASK", "DONE");
                    dialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    publishProgress((int) ((taskSnapshot.getBytesTransferred() * 100) /
                            taskSnapshot.getTotalByteCount()));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            Log.println(Log.ASSERT, "PROGRESS", values[0].toString());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
