package com.example.downloadmanager.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.downloadmanager.Constants;
import com.example.downloadmanager.MainActivity;
import com.example.downloadmanager.R;



public class NotificationService extends Service {


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    private static final String TAG = "NotificationService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getIntExtra(Constants.PROGRESS, 0) == 100) {
                if (mNotifyManager != null) {
                    mBuilder.setContentText("Download completed")
                            .setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    stopSelf();
                }
            }
            if (mNotifyManager != null) {
                mBuilder.setProgress(100, intent.getIntExtra(Constants.PROGRESS, 0), false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }
    };

    @Override
    public void onCreate() {
        Intent pendingIntent = new Intent(this, MainActivity.class);
        pendingIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
        showNotification(this, "Download Manager", "Downloading...", pendingIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.UPDATE_UI));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotifyManager.cancel(id);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.UPDATE_UI));
        mNotifyManager = null;
    }


    public void showNotification(Context context, String title, String body, Intent intent) {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Download";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotifyManager.createNotificationChannel(mChannel);
        }

        mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        mNotifyManager.notify(notificationId, mBuilder.build());
    }
}
