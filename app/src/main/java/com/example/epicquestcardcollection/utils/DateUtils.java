package com.example.epicquestcardcollection.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase de utilidades para el manejo de fechas y tiempos en la aplicación.
 * Proporciona métodos para formatear y calcular diferencias de tiempo.
 */
public class DateUtils {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    /**
     * Convierte un timestamp en una cadena de hora legible
     */
    public static String formatTime(long timestamp) {
        return TIME_FORMAT.format(new Date(timestamp));
    }

    /**
     * Convierte un timestamp en una cadena de fecha legible
     */
    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    /**
     * Calcula el tiempo restante para la próxima oportunidad
     * @param lastCardTime Timestamp de la última carta obtenida
     * @return Tiempo restante en milisegundos, 0 si ya pasó el cooldown
     */
    public static long calculateRemainingCooldown(long lastCardTime) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastCardTime;
        long cooldownTime = AppConstants.OPPORTUNITY_COOLDOWN_MS;

        if (elapsedTime >= cooldownTime) {
            return 0;
        } else {
            return cooldownTime - elapsedTime;
        }
    }

    /**
     * Formatea el tiempo restante en una cadena legible (mm:ss)
     */
    public static String formatRemainingTime(long milliseconds) {
        if (milliseconds <= 0) {
            return "Listo";
        }

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * Verifica si ha pasado un día completo desde el último reset
     */
    public static boolean isNewDay(long lastResetTime) {
        long currentTime = System.currentTimeMillis();
        long oneDayInMs = 24 * 60 * 60 * 1000; // 24 horas en milisegundos

        return (currentTime - lastResetTime) >= oneDayInMs;
    }

    /**
     * Obtiene el timestamp de inicio del día actual (medianoche)
     */
    public static long getStartOfDay() {
        // Implementación simple - en una app real usaríamos Calendar o java.time
        return System.currentTimeMillis() / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000);
    }
}
