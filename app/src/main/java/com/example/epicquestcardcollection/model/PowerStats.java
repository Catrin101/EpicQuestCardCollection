package com.example.epicquestcardcollection.model;

/**
 * Modelo que representa las estadísticas de poder de un héroe.
 * Valores van de 0 a 100 para cada estadística.
 */
public class PowerStats {
    private int intelligence;
    private int strength;
    private int speed;
    private int durability;
    private int power;
    private int combat;

    public PowerStats() {
        // Constructor por defecto
    }

    public PowerStats(int intelligence, int strength, int speed,
                      int durability, int power, int combat) {
        this.intelligence = intelligence;
        this.strength = strength;
        this.speed = speed;
        this.durability = durability;
        this.power = power;
        this.combat = combat;
    }

    // ==================== GETTERS Y SETTERS ====================

    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getDurability() { return durability; }
    public void setDurability(int durability) { this.durability = durability; }

    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }

    public int getCombat() { return combat; }
    public void setCombat(int combat) { this.combat = combat; }
}
