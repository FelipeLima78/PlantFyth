package com.network.plantfyth.notifications;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.util.Log;

import com.network.plantfyth.MainActivity;
import com.network.plantfyth.R;
import com.network.plantfyth.model.Plantio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public final class PlantNotificationScheduler {

    public static final String EXTRA_PLANT_NAME = "plant_name";
    public static final String EXTRA_ACTION_NAME = "action_name";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    private static final String CHANNEL_ID = "plant_care_channel";
    private static final int SUN_PENDING_NOTIFICATION_ID = 8801;
    private static final int DAILY_SUN_REMINDER_NOTIFICATION_ID = 8802;
    private static final int ACTION_IRRIGAR = 1;
    private static final int ACTION_PODAR = 2;
    private static final int ACTION_SOL = 3;
    private static final int ACTION_ADUBAR = 4;
    private static final String ALARM_PREFS = "PLANT_CARE_LOCAL_ALARMS";
    private static final String KEY_SCHEDULED_ALARMS = "scheduled_alarms";
    private static final String SUN_CACHE_PREFS = "PLANT_CARE_SUN_CACHE";

    private PlantNotificationScheduler() {
    }

    public static void schedulePlantCareAlarms(Context context, List<Plantio> plantas) {
        Set<Integer> activePlantIds = new HashSet<>();
        for (Plantio plantio : plantas) {
            if (plantio.getId() != null) activePlantIds.add(plantio.getId());
            schedulePlantCareAlarms(context, plantio);
        }

        Set<String> scheduledKeys = getScheduledAlarmKeys(context);
        for (String key : new HashSet<>(scheduledKeys)) {
            Integer plantId = plantIdFromAlarmKey(key);
            if (plantId != null && !activePlantIds.contains(plantId)) {
                cancelAlarm(context, plantId, actionTypeFromAlarmKey(key));
            }
        }
    }

    public static void schedulePlantCareAlarms(Context context, Plantio plantio) {
        Date irrigacao = parsePlantDate(plantio.getPrevisaoProximaIrrigacao());
        Date poda = parsePlantDate(plantio.getPrevisaoProximaPoda());
        Date adubacao = parsePlantDate(plantio.getPrevisao_proxima_adubacao());
        if (plantio.getId() == null) return;
        agendarOuCancelar(context, plantio, ACTION_IRRIGAR, "regar", irrigacao);
        agendarOuCancelar(context, plantio, ACTION_PODAR, "podar", poda);
        agendarOuCancelar(context, plantio, ACTION_ADUBAR, "adubar", adubacao);
    }

    public static void showSingleCareNotification(Context context, int notificationId,
                                                  String plantName, String actionName) {
        if (!canPostNotifications(context)) return;

        createNotificationChannel(context);

        String safePlantName = plantName != null ? plantName : "sua planta";
        String safeActionName = actionName != null ? actionName : "cuidar";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_leaf)
                .setContentTitle("Talvez seja hora de " + safeActionName)
                .setContentText("A planta " + safePlantName + " talvez precise de cuidado.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openAppIntent(context));

        postar(context, notificationId, builder.build());
    }

    public static void notifySunPending(Context context, List<Plantio> plantas) {
        boolean hasPlants = !plantas.isEmpty();
        boolean pending = false;
        for (Plantio plantio : plantas) {
            if (!plantio.isPegou_sol_hoje()) {
                pending = true;
                break;
            }
        }
        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        context.getSharedPreferences(SUN_CACHE_PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean("has_plants", hasPlants)
                .putBoolean("sun_pending", pending)
                .putString("snapshot_date", hoje)
                .apply();
        notifySunPendingFromCachedState(context);
    }

    public static void notifySunPendingFromCachedState(Context context) {
        SharedPreferences cache = context.getSharedPreferences(SUN_CACHE_PREFS, Context.MODE_PRIVATE);
        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean hasPlants = cache.getBoolean("has_plants", false);
        boolean pending = hasPlants && (!hoje.equals(cache.getString("snapshot_date", ""))
                || cache.getBoolean("sun_pending", true));

        if (!pending) {
            NotificationManagerCompat.from(context).cancel(SUN_PENDING_NOTIFICATION_ID);
            return;
        }
        if (!canPostNotifications(context)) return;
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_leaf)
                .setContentTitle("Confira a luz solar das suas plantas")
                .setContentText("Há plantas que ainda não receberam luz solar hoje.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(openAppIntent(context));
        postar(context, SUN_PENDING_NOTIFICATION_ID, builder.build());
    }

    public static void notifyDailySunReminder(Context context) {
        notifyDailySunReminder(context, DAILY_SUN_REMINDER_NOTIFICATION_ID);
    }

    public static void notifyDailySunReminder(Context context, int notificationId) {
        if (!canPostNotifications(context)) return;
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_leaf)
                .setContentTitle("Hora de conferir suas plantas")
                .setContentText("Verifique se suas plantas já receberam luz solar hoje.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(openAppIntent(context));
        postar(context, notificationId, builder.build());
    }

    public static void restorePlantCareAlarms(Context context) {
        SharedPreferences prefs = alarmPreferences(context);
        for (String key : new HashSet<>(getScheduledAlarmKeys(context))) {
            Integer plantId = plantIdFromAlarmKey(key);
            int actionType = actionTypeFromAlarmKey(key);
            long triggerAt = prefs.getLong(key, -1L);
            String plantName = prefs.getString(key + "_plant", "Planta sem nome");
            String actionName = prefs.getString(key + "_action", "cuidar");
            if (plantId == null || actionType == 0 || triggerAt < 0) {
                removerAlarmeSalvo(context, key);
            } else if (triggerAt <= System.currentTimeMillis()) {
                showSingleCareNotification(
                        context,
                        notificationId(plantId, actionType),
                        plantName,
                        actionName
                );
                removerAlarmeSalvo(context, key);
            } else {
                scheduleAlarm(context, plantId, actionType, plantName, actionName, triggerAt);
            }
        }
    }

    public static void clearScheduledAlarm(Context context, int notificationId) {
        int plantId = notificationId / 10;
        int actionType = notificationId % 10;
        removerAlarmeSalvo(context, alarmKey(plantId, actionType));
    }

    private static void agendarOuCancelar(Context context, Plantio plantio, int actionType,
                                          String actionName, Date date) {
        if (date != null && date.after(new Date())) {
            scheduleAlarm(context, plantio.getId(), actionType, plantName(plantio),
                    actionName, date.getTime());
        } else {
            cancelAlarm(context, plantio.getId(), actionType);
        }
    }

    private static void scheduleAlarm(Context context, int plantId, int actionType,
                                      String plantName, String actionName, long triggerAt) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, PlantCareAlarmReceiver.class);
        intent.putExtra(EXTRA_PLANT_NAME, plantName);
        intent.putExtra(EXTRA_ACTION_NAME, actionName);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId(plantId, actionType));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId(plantId, actionType),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        salvarAlarme(context, plantId, actionType, triggerAt, plantName, actionName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }

    private static void cancelAlarm(Context context, int plantId, int actionType) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PlantCareAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId(plantId, actionType),
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        removerAlarmeSalvo(context, alarmKey(plantId, actionType));
    }

    private static void salvarAlarme(Context context, int plantId, int actionType,
                                     long triggerAt, String plantName, String actionName) {
        String key = alarmKey(plantId, actionType);
        Set<String> keys = getScheduledAlarmKeys(context);
        keys.add(key);
        alarmPreferences(context).edit()
                .putLong(key, triggerAt)
                .putString(key + "_plant", plantName)
                .putString(key + "_action", actionName)
                .putStringSet(KEY_SCHEDULED_ALARMS, keys)
                .apply();
    }

    private static void removerAlarmeSalvo(Context context, String key) {
        Set<String> keys = getScheduledAlarmKeys(context);
        keys.remove(key);
        alarmPreferences(context).edit()
                .remove(key)
                .remove(key + "_plant")
                .remove(key + "_action")
                .putStringSet(KEY_SCHEDULED_ALARMS, keys)
                .apply();
    }

    private static SharedPreferences alarmPreferences(Context context) {
        return context.getSharedPreferences(ALARM_PREFS, Context.MODE_PRIVATE);
    }

    private static Set<String> getScheduledAlarmKeys(Context context) {
        Set<String> saved = alarmPreferences(context).getStringSet(KEY_SCHEDULED_ALARMS, null);
        return saved == null ? new HashSet<>() : new HashSet<>(saved);
    }

    private static String alarmKey(int plantId, int actionType) {
        return "alarm_" + plantId + "_" + actionType;
    }

    private static Integer plantIdFromAlarmKey(String key) {
        String[] parts = key.split("_");
        if (parts.length != 3 || !"alarm".equals(parts[0])) return null;
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static int actionTypeFromAlarmKey(String key) {
        String[] parts = key.split("_");
        if (parts.length != 3 || !"alarm".equals(parts[0])) return 0;
        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException ignored) {
            return 0;
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
        channel.setDescription("Avisos para regar, podar e adubar plantas");
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

    @SuppressLint("MissingPermission")
    private static void postar(Context context, int id, Notification notificacao) {
        if (!canPostNotifications(context)) {
            Log.w("PlantNotif", "Sem permissao POST_NOTIFICATIONS");
            return;
        }
        try {
            NotificationManagerCompat.from(context).notify(id, notificacao);
        } catch (SecurityException e) {
            Log.w("PlantNotif", "Notificacao bloqueada", e);
        }
    }


}
