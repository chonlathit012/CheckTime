package com.example.idont.checktime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by iDont on 19/9/2560.
 */

public class AlarmFinish extends BroadcastReceiver {

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "AlarmFinish");
        wakeLock.acquire(); //wake up the screen

        Intent intent1 = new Intent(context, SplashScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(intent1);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Check time !!!")
                        .setContentText("Please check-out")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1000, notification);


        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

    }
}
