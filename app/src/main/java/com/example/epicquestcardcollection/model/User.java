package com.example.epicquestcardcollection.model;

import com.example.epicquestcardcollection.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa un usuario de la aplicación.
 * Contiene toda la información del usuario y su progreso en el juego.
 */
public class User {
    private String username;
    private String password; // En producción debería ser hash
    private String email;
    private List<HeroCard> collection;
    private int dailyOpportunities;
    private long lastCardTime;
    private int playerLevel;
    private List<String> achievements;
    private long createdAt;

    public User() {
        this.collection = new ArrayList<>();
        this.dailyOpportunities = 5; // Valor por defecto
        this.lastCardTime = 0;
        this.playerLevel = 1;
        this.achievements = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public User(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Agrega una carta a la colección del usuario
     */
    public void addCardToCollection(HeroCard card) {
        if (card != null && !collection.contains(card)) {
            collection.add(card);
            // TODO: En futuras iteraciones, actualizar logros y nivel
        }
    }

    /**
     * Verifica si el usuario puede obtener una carta
     */
    public boolean canObtainCard() {
        return dailyOpportunities > 0;
    }

    /**
     * Consume una oportunidad diaria
     */
    public void consumeOpportunity() {
        if (dailyOpportunities > 0) {
            dailyOpportunities--;
            lastCardTime = System.currentTimeMillis();
        }
    }

    /**
     * Reinicia las oportunidades diarias (para testing)
     */
    public void resetDailyOpportunities() {
        this.dailyOpportunities = AppConstants.DAILY_OPPORTUNITIES;
        this.lastCardTime = 0;
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<HeroCard> getCollection() { return collection; }
    public void setCollection(List<HeroCard> collection) { this.collection = collection; }

    public int getDailyOpportunities() { return dailyOpportunities; }
    public void setDailyOpportunities(int dailyOpportunities) {
        this.dailyOpportunities = dailyOpportunities;
    }

    public long getLastCardTime() { return lastCardTime; }
    public void setLastCardTime(long lastCardTime) { this.lastCardTime = lastCardTime; }

    public int getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}