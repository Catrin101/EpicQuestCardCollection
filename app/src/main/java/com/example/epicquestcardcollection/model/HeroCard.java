package com.example.epicquestcardcollection.model;

import com.example.epicquestcardcollection.R;
import java.io.Serializable;

/**
 * Modelo que representa una carta de héroe con toda su información.
 * Implementa Serializable para poder pasar el objeto entre actividades.
 */
public class HeroCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String biography;
    private String imageUrl;
    private PowerStats powerStats;
    private String rarity;
    private int totalPower;
    private long obtainedAt;

    public HeroCard() {
        this.obtainedAt = System.currentTimeMillis();
    }

    public HeroCard(String id, String name, String biography, String imageUrl, PowerStats powerStats) {
        this();
        this.id = id;
        this.name = name;
        this.biography = biography;
        this.imageUrl = imageUrl;
        this.powerStats = powerStats;
        this.totalPower = calculateTotalPower();
        this.rarity = calculateRarity();
    }

    /**
     * Calcula el poder total basado en las estadísticas
     */
    private int calculateTotalPower() {
        if (powerStats == null) return 0;
        return powerStats.getIntelligence() + powerStats.getStrength() +
                powerStats.getSpeed() + powerStats.getDurability() +
                powerStats.getPower() + powerStats.getCombat();
    }

    /**
     * Calcula la rareza basada en el poder total
     */
    private String calculateRarity() {
        int total = calculateTotalPower();
        if (total >= 500) return "LEGENDARY";
        else if (total >= 400) return "EPIC";
        else if (total >= 300) return "RARE";
        else if (total >= 200) return "UNCOMMON";
        else return "COMMON";
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public PowerStats getPowerStats() { return powerStats; }
    public void setPowerStats(PowerStats powerStats) {
        this.powerStats = powerStats;
        this.totalPower = calculateTotalPower();
        this.rarity = calculateRarity();
    }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public int getTotalPower() { return totalPower; }
    public void setTotalPower(int totalPower) { this.totalPower = totalPower; }

    public long getObtainedAt() { return obtainedAt; }
    public void setObtainedAt(long obtainedAt) { this.obtainedAt = obtainedAt; }

    /**
     * Método para determinar el color de la rareza
     */
    public int getRarityColor() {
        switch (rarity) {
            case "LEGENDARY":
                return R.color.legendary_color; // Dorado
            case "EPIC":
                return R.color.epic_color; // Púrpura
            case "RARE":
                return R.color.rare_color; // Azul
            case "UNCOMMON":
                return R.color.uncommon_color; // Verde
            default: // COMMON
                return R.color.common_color; // Gris
        }
    }
}
