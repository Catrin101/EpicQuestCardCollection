package com.example.epicquestcardcollection.firebase;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Helper para gestionar la autenticación con Firebase
 * Maneja registro, login, logout y estado del usuario
 */
public class FirebaseAuthHelper {

    private static final String TAG = "FirebaseAuthHelper";
    private final FirebaseAuth mAuth;

    // Interface para callbacks
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String errorMessage);
    }

    public FirebaseAuthHelper() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Registra un nuevo usuario con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña (mínimo 6 caracteres)
     * @param callback Callback con el resultado
     */
    public void registerUser(String email, String password, AuthCallback callback) {
        Log.d(TAG, "Intentando registrar usuario: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "✅ Usuario registrado: " + user.getUid());
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error desconocido";
                        Log.e(TAG, "❌ Error al registrar: " + error);
                        callback.onError(error);
                    }
                });
    }

    /**
     * Inicia sesión con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña
     * @param callback Callback con el resultado
     */
    public void loginUser(String email, String password, AuthCallback callback) {
        Log.d(TAG, "Intentando login: " + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "✅ Login exitoso: " + user.getUid());
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error desconocido";
                        Log.e(TAG, "❌ Error al hacer login: " + error);
                        callback.onError(error);
                    }
                });
    }

    /**
     * Obtiene el usuario actualmente autenticado
     * @return FirebaseUser o null si no hay sesión
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Verifica si hay un usuario con sesión activa
     * @return true si hay sesión activa
     */
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Cerrando sesión de: " + currentUser.getUid());
            mAuth.signOut();
            Log.d(TAG, "✅ Sesión cerrada");
        }
    }

    /**
     * Obtiene el UID del usuario actual
     * @return UID o null si no hay sesión
     */
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * Obtiene el email del usuario actual
     * @return Email o null si no hay sesión
     */
    public String getCurrentUserEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }
}
