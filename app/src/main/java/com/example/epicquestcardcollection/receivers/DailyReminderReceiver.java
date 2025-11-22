package com.example.epicquestcardcollection.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.ui.activities.DashboardActivity;

/**
 * Receiver simple para manejar recordatorios diarios
 * Se activa cuando suena la alarma programada
 */
public class DailyReminderReceiver extends BroadcastReceiver {

    // Constantes para la notificación
    private static final String CHANNEL_ID = "daily_reminders";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Mostrar notificación cuando se active la alarma
        showDailyReminderNotification(context);
    }

    private void showDailyReminderNotification(Context context) {
        // Crear canal de notificación (requerido para Android 8.0+)
        createNotificationChannel(context);

        // Intent para abrir la app al hacer click en la notificación
        Intent appIntent = new Intent(context, DashboardActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("¡Oportunidades Renovadas! 🃏")
                .setContentText("Tus oportunidades diarias están listas. ¡Ven a obtener cartas nuevas!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Crear canal de notificación solo para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Recordatorios Diarios";
            String description = "Notificaciones para recordar oportunidades diarias";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
