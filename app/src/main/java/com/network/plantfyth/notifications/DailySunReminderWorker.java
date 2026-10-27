package com.network.plantfyth.notifications;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/** Lembrete diário local; não acessa a API nem exige conexão com a internet. */
public class DailySunReminderWorker extends Worker {

    private static final String WORK_PREFIX = "daily_sun_reminder_";

    public DailySunReminderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        PlantNotificationScheduler.notifyDailySunReminder(getApplicationContext());
        return Result.success();
    }

    public static void scheduleDailyReminder(Context context, int usuarioId) {
        PeriodicWorkRequest reminder = new PeriodicWorkRequest.Builder(
                DailySunReminderWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setInitialDelay(delayUntilSevenAm(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        WORK_PREFIX + usuarioId,
                        ExistingPeriodicWorkPolicy.KEEP,
                        reminder
                );
    }

    private static long delayUntilSevenAm() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, 7);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1);
        return target.getTimeInMillis() - now.getTimeInMillis();
    }
}
