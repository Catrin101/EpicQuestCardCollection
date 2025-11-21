package com.example.epicquestcardcollection.data.repository;

import android.content.Context;

import com.example.epicquestcardcollection.data.local.SessionManager;
import com.example.epicquestcardcollection.data.local.SharedPreferencesHelper;
import com.example.epicquestcardcollection.model.User;
import com.example.epicquestcardcollection.utils.AppConstants;
import com.example.epicquestcardcollection.utils.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación concreta del UserRepository usando SharedPreferences.
 * Gestiona múltiples usuarios y sus datos de colección.
 */
public class UserRepositoryImpl implements UserRepository {

    private final SharedPreferencesHelper prefsHelper;
    private final SessionManager sessionManager;
    private final Gson gson;

    public UserRepositoryImpl(Context context) {
        this.prefsHelper = new SharedPreferencesHelper(context, AppConstants.PREFS_NAME);
        this.sessionManager = SessionManager.getInstance(context);
        this.gson = new Gson();
    }

    @Override
    public OperationResult registerUser(String username, String password, String email) {
        // Validar datos de entrada
        ValidationUtils.ValidationResult validation =
                ValidationUtils.validateRegistrationData(username, password, email);

        if (!validation.isValid()) {
            return new OperationResult(false, validation.getMessage());
        }

        // Verificar si el username ya existe
        if (isUsernameTaken(username)) {
            return new OperationResult(false, "El nombre de usuario ya está en uso");
        }

        // Crear nuevo usuario
        User newUser = new User(username, password, email);

        // Guardar usuario en el registro
        saveUserToRegistry(newUser);

        // Iniciar sesión automáticamente
        sessionManager.login(newUser);

        return new OperationResult(true, "Usuario registrado exitosamente", newUser);
    }

    @Override
    public OperationResult loginUser(String username, String password) {
        // Validar credenciales
        ValidationUtils.ValidationResult validation =
                ValidationUtils.validateLoginCredentials(username, password);

        if (!validation.isValid()) {
            return new OperationResult(false, validation.getMessage());
        }

        // Obtener usuario del registro
        User user = getUserFromRegistry(username);
        if (user == null) {
            return new OperationResult(false, "Usuario no encontrado");
        }

        // Verificar contraseña (en producción esto sería un hash)
        // TODO: Implementar hashing de contraseñas para mejorar la seguridad.
        if (!user.getPassword().equals(password)) {
            return new OperationResult(false, "Contraseña incorrecta");
        }

        // Iniciar sesión
        sessionManager.login(user);

        return new OperationResult(true, "Inicio de sesión exitoso", user);
    }

    @Override
    public OperationResult logoutUser() {
        if (!sessionManager.isLoggedIn()) {
            return new OperationResult(false, "No hay sesión activa");
        }

        sessionManager.logout();
        return new OperationResult(true, "Sesión cerrada exitosamente");
    }

    @Override
    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    @Override
    public OperationResult updateUser(User user) {
        if (user == null) {
            return new OperationResult(false, "Usuario inválido");
        }

        // Guardar cambios en el registro
        saveUserToRegistry(user);

        // Si es el usuario actual, actualizar la sesión
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getUsername().equals(user.getUsername())) {
            sessionManager.login(user);
        }

        return new OperationResult(true, "Usuario actualizado exitosamente", user);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return getUserFromRegistry(username) != null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Guarda un usuario en el registro de usuarios
     */
    private void saveUserToRegistry(User user) {
        // Obtener el mapa actual de usuarios
        Map<String, User> users = getUsersRegistry();

        // Agregar/actualizar usuario
        users.put(user.getUsername(), user);

        // Guardar mapa actualizado
        String usersJson = gson.toJson(users);
        prefsHelper.putString(AppConstants.KEY_USERS_DATA, usersJson);
    }

    /**
     * Obtiene un usuario del registro por username
     */
    private User getUserFromRegistry(String username) {
        Map<String, User> users = getUsersRegistry();
        return users.get(username);
    }

    /**
     * Obtiene el mapa completo de usuarios registrados
     */
    private Map<String, User> getUsersRegistry() {
        String usersJson = prefsHelper.getString(AppConstants.KEY_USERS_DATA, "{}");

        Type type = new TypeToken<HashMap<String, User>>(){}.getType();
        Map<String, User> users = gson.fromJson(usersJson, type);

        return users != null ? users : new HashMap<>();
    }
}
