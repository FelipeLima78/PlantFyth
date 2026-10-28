package com.network.plantfyth.notifications;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
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
    private static final String EVENING_WORK_PREFIX = "daily_sun_reminder_evening_";

    private static final int MORNING_REMINDER_HOUR = 8;
    private static final int EVENING_REMINDER_HOUR = 18;
    private static final String SCHEDULE_PREFS = "PLANT_CARE_NOTIFICATION_SCHEDULE";
    private static final String KEY_NOTIFICATION_ID = "notification_id";

    public DailySunReminderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        int notificationId = getInputData().getInt(KEY_NOTIFICATION_ID, 8802);
        PlantNotificationScheduler.notifyDailySunReminder(getApplicationContext(), notificationId);
        return Result.success();
    }

    public static void scheduleDailyReminder(Context context, int usuarioId) {
        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        android.content.SharedPreferences prefs = context.getSharedPreferences(
                SCHEDULE_PREFS,
                Context.MODE_PRIVATE
        );
        agendarLembrete(workManager, prefs, WORK_PREFIX + usuarioId,
                "sun_morning_hour_" + usuarioId, MORNING_REMINDER_HOUR);
        agendarLembrete(workManager, prefs, EVENING_WORK_PREFIX + usuarioId,
                "sun_evening_hour_" + usuarioId, EVENING_REMINDER_HOUR);
    }

    private static void agendarLembrete(WorkManager workManager,
                                       android.content.SharedPreferences prefs,
                                       String uniqueName, String preferenceKey, int hour) {
        Data data = new Data.Builder()
                .putInt(KEY_NOTIFICATION_ID, 8802)
                .build();
        PeriodicWorkRequest reminder = new PeriodicWorkRequest.Builder(
                DailySunReminderWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setInputData(data)
                .setInitialDelay(delayUntilHour(hour), TimeUnit.MILLISECONDS)
                .build();

        boolean horarioMudou = prefs.getInt(preferenceKey, -1) != hour;
        workManager.enqueueUniquePeriodicWork(
                uniqueName,
                horarioMudou ? ExistingPeriodicWorkPolicy.UPDATE : ExistingPeriodicWorkPolicy.KEEP,
                reminder
        );
        if (horarioMudou) prefs.edit().putInt(preferenceKey, hour).apply();
    }

    private static long delayUntilHour(int hourOfDay) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hourOfDay);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1);
        return target.getTimeInMillis() - now.getTimeInMillis();
    }
}
