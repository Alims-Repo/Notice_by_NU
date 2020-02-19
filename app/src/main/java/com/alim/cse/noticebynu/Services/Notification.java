package com.alim.cse.noticebynu.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Objects;

public class Notification  {


    private Context context;
    private String channelID,Name,Des;
    private PendingIntent pendingIntent = null;
    private android.app.Notification.Builder notification;
    private NotificationManager notificationManager;
    private NotificationManagerCompat notificationManager_compat;
    private NotificationCompat.Builder builder;

    Notification(Context context, String channelID, String Name, String Des) {
        this.Des = Des;
        this.Name = Name;
        this.context = context;
        this.channelID = channelID;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, Name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Des);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
            notification = new android.app.Notification.Builder(context, channelID);
        } else {
            notificationManager_compat = NotificationManagerCompat.from(context);
            builder = new NotificationCompat.Builder(context, channelID);
        }
    }

    /*void PushNotification(int id, String Title, String Content, int icon, int prog, PendingIntent pendingIntent
            , boolean progress, boolean indetermine, boolean ongoing, int style, boolean sound) {

        if (sound) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setContentTitle(Title)
                    .setContentText(Content)
                    .setSmallIcon(icon)
                    .setChannelId(channelID)
                    .setOngoing(ongoing)
                    .setContentIntent(pendingIntent)
                    .build();
            if (style==0)
                notification.setStyle(new android.app.Notification.BigTextStyle());
            if (progress)
                notification.setProgress(100,prog,indetermine);
            notificationManager.notify(id, notification.build());
        } else {
            builder.setContentTitle(Title)
                    .setContentText(Content)
                    .setSmallIcon(icon)
                    .setOngoing(ongoing);
            if (style==0)
                builder.setStyle(new NotificationCompat.BigTextStyle());
            if (progress)
                builder.setProgress(100,prog,indetermine);
            notificationManager_compat.notify(id, builder.build());
        }
    }

    void Cancel(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancel(id);
        } else {
            notificationManager_compat.cancel(id);
        }
    }*/
}
