package com.network.plantfyth.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;

public class PlantCareAlarmReceiver extends BroadcastReceiver {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        String plantName = intent.getStringExtra(PlantNotificationScheduler.EXTRA_PLANT_NAME);
        String actionName = intent.getStringExtra(PlantNotificationScheduler.EXTRA_ACTION_NAME);
        int notificationId = intent.getIntExtra(PlantNotificationScheduler.EXTRA_NOTIFICATION_ID, 0);

        PlantNotificationScheduler.showSingleCareNotification(
                context,
                notificationId,
                plantName,
                actionName
        );
    }
}
