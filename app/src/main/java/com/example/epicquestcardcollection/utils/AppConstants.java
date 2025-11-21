package com.example.epicquestcardcollection.utils;

/**
 * Clase para almacenar constantes globales de la aplicación.
 * Centraliza todos los valores fijos para facilitar el mantenimiento.
 */
public class AppConstants {

    // Preferencias
    public static final String PREFS_NAME = "EpicQuestPrefs";
    public static final String KEY_CURRENT_USER = "current_user";
    public static final String KEY_USERS_DATA = "users_data";
    public static final String KEY_FIRST_TIME = "first_time";
    public static final String KEY_CURRENT_SESSION = "current_session";

    // Sistema de oportunidades
    public static final int DAILY_OPPORTUNITIES = 500; // deven ser 5 por dia
    public static final long OPPORTUNITY_COOLDOWN_MS = 5000; // 1 hora en milisegundos = 3600000

    // API
    public static final String SUPERHERO_API_BASE_URL = "https://superheroapi.com/api/";
    public static final String API_ACCESS_TOKEN = "497204daa803d1df886a17fd2485f1f4";

    // Navegación
    public static final int ONBOARDING_SCREEN_COUNT = 3;

    // Rarezas (SOLO UNA VEZ)
    public static final String RARITY_COMMON = "COMMON";
    public static final String RARITY_UNCOMMON = "UNCOMMON";
    public static final String RARITY_RARE = "RARE";
    public static final String RARITY_EPIC = "EPIC";
    public static final String RARITY_LEGENDARY = "LEGENDARY";

    // Validación de credenciales
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    private AppConstants() {
        // Clase no instanciable
    }
}
