package com.example.epicquestcardcollection.data.repository;

import com.example.epicquestcardcollection.base.data.Result;
import com.example.epicquestcardcollection.model.User;

/**
 * Contrato que define las operaciones disponibles para la gestión de usuarios.
 * Sigue el patrón Repository para abstraer el origen de datos.
 */
public interface UserRepository {
    /**
     * Callback para operaciones asíncronas.
     * @param <T>
     */
    interface Callback<T> {
        void onResult(Result<T> result);
    }

    /**
     * Registra un nuevo usuario en el sistema
     */
    void registerUser(String username, String password, String email, Callback<User> callback);

    /**
     * Autentica un usuario con sus credenciales
     */
    void loginUser(String username, String password, Callback<User> callback);

    /**
     * Cierra la sesión del usuario actual
     */
    void logoutUser(Callback<Void> callback);

    /**
     * Obtiene el usuario actualmente logueado
     */
    void getCurrentUser(Callback<User> callback);

    /**
     * Actualiza los datos de un usuario
     */
    void updateUser(User user, Callback<User> callback);

    /**
     * Verifica si un username ya está en uso
     */
    void isUsernameTaken(String username, Callback<Boolean> callback);

    /**
     * Verifica si hay una sesión activa
     */
    void isUserLoggedIn(Callback<Boolean> callback);

    /**
     * Resetea las oportunidades diarias del usuario actual (para testing)
     */
    void resetDailyOpportunities(Callback<Void> callback);
}
