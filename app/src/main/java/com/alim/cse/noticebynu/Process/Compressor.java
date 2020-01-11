package com.alim.cse.noticebynu.Process;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.alim.cse.noticebynu.Services.Downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Compressor {

    String Name;
    private File zipFile;
    private File last_file;
    private Callbacks callbacks;

    public Compressor(File Zip, String Save) {
        this .Name = Save;
        this.zipFile = Zip;
    }

    public class Unzip extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            long total_len = zipFile.length();
            long total_installed_len = 0;
            ZipInputStream zis = null;
            try {
                zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
                ZipEntry ze;
                int count;
                byte[] buffer = new byte[1024];
                while ((ze = zis.getNextEntry()) != null) {
                    total_installed_len += ze.getCompressedSize();
                    String file_name = ze.getName();
                    publishProgress((int)((total_installed_len * 100 / total_len)/2)+50);

                    File Save = new File(Environment.getExternalStorageDirectory(), "Android/data/com.alim.cse.noticebynu/pdf/"+Name);

                    File file = new File(Save, file_name);
                    File dir = ze.isDirectory() ? file : file.getParentFile();

                    last_file = file;

                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;
                    FileOutputStream fout = new FileOutputStream(file);
                    try
                    {
                        while ((count = zis.read(buffer)) != -1)
                            fout.write(buffer, 0, count);
                    } catch (IOException e) {
                        Log.println(Log.ASSERT,"IOException",e.toString());
                    } finally {
                        try {
                            fout.close();
                        } catch (IOException e) {
                            Log.println(Log.ASSERT,"IOException 2",e.toString());
                        }
                    }
                    long time = ze.getTime();
                    if (time > 0)
                        file.setLastModified(time);
                }
            } catch (IOException e) {
                Log.println(Log.ASSERT,"IOException 3",e.toString());
            } finally {
                try {
                    zis.close();
                } catch (IOException e) {
                    Log.println(Log.ASSERT,"IOException 4",e.toString());
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            callbacks.updateClient(false,values[0],null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callbacks.updateClient(true,100,last_file);
        }
    }

    public interface Callbacks{
        void updateClient(boolean done, int pro ,File file);
    }

    public void registerClient(Activity activity){
        this.callbacks = (Callbacks) activity;
    }
}
