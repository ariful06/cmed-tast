package com.example.downloadmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloadmanager.services.DownloadService;
import com.example.downloadmanager.services.NotificationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //initialize our progress dialog/bar
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    Button button;

    public static int progress = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting some display
        setContentView(R.layout.activity_main);
        requestPermission();
        if (getIntent().hasExtra(Constants.IS_FROM_NOTIFICATION)){
            if ((getIntent().getExtras()).getBoolean(Constants.IS_FROM_NOTIFICATION) == true){
                stopService(new Intent(this,NotificationService.class));
            }
        }

        TextView tv = new TextView(this);
        tv.setText("Download file ");
        button = findViewById(R.id.download);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("update_ui"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (checkPermission()){
                   showDialog(DIALOG_DOWNLOAD_PROGRESS);
                   Intent intent = new Intent(MainActivity.this, DownloadService.class);
                   startService(intent);
                }
            }
        });
        if (mProgressDialog == null){
            if(isMyServiceRunning(DownloadService.class)){
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file…");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(progress);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);

            }
        }
        checkNotificationService();
    }

    private void checkNotificationService() {
        if (isMyServiceRunning(NotificationService.class)){
            stopService(new Intent(MainActivity.this,NotificationService.class));
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file…");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    protected void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks for the permission", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationService();
        final IntentFilter intentFilter = new IntentFilter("update_ui");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);
        if (mProgressDialog != null){
            if (!mProgressDialog.isShowing()){
                mProgressDialog.setProgress(progress);
                mProgressDialog.show();
            }
        }else{
            if(isMyServiceRunning(DownloadService.class)){
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgress(progress);
                mProgressDialog.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            progress = intent.getIntExtra("progress",0);
            if (mProgressDialog != null){
                mProgressDialog.setProgress(progress);
                if (progress == 100){
                    Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                    Intent lintent = new Intent(MainActivity.this, DownloadService.class);
                    context.stopService(lintent);
                    mProgressDialog.dismiss();
                }

            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, NotificationService.class));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}