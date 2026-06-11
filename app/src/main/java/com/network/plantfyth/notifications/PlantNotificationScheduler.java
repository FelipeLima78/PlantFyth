package com.network.plantfyth.notifications;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.network.plantfyth.MainActivity;
import com.network.plantfyth.R;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class PlantNotificationScheduler {

    public static final String EXTRA_PLANT_NAME = "plant_name";
    public static final String EXTRA_ACTION_NAME = "action_name";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    private static final String CHANNEL_ID = "plant_care_channel";
    private static final int SUMMARY_NOTIFICATION_ID = 8800;
    private static final int ACTION_IRRIGAR = 1;
    private static final int ACTION_PODAR = 2;

    private PlantNotificationScheduler() {
    }

    public static void refreshPlantCareAlarmsAndNotifyDue(Context context, int usuarioId, boolean notifyDueNow) {
        PlantFythAPI api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);
        api.buscarPlantasUsuario(usuarioId).enqueue(new Callback<List<Plantio>>() {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            @Override
            public void onResponse(@NonNull Call<List<Plantio>> call,
                                   @NonNull Response<List<Plantio>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                List<Plantio> plantas = response.body();
                schedulePlantCareAlarms(context, plantas);
                if (notifyDueNow) {
                    notifyDuePlants(context, plantas);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plantio>> call, @NonNull Throwable t) {
            }
        });
    }

    public static void schedulePlantCareAlarms(Context context, List<Plantio> plantas) {
        for (Plantio plantio : plantas) {
            schedulePlantCareAlarms(context, plantio);
        }
    }

    public static void schedulePlantCareAlarms(Context context, Plantio plantio) {
        Date irrigacao = parsePlantDate(plantio.getPrevisaoProximaIrrigacao());
        Date poda = parsePlantDate(plantio.getPrevisaoProximaPoda());

        if (irrigacao != null && irrigacao.after(new Date())) {
            scheduleAlarm(context, plantio, ACTION_IRRIGAR, "regar", irrigacao);
        }

        if (poda != null && poda.after(new Date())) {
            scheduleAlarm(context, plantio, ACTION_PODAR, "podar", poda);
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void notifyDuePlants(Context context, List<Plantio> plantas) {
        if (!canPostNotifications(context)) return;

        List<String> messages = new ArrayList<>();
        Date now = new Date();

        for (Plantio plantio : plantas) {
            Date irrigacao = parsePlantDate(plantio.getPrevisaoProximaIrrigacao());
            Date poda = parsePlantDate(plantio.getPrevisaoProximaPoda());
            String plantName = plantName(plantio);

            if (irrigacao != null && !irrigacao.after(now)) {
                messages.add("Regar: " + plantName);
            }

            if (poda != null && !poda.after(now)) {
                messages.add("Podar: " + plantName);
            }
        }

        if (messages.isEmpty()) return;

        createNotificationChannel(context);
        String title = "Plantas precisam de cuidado";
        String text = messages.size() == 1
                ? messages.get(0)
                : messages.size() + " cuidados pendentes";
        String bigText = joinMessages(messages);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openAppIntent(context));

        NotificationManagerCompat.from(context).notify(SUMMARY_NOTIFICATION_ID, builder.build());
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void showSingleCareNotification(Context context, int notificationId,
                                                  String plantName, String actionName) {
        if (!canPostNotifications(context)) return;

        createNotificationChannel(context);

        String safePlantName = plantName != null ? plantName : "sua planta";
        String safeActionName = actionName != null ? actionName : "cuidar";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Hora de " + safeActionName)
                .setContentText("A planta " + safePlantName + " precisa de cuidado.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openAppIntent(context));

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    private static void scheduleAlarm(Context context, Plantio plantio, int actionType,
                                      String actionName, Date date) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null || plantio.getId() == null) return;

        Intent intent = new Intent(context, PlantCareAlarmReceiver.class);
        intent.putExtra(EXTRA_PLANT_NAME, plantName(plantio));
        intent.putExtra(EXTRA_ACTION_NAME, actionName);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId(plantio.getId(), actionType));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId(plantio.getId(), actionType),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAt = date.getTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }

    private static PendingIntent openAppIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                9900,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Cuidados das plantas",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Avisos para regar e podar plantas");
        manager.createNotificationChannel(channel);
    }

    private static boolean canPostNotifications(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static Date parsePlantDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;

        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "dd/MM/yyyy"
        };

        for (String formatText : formats) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(formatText, Locale.US);
                format.setLenient(false);
                return format.parse(value);
            } catch (ParseException ignored) {
            }
        }

        return null;
    }

    private static String plantName(Plantio plantio) {
        String name = plantio.getNome();
        return name != null && !name.trim().isEmpty() ? name : "Planta sem nome";
    }

    private static int notificationId(int plantId, int actionType) {
        return plantId * 10 + actionType;
    }

    private static String joinMessages(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            if (i > 0) builder.append('\n');
            builder.append(messages.get(i));
        }
        return builder.toString();
    }
}
