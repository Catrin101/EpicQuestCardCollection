package com.example.epicquestcardcollection.model;

import com.example.epicquestcardcollection.utils.AppConstants;
import com.example.epicquestcardcollection.utils.PasswordHasher;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String hashedPassword;
    private byte[] salt;
    private String email;
    private List<HeroCard> collection;
    private int dailyOpportunities;
    private long lastCardTime;
    private int playerLevel;
    private List<String> achievements;
    private long createdAt;

    public User() {
        this.collection = new ArrayList<>();
        this.dailyOpportunities = 5;
        this.lastCardTime = 0;
        this.playerLevel = 1;
        this.achievements = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public User(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        try {
            this.salt = PasswordHasher.getSalt();
            this.hashedPassword = PasswordHasher.getSecurePassword(password, this.salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.email = email;
    }

    public boolean checkPassword(String password) {
        if (this.hashedPassword != null && this.salt != null) {
            return this.hashedPassword.equals(PasswordHasher.getSecurePassword(password, this.salt));
        } else {
            return this.password.equals(password);
        }
    }

    public void upgradePassword(String password) {
        try {
            this.salt = PasswordHasher.getSalt();
            this.hashedPassword = PasswordHasher.getSecurePassword(password, this.salt);
            this.password = null;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCardToCollection(HeroCard card) {
        if (card != null && !collection.contains(card)) {
            collection.add(card);
        }
    }

    public boolean canObtainCard() {
        return dailyOpportunities > 0;
    }

    public void consumeOpportunity() {
        if (dailyOpportunities > 0) {
            dailyOpportunities--;
            lastCardTime = System.currentTimeMillis();
        }
    }

    public void resetDailyOpportunities() {
        this.dailyOpportunities = AppConstants.DAILY_OPPORTUNITIES;
        this.lastCardTime = 0;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public byte[] getSalt() { return salt; }
    public void setSalt(byte[] salt) { this.salt = salt; }

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
