package com.network.plantfyth.notifications;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class PlantCareDailyWorker extends Worker {

    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String WORK_MORNING_PREFIX = "plant_care_morning_";
    private static final String WORK_EVENING_PREFIX = "plant_care_evening_";

    public PlantCareDailyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @NonNull
    @Override
    public Result doWork() {
        int usuarioId = getInputData().getInt(KEY_USUARIO_ID, -1);
        if (usuarioId == -1) {
            usuarioId = getApplicationContext()
                    .getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                    .getInt("usuario_id", -1);
        }
        if (usuarioId == -1) return Result.success();

        PlantFythAPI api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);

        try {
            Response<List<Plantio>> response = api.buscarPlantasUsuario(usuarioId).execute();
            if (!response.isSuccessful() || response.body() == null) return Result.retry();

            List<Plantio> plantas = response.body();
            PlantNotificationScheduler.schedulePlantCareAlarms(getApplicationContext(), plantas);
            PlantNotificationScheduler.notifyDuePlants(getApplicationContext(), plantas);
            return Result.success();
        } catch (IOException e) {
            return Result.retry();
        }
    }

    public static void scheduleDailyChecks(Context context, int usuarioId) {
        Data data = new Data.Builder()
                .putInt(KEY_USUARIO_ID, usuarioId)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest morning = new PeriodicWorkRequest.Builder(
                PlantCareDailyWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setInputData(data)
                .setConstraints(constraints)
                .setInitialDelay(delayUntilHour(9), TimeUnit.MILLISECONDS)
                .build();

        PeriodicWorkRequest evening = new PeriodicWorkRequest.Builder(
                PlantCareDailyWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setInputData(data)
                .setConstraints(constraints)
                .setInitialDelay(delayUntilHour(18), TimeUnit.MILLISECONDS)
                .build();

        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        workManager.enqueueUniquePeriodicWork(
                WORK_MORNING_PREFIX + usuarioId,
                ExistingPeriodicWorkPolicy.UPDATE,
                morning
        );
        workManager.enqueueUniquePeriodicWork(
                WORK_EVENING_PREFIX + usuarioId,
                ExistingPeriodicWorkPolicy.UPDATE,
                evening
        );
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
