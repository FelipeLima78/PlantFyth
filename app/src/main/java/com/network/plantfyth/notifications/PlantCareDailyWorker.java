
package com.network.plantfyth.notifications;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class PlantCareDailyWorker extends Worker {

    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String KEY_RESET_FLAGS = "reset_flags";

    private static final String WORK_MORNING_PREFIX =
            "plant_care_sun_morning_";
    private static final String LEGACY_WORK_MORNING_PREFIX =
            "plant_care_morning_";
    private static final String LEGACY_WORK_EVENING_PREFIX = "plant_care_evening_";
    private static final String LEGACY_LOCAL_CARE_REMINDER_PREFIX = "daily_care_reminder_local_";

    public PlantCareDailyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @NonNull
    @Override
    public Result doWork() {
        int usuarioId = getInputData().getInt(KEY_USUARIO_ID, -1);

        Context context = getApplicationContext();

        if (usuarioId == -1) {
            usuarioId = context
                    .getSharedPreferences(
                            "USER_DATA",
                            Context.MODE_PRIVATE
                    )
                    .getInt("usuario_id", -1);
        }

        if (usuarioId == -1) {
            return Result.success();
        }

        boolean deveResetar = getInputData().getBoolean(KEY_RESET_FLAGS, false);
        if (!deveResetar || jaResetouHoje(context, usuarioId)) {
            return Result.success();
        }

        PlantFythAPI api = new RetroFitService()
                .getRetrofit()
                .create(PlantFythAPI.class);

        try {
            Response<ResponseBody> resetResponse = api
                    .resetarFlagsDiarias(usuarioId)
                    .execute();

            if (!resetResponse.isSuccessful()) return Result.retry();

            registrarReset(context, usuarioId);

            return Result.success();

        } catch (IOException e) {
            return Result.retry();
        }
    }

    public static void scheduleDailyChecks(
            Context context,
            int usuarioId
    ) {
        androidx.work.Data morningData = new androidx.work.Data.Builder()
                .putInt(KEY_USUARIO_ID, usuarioId)
                .putBoolean(KEY_RESET_FLAGS, true)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest morning = new PeriodicWorkRequest.Builder(
                PlantCareDailyWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setInputData(morningData)
                .setConstraints(constraints)
                .setInitialDelay(delayUntilHour(7), TimeUnit.MILLISECONDS)
                .build();

        WorkManager workManager = WorkManager.getInstance(
                context.getApplicationContext()
        );

        // Remove o agendamento antigo das 9h em aparelhos que já tinham o app.
        workManager.cancelUniqueWork(LEGACY_WORK_MORNING_PREFIX + usuarioId);

        workManager.enqueueUniquePeriodicWork(
                WORK_MORNING_PREFIX + usuarioId,
                ExistingPeriodicWorkPolicy.KEEP,
                morning
        );

        workManager.cancelUniqueWork(LEGACY_WORK_EVENING_PREFIX + usuarioId);
        workManager.cancelUniqueWork(LEGACY_LOCAL_CARE_REMINDER_PREFIX + usuarioId);

        DailySunReminderWorker.scheduleDailyReminder(context, usuarioId);
    }

    public static boolean jaResetouHoje(Context context, int usuarioId) {
        String hoje = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        SharedPreferences prefs = context.getSharedPreferences(
                "PLANT_CARE_RESET",
                Context.MODE_PRIVATE
        );

        String ultimoReset = prefs.getString(
                "ultimo_reset_" + usuarioId,
                ""
        );

        return hoje.equals(ultimoReset);
    }

    public static void registrarReset(Context context, int usuarioId) {
        String hoje = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        context.getSharedPreferences(
                        "PLANT_CARE_RESET",
                        Context.MODE_PRIVATE
                )
                .edit()
                .putString("ultimo_reset_" + usuarioId, hoje)
                .apply();
    }

    private static long delayUntilHour(int hourOfDay) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.HOUR_OF_DAY, hourOfDay);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }

        return target.getTimeInMillis() - now.getTimeInMillis();
    }
}
