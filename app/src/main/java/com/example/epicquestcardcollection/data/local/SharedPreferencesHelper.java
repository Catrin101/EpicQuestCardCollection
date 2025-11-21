package com.example.epicquestcardcollection.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Clase helper para manejar operaciones con SharedPreferences de manera tipo-segura.
 * Proporciona métodos genéricos para guardar y recuperar objetos complejos.
 */
public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesHelper(Context context, String prefsName) {
        this.sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // ==================== MÉTODOS BÁSICOS ====================

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // ==================== MÉTODOS AVANZADOS (OBJETOS) ====================

    /**
     * Guarda un objeto complejo en SharedPreferences como JSON
     */
    public <T> void putObject(String key, T object) {
        if (object == null) {
            sharedPreferences.edit().remove(key).apply();
            return;
        }
        String json = gson.toJson(object);
        putString(key, json);
    }

    /**
     * Recupera un objeto complejo de SharedPreferences
     */
    public <T> T getObject(String key, Class<T> classOfT, T defaultValue) {
        String json = getString(key, null);
        if (json == null) {
            return defaultValue;
        }
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * Recupera una lista de objetos complejos
     */
    public <T> T getList(String key, Type typeOfT, T defaultValue) {
        String json = getString(key, null);
        if (json == null) {
            return defaultValue;
        }
        try {
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    // ==================== MÉTODOS UTILITARIOS ====================

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}
