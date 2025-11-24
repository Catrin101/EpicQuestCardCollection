package com.example.epicquestcardcollection.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.epicquestcardcollection.base.data.Result;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Implementación concreta del UserRepository usando SharedPreferences.
 * Gestiona múltiples usuarios y sus datos de colección.
 */
public class UserRepositoryImpl implements UserRepository {

    private final SharedPreferencesHelper prefsHelper;
    private final SessionManager sessionManager;
    private final Gson gson;
    private final Executor executor;
    private final Handler mainHandler;

    public UserRepositoryImpl(Context context) {
        this.prefsHelper = new SharedPreferencesHelper(context, AppConstants.PREFS_NAME);
        this.sessionManager = SessionManager.getInstance(context);
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void registerUser(String username, String password, String email, Callback<User> callback) {
        executor.execute(() -> {
            try {
                ValidationUtils.ValidationResult validation =
                        ValidationUtils.validateRegistrationData(username, password, email);
                if (!validation.isValid()) {
                    throw new Exception(validation.getMessage());
                }

                if (isUsernameTakenSync(username)) {
                    throw new Exception("El nombre de usuario ya está en uso");
                }

                User newUser = new User(username, password, email);
                saveUserToRegistry(newUser);
                sessionManager.login(newUser);

                mainHandler.post(() -> callback.onResult(Result.success(newUser)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void loginUser(String username, String password, Callback<User> callback) {
        executor.execute(() -> {
            try {
                ValidationUtils.ValidationResult validation =
                        ValidationUtils.validateLoginCredentials(username, password);
                if (!validation.isValid()) {
                    throw new Exception(validation.getMessage());
                }

                User user = getUserFromRegistry(username);
                if (user == null) {
                    throw new Exception("Usuario no encontrado");
                }

                if (!user.checkPassword(password)) {
                    throw new Exception("Contraseña incorrecta");
                }

                if (user.getPassword() != null) {
                    user.upgradePassword(password);
                    saveUserToRegistry(user);
                }

                sessionManager.login(user);
                mainHandler.post(() -> callback.onResult(Result.success(user)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void logoutUser(Callback<Void> callback) {
        executor.execute(() -> {
            try {
                if (!sessionManager.isLoggedIn()) {
                    throw new Exception("No hay sesión activa");
                }
                sessionManager.logout();
                mainHandler.post(() -> callback.onResult(Result.success(null)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void getCurrentUser(Callback<User> callback) {
        executor.execute(() -> {
            try {
                User user = sessionManager.getCurrentUser();
                mainHandler.post(() -> callback.onResult(Result.success(user)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void updateUser(User user, Callback<User> callback) {
        executor.execute(() -> {
            try {
                if (user == null) {
                    throw new Exception("Usuario inválido");
                }

                saveUserToRegistry(user);
                User currentUser = sessionManager.getCurrentUser();
                if (currentUser != null && currentUser.getUsername().equals(user.getUsername())) {
                    sessionManager.login(user);
                }

                mainHandler.post(() -> callback.onResult(Result.success(user)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void isUsernameTaken(String username, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                boolean isTaken = isUsernameTakenSync(username);
                mainHandler.post(() -> callback.onResult(Result.success(isTaken)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void isUserLoggedIn(Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                boolean isLoggedIn = sessionManager.isLoggedIn();
                mainHandler.post(() -> callback.onResult(Result.success(isLoggedIn)));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    @Override
    public void resetDailyOpportunities(Callback<Void> callback) {
        executor.execute(() -> {
            try {
                User currentUser = sessionManager.getCurrentUser();
                if (currentUser != null) {
                    currentUser.resetDailyOpportunities();
                    saveUserToRegistry(currentUser);
                    mainHandler.post(() -> callback.onResult(Result.success(null)));
                } else {
                    throw new Exception("No hay usuario activo");
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(Result.error(e)));
            }
        });
    }

    /**
     * Verifica si un username ya está en uso de forma síncrona.
     * @param username
     * @return
     */
    private boolean isUsernameTakenSync(String username) {
        return getUserFromRegistry(username) != null;
    }

    /**
     * Guarda un usuario en el registro de usuarios.
     * @param user
     */
    private void saveUserToRegistry(User user) {
        Map<String, User> users = getUsersRegistry();
        users.put(user.getUsername(), user);
        String usersJson = gson.toJson(users);
        prefsHelper.putString(AppConstants.KEY_USERS_DATA, usersJson);
    }

    /**
     * Obtiene un usuario del registro por username.
     * @param username
     * @return
     */
    private User getUserFromRegistry(String username) {
        Map<String, User> users = getUsersRegistry();
        return users.get(username);
    }

    /**
     * Obtiene el mapa completo de usuarios registrados.
     * @return
     */
    private Map<String, User> getUsersRegistry() {
        String usersJson = prefsHelper.getString(AppConstants.KEY_USERS_DATA, "{}");
        Type type = new TypeToken<HashMap<String, User>>() {
        }.getType();
        Map<String, User> users = gson.fromJson(usersJson, type);
        return users != null ? users : new HashMap<>();
    }
}
