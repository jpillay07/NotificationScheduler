package com.codelabs.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationJobService extends JobService {

    private static final int NOTIFICATION_ID = 0;
    NotificationManager mNotificationManageer;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        createNotificationChannel();

        Intent activityIntent = new Intent(this,MainActivity.class);

        PendingIntent intent = PendingIntent.getActivity(this, NOTIFICATION_ID, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.jobservice_notification_title))
                .setContentText(getString(R.string.jobservice_notification_text))
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        mNotificationManageer.notify(NOTIFICATION_ID, builder.build());

        return false;
    }

    private void createNotificationChannel() {

        mNotificationManageer = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "JobService Channel", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setDescription("Notifications from Job Service");

            mNotificationManageer.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
