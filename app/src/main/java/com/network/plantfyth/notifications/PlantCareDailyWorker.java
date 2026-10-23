
package com.network.plantfyth.notifications;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class PlantCareDailyWorker extends Worker {

    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String KEY_RESET_FLAGS = "reset_flags";

    private static final String WORK_MORNING_PREFIX =
            "plant_care_morning_";
    private static final String WORK_EVENING_PREFIX =
            "plant_care_evening_";

    public PlantCareDailyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

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

        PlantFythAPI api = new RetroFitService()
                .getRetrofit()
                .create(PlantFythAPI.class);

        try {
            // Só a tarefa da manhã pode resetar as flags.
            boolean deveResetar = getInputData()
                    .getBoolean(KEY_RESET_FLAGS, false);

            if (deveResetar && !jaResetouHoje(context, usuarioId)) {

                Response<ResponseBody> resetResponse = api
                        .resetarFlagsDiarias(usuarioId)
                        .execute();

                if (!resetResponse.isSuccessful()) {
                    return Result.retry();
                }

                // Registra somente depois que a API confirmar o sucesso.
                registrarReset(context, usuarioId);
            }

            // Mantém o funcionamento atual das notificações.
            Response<List<Plantio>> response = api
                    .buscarPlantasUsuario(usuarioId)
                    .execute();

            if (!response.isSuccessful() || response.body() == null) {
                return Result.retry();
            }

            List<Plantio> plantas = response.body();

            PlantNotificationScheduler.schedulePlantCareAlarms(
                    context,
                    plantas
            );

            PlantNotificationScheduler.notifyDuePlants(
                    context,
                    plantas
            );

            return Result.success();

        } catch (IOException e) {
            return Result.retry();
        }
    }

    public static void scheduleDailyChecks(
            Context context,
            int usuarioId
    ) {
        Data morningData = new Data.Builder()
                .putInt(KEY_USUARIO_ID, usuarioId)
                .putBoolean(KEY_RESET_FLAGS, true)
                .build();

        Data eveningData = new Data.Builder()
                .putInt(KEY_USUARIO_ID, usuarioId)
                .putBoolean(KEY_RESET_FLAGS, false)
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
                .setInitialDelay(delayUntilHour(9), TimeUnit.MILLISECONDS)
                .build();

        PeriodicWorkRequest evening = new PeriodicWorkRequest.Builder(
                PlantCareDailyWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setInputData(eveningData)
                .setConstraints(constraints)
                .setInitialDelay(delayUntilHour(18), TimeUnit.MILLISECONDS)
                .build();

        WorkManager workManager = WorkManager.getInstance(
                context.getApplicationContext()
        );

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

    private boolean jaResetouHoje(Context context, int usuarioId) {
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

    private void registrarReset(Context context, int usuarioId) {
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
