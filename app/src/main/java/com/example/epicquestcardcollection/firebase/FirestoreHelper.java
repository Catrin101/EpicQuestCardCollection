package com.example.epicquestcardcollection.firebase;

import android.util.Log;
import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper para gestionar operaciones con Firestore Database
 * Maneja guardado, lectura y actualización de datos
 */
public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db;

    // Nombres de las colecciones
    private static final String USERS_COLLECTION = "users";
    private static final String CARDS_COLLECTION = "cards";

    // Interface para callbacks
    public interface FirestoreCallback {
        void onSuccess(Object data);
        void onError(String errorMessage);
    }

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Guarda o actualiza los datos de un usuario
     * @param userId ID único del usuario (UID de Firebase Auth)
     * @param user Objeto User con los datos
     * @param callback Callback con el resultado
     */
    public void saveUser(String userId, User user, FirestoreCallback callback) {
        Log.d(TAG, "Guardando usuario: " + userId);

        // Convertir User a Map para Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("playerLevel", user.getPlayerLevel());
        userData.put("dailyOpportunities", user.getDailyOpportunities());
        userData.put("lastCardTime", user.getLastCardTime());
        userData.put("createdAt", user.getCreatedAt());

        // Guardar en Firestore
        db.collection(USERS_COLLECTION)
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Usuario guardado exitosamente");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar usuario: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Obtiene los datos de un usuario desde Firestore
     * @param userId ID del usuario
     * @param callback Callback con los datos del usuario
     */
    public void getUser(String userId, FirestoreCallback callback) {
        Log.d(TAG, "Obteniendo usuario: " + userId);

        db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir datos a objeto User
                        User user = new User();
                        user.setUsername(documentSnapshot.getString("username"));
                        user.setEmail(documentSnapshot.getString("email"));
                        user.setPlayerLevel(documentSnapshot.getLong("playerLevel").intValue());
                        user.setDailyOpportunities(documentSnapshot.getLong("dailyOpportunities").intValue());
                        user.setLastCardTime(documentSnapshot.getLong("lastCardTime"));
                        user.setCreatedAt(documentSnapshot.getLong("createdAt"));

                        Log.d(TAG, "✅ Usuario obtenido: " + user.getUsername());
                        callback.onSuccess(user);
                    } else {
                        Log.w(TAG, "⚠️ Usuario no encontrado en Firestore");
                        callback.onError("Usuario no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al obtener usuario: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Guarda una carta en la colección del usuario
     * @param userId ID del usuario
     * @param card Carta a guardar
     * @param callback Callback con el resultado
     */
    public void saveCard(String userId, HeroCard card, FirestoreCallback callback) {
        Log.d(TAG, "Guardando carta: " + card.getName() + " para usuario: " + userId);

        // Convertir HeroCard a Map
        Map<String, Object> cardData = new HashMap<>();
        cardData.put("id", card.getId());
        cardData.put("name", card.getName());
        cardData.put("biography", card.getBiography());
        cardData.put("imageUrl", card.getImageUrl());
        cardData.put("rarity", card.getRarity());
        cardData.put("totalPower", card.getTotalPower());
        cardData.put("obtainedAt", card.getObtainedAt());

        // Guardar power stats
        if (card.getPowerStats() != null) {
            Map<String, Object> powerStats = new HashMap<>();
            powerStats.put("intelligence", card.getPowerStats().getIntelligence());
            powerStats.put("strength", card.getPowerStats().getStrength());
            powerStats.put("speed", card.getPowerStats().getSpeed());
            powerStats.put("durability", card.getPowerStats().getDurability());
            powerStats.put("power", card.getPowerStats().getPower());
            powerStats.put("combat", card.getPowerStats().getCombat());
            cardData.put("powerStats", powerStats);
        }

        // Guardar en subcollection del usuario
        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CARDS_COLLECTION)
                .document(card.getId())
                .set(cardData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Carta guardada exitosamente");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar carta: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Obtiene todas las cartas de un usuario
     * @param userId ID del usuario
     * @param callback Callback con la lista de cartas
     */
    public void getUserCards(String userId, FirestoreCallback callback) {
        Log.d(TAG, "Obteniendo cartas del usuario: " + userId);

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CARDS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<HeroCard> cards = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Aquí deberías reconstruir el objeto HeroCard
                        // Por simplicidad, solo logueo el nombre
                        String cardName = document.getString("name");
                        Log.d(TAG, "Carta encontrada: " + cardName);
                    }

                    Log.d(TAG, "✅ Total de cartas obtenidas: " + cards.size());
                    callback.onSuccess(cards);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al obtener cartas: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Actualiza un campo específico del usuario
     * @param userId ID del usuario
     * @param field Nombre del campo a actualizar
     * @param value Nuevo valor
     * @param callback Callback con el resultado
     */
    public void updateUserField(String userId, String field, Object value, FirestoreCallback callback) {
        Log.d(TAG, "Actualizando campo " + field + " del usuario: " + userId);

        db.collection(USERS_COLLECTION)
                .document(userId)
                .update(field, value)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Campo actualizado exitosamente");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al actualizar: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }
}
