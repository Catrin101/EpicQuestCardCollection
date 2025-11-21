package com.example.epicquestcardcollection.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.remote.SuperHeroAPI;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;
import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.User;
import com.example.epicquestcardcollection.utils.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;

/**
 * Actividad principal para obtener nuevas cartas de héroes.
 * Incluye navegación inferior para moverse entre secciones.
 */
public class CardObtainActivity extends BaseActivity implements SuperHeroAPI.HeroCallback {

    private TextView tvOpportunities;
    private TextView tvCooldown;
    private Button btnObtainCard;
    private View cardContainer;
    private ImageView ivHeroImage;
    private TextView tvHeroName;
    private TextView tvHeroRarity;
    private TextView tvHeroStats;
    private TextView tvHeroBiography;
    private BottomNavigationView bottomNavigation;

    private UserRepository userRepository;
    private User currentUser;
    private boolean isObtainingCard = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_card_obtain;
    }

    @Override
    protected void initializeUI() {
        tvOpportunities = findViewById(R.id.tvOpportunities);
        tvCooldown = findViewById(R.id.tvCooldown);
        btnObtainCard = findViewById(R.id.btnObtainCard);
        cardContainer = findViewById(R.id.cardContainer);
        ivHeroImage = findViewById(R.id.ivHeroImage);
        tvHeroName = findViewById(R.id.tvHeroName);
        tvHeroRarity = findViewById(R.id.tvHeroRarity);
        tvHeroStats = findViewById(R.id.tvHeroStats);
        tvHeroBiography = findViewById(R.id.tvHeroBiography);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        userRepository = new UserRepositoryImpl(this);
        currentUser = userRepository.getCurrentUser();

        setupBottomNavigation();
        updateUI();
        startCooldownChecker();
    }

    @Override
    protected void setupListeners() {
        btnObtainCard.setOnClickListener(v -> attemptObtainCard());

        // Bottom Navigation listener
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                navigateToDashboard();
                return true;
            } else if (itemId == R.id.navigation_obtain_cards) {
                // Ya estamos en esta actividad
                return true;
            } else if (itemId == R.id.navigation_collection) {
                navigateToCollection();
                return true;
            }
            return false;
        });
    }

    private void setupBottomNavigation() {
        // Marcar la opción actual como seleccionada
        bottomNavigation.setSelectedItemId(R.id.navigation_obtain_cards);
    }

    private void updateUI() {
        if (currentUser == null) return;

        // Actualizar oportunidades
        int opportunities = currentUser.getDailyOpportunities();
        tvOpportunities.setText("Oportunidades: " + opportunities + "/5");

        // Verificar cooldown
        long lastCardTime = currentUser.getLastCardTime();
        long remainingCooldown = DateUtils.calculateRemainingCooldown(lastCardTime);

        if (remainingCooldown > 0 && opportunities < 5) {
            tvCooldown.setText("Siguiente oportunidad en: " +
                    DateUtils.formatRemainingTime(remainingCooldown));
            tvCooldown.setVisibility(View.VISIBLE);
            btnObtainCard.setEnabled(false);
            btnObtainCard.setText("En espera...");
        } else {
            tvCooldown.setVisibility(View.GONE);
            btnObtainCard.setEnabled(opportunities > 0 && !isObtainingCard);
            btnObtainCard.setText(opportunities > 0 ? "Obtener Carta" : "Sin Oportunidades");
        }
    }

    private void attemptObtainCard() {
        if (currentUser == null) {
            showToast("Error: No hay usuario activo");
            return;
        }

        if (!currentUser.canObtainCard()) {
            showToast("No tienes oportunidades disponibles");
            return;
        }

        isObtainingCard = true;
        btnObtainCard.setEnabled(false);
        btnObtainCard.setText("Buscando héroe...");

        // Ocultar carta anterior
        cardContainer.setVisibility(View.GONE);

        // Obtener carta aleatoria
        SuperHeroAPI.getRandomHero(this);
    }

    @Override
    public void onHeroReceived(HeroCard heroCard) {
        isObtainingCard = false;

        if (currentUser != null && heroCard != null) {
            // Consumir oportunidad y agregar carta
            currentUser.consumeOpportunity();
            currentUser.addCardToCollection(heroCard);
            userRepository.updateUser(currentUser);

            // Mostrar carta obtenida
            displayHeroCard(heroCard);
            showToast("¡Nueva carta obtenida: " + heroCard.getName() + "!");

            // Actualizar UI
            updateUI();
        } else {
            showToast("Error al obtener la carta");
            updateUI();
        }
    }

    @Override
    public void onError(String errorMessage) {
        isObtainingCard = false;
        showToast("Error: " + errorMessage);
        updateUI();
    }

    private void displayHeroCard(HeroCard heroCard) {
        // Mostrar información del héroe
        tvHeroName.setText(heroCard.getName());
        tvHeroRarity.setText("Rareza: " + heroCard.getRarity());
        tvHeroBiography.setText(heroCard.getBiography());

        // Mostrar estadísticas
        String stats = String.format(
                "Fuerza: %d | Velocidad: %d | Inteligencia: %d\nPoder: %d | Combate: %d | Durabilidad: %d",
                heroCard.getPowerStats().getStrength(),
                heroCard.getPowerStats().getSpeed(),
                heroCard.getPowerStats().getIntelligence(),
                heroCard.getPowerStats().getPower(),
                heroCard.getPowerStats().getCombat(),
                heroCard.getPowerStats().getDurability()
        );
        tvHeroStats.setText(stats);

        // Cargar imagen con Picasso
        Picasso.get()
                .load(heroCard.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivHeroImage);

        // Mostrar contenedor de carta
        cardContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Inicia un thread para verificar el cooldown periódicamente
     */
    private void startCooldownChecker() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000); // Actualizar cada segundo
                    runOnUiThread(this::updateUI);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCollection() {
        Intent intent = new Intent(this, CardCollectionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
