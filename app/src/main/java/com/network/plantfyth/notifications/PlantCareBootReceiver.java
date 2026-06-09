package com.network.plantfyth.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlantCareBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        int usuarioId = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                .getInt("usuario_id", -1);
        if (usuarioId == -1) return;

        PlantCareDailyWorker.scheduleDailyChecks(context, usuarioId);
        PlantNotificationScheduler.refreshPlantCareAlarmsAndNotifyDue(context, usuarioId, false);
    }
}
