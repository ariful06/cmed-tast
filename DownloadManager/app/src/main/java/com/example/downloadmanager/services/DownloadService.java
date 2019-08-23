package com.example.downloadmanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.downloadmanager.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {

    public String fileName = "filename";

    public String fileURL = "http://dropbox.sandbox2000.com/intrvw/SampleVideo_1280x720_30mb.mp4";
    //    public String fileURL = "http://i.ytimg.com/vi/5LI2/maxresdefault.jpg";
    File myDir;

    private static final String TAG = "DownloadService";
    String root;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        checkAndCreateDirectory("/mydirectory");
        new DownloadFileAsync().execute(fileURL);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... aurl) {

            try {
                URL u = new URL(fileURL);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                int lenghtOfFile = c.getContentLength();
                FileOutputStream f = new FileOutputStream(new File(myDir, fileName));
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;

                while ((len1 = in.read(buffer)) > 0) {
                    total += len1; //total = total + len1
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    f.write(buffer, 0, len1);
                }
                f.close();


            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d(TAG, progress[0]);
            Intent intent = new Intent(Constants.UPDATE_UI);
            Bundle bundle = new Bundle();
            bundle.putInt("progress", Integer.parseInt(progress[0]));
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
            if (Integer.parseInt(progress[0]) == 100) {
                stopSelf();
            }
        }

    }

    public void checkAndCreateDirectory(String dirName) {
        root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + dirName);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
    }
}
