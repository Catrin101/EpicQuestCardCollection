package com.example.epicquestcardcollection.data.local;

import android.content.Context;
import com.example.epicquestcardcollection.model.User;
import com.example.epicquestcardcollection.utils.AppConstants;

/**
 * Singleton que gestiona la sesión del usuario en toda la aplicación.
 * Maneja el login, logout y estado actual de la sesión.
 */
public class SessionManager {
    private static SessionManager instance;
    private final SharedPreferencesHelper prefsHelper;
    private User currentUser;

    private SessionManager(Context context) {
        this.prefsHelper = new SharedPreferencesHelper(context, AppConstants.PREFS_NAME);
        loadCurrentUser();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // ==================== GESTIÓN DE SESIÓN ====================

    /**
     * Inicia sesión con un usuario
     */
    public void login(User user) {
        this.currentUser = user;
        prefsHelper.putObject(AppConstants.KEY_CURRENT_USER, user);
        prefsHelper.putString(AppConstants.KEY_CURRENT_SESSION, user.getUsername());
    }

    /**
     * Cierra la sesión actual preservando los datos del usuario
     */
    public void logout() {
        // Preservar los datos del usuario antes de cerrar sesión
        if (currentUser != null) {
            saveUserData(currentUser);
        }

        this.currentUser = null;
        prefsHelper.remove(AppConstants.KEY_CURRENT_USER);
        prefsHelper.remove(AppConstants.KEY_CURRENT_SESSION);
    }

    /**
     * Verifica si hay una sesión activa
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Obtiene el usuario actual
     */
    public User getCurrentUser() {
        return currentUser;
    }

    // ==================== GESTIÓN DE USUARIOS ====================

    /**
     * Guarda los datos de un usuario en el registro de usuarios
     */
    public void saveUserData(User user) {
        // TODO: En la siguiente iteración implementaremos el registro múltiple
        // Por ahora solo guardamos el usuario actual
        prefsHelper.putObject(user.getUsername(), user);
    }

    /**
     * Carga el usuario actual desde SharedPreferences
     */
    private void loadCurrentUser() {
        String currentUsername = prefsHelper.getString(AppConstants.KEY_CURRENT_SESSION, null);
        if (currentUsername != null) {
            this.currentUser = prefsHelper.getObject(currentUsername, User.class, null);
        }

        // Fallback: cargar directamente si no se encuentra por username
        if (this.currentUser == null) {
            this.currentUser = prefsHelper.getObject(AppConstants.KEY_CURRENT_USER, User.class, null);
        }
    }

    /**
     * Verifica si es la primera vez que se usa la app
     */
    public boolean isFirstTime() {
        return prefsHelper.getBoolean(AppConstants.KEY_FIRST_TIME, true);
    }

    /**
     * Marca que ya no es la primera vez que se usa la app
     */
    public void setFirstTimeCompleted() {
        prefsHelper.putBoolean(AppConstants.KEY_FIRST_TIME, false);
    }
}
