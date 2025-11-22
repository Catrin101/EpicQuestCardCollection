package com.example.epicquestcardcollection.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;
import com.example.epicquestcardcollection.model.User;

/**
 * Actividad principal que sirve como dashboard/menú de la aplicación.
 * Permite navegar a todas las secciones principales del juego.
 */
public class DashboardActivity extends BaseActivity {

    private static final String TAG = "DashboardActivity";

    private TextView tvWelcome;
    private com.google.android.material.card.MaterialCardView btnObtainCards;
    private com.google.android.material.card.MaterialCardView btnMyCollection;
    private com.google.android.material.card.MaterialCardView btnStats;
    private com.google.android.material.card.MaterialCardView btnSettings;
    private Button btnLogout;

    private UserRepository userRepository;
    private User currentUser;
    private boolean isBackPressedOnce = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_dashboard;
    }

    @Override
    protected void initializeUI() {
        try {
            Log.d(TAG, "Iniciando inicialización de UI");

            // Configurar OnBackPressedDispatcher
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (isBackPressedOnce) {
                        finishAffinity();
                        return;
                    }

                    isBackPressedOnce = true;
                    showToast("Presiona nuevamente para salir");

                    new android.os.Handler().postDelayed(() -> isBackPressedOnce = false, 2000);
                }
            });

            // Inicializar vistas
            tvWelcome = findViewById(R.id.tvWelcome);
            btnObtainCards = findViewById(R.id.btnObtainCards);
            btnMyCollection = findViewById(R.id.btnMyCollection);
            btnStats = findViewById(R.id.btnStats);
            btnSettings = findViewById(R.id.btnSettings);
            btnLogout = findViewById(R.id.btnLogout);

            // Validar que se encontraron todas las vistas
            if (tvWelcome == null || btnObtainCards == null) {
                Log.e(TAG, "Error: No se pudieron encontrar todos los elementos de la UI");
                finish();
                return;
            }

            // Inicializar repositorio y obtener usuario
            userRepository = new UserRepositoryImpl(this);
            currentUser = userRepository.getCurrentUser();

            setupWelcomeMessage();
            Log.d(TAG, "UI inicializada correctamente");

        } catch (Exception e) {
            Log.e(TAG, "Error en initializeUI: ", e);
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void setupListeners() {
        try {
            Log.d(TAG, "Configurando listeners");

            if (btnObtainCards != null) {
                btnObtainCards.setOnClickListener(v -> navigateToCardObtain());
            }
            if (btnMyCollection != null) {
                btnMyCollection.setOnClickListener(v -> navigateToCollection());
            }
            if (btnStats != null) {
                btnStats.setOnClickListener(v -> navigateToStats());
            }
            if (btnSettings != null) {
                btnSettings.setOnClickListener(v -> navigateToSettings());
            }
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> performLogout());
            }

            Log.d(TAG, "Listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error en setupListeners: ", e);
            e.printStackTrace();
        }
    }

    private void setupWelcomeMessage() {
        try {
            if (currentUser != null && currentUser.getUsername() != null) {
                String welcomeMessage = "¡Bienvenido, " + currentUser.getUsername() + "!";
                tvWelcome.setText(welcomeMessage);
                Log.d(TAG, "Mensaje de bienvenida configurado para: " + currentUser.getUsername());
            } else {
                tvWelcome.setText("¡Bienvenido, Coleccionista!");
                Log.d(TAG, "Usuario no encontrado, usando mensaje genérico");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en setupWelcomeMessage: ", e);
            e.printStackTrace();
            tvWelcome.setText("¡Bienvenido!");
        }
    }

    private void navigateToCardObtain() {
        try {
            Log.d(TAG, "Navegando a CardObtainActivity");
            Intent intent = new Intent(this, CardObtainActivity.class);
            startActivity(intent);
            // ELIMINAR finish() para mantener el Dashboard en el stack
        } catch (Exception e) {
            Log.e(TAG, "Error navegando a CardObtainActivity: ", e);
            e.printStackTrace();
            showToast("Error al abrir Obtener Cartas");
        }
    }

    private void navigateToCollection() {
        try {
            Log.d(TAG, "Navegando a CardCollectionActivity");
            Intent intent = new Intent(this, CardCollectionActivity.class);
            startActivity(intent);
            // ELIMINAR finish() para mantener el Dashboard en el stack
        } catch (Exception e) {
            Log.e(TAG, "Error navegando a CardCollectionActivity: ", e);
            e.printStackTrace();
            showToast("Error al abrir Mi Colección");
        }
    }

    private void navigateToStats() {
        try {
            Log.d(TAG, "Navegando a StatsActivity");
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navegando a StatsActivity: ", e);
            e.printStackTrace();
            showToast("Error al abrir Estadísticas");
        }
    }

    private void navigateToSettings() {
        showToast("Configuración - Próximamente en Fase 5");
        // Podemos implementar SettingsActivity después
    }

    private void performLogout() {
        try {
            if (userRepository != null) {
                UserRepository.OperationResult result = userRepository.logoutUser();
                if (result != null && result.isSuccess()) {
                    showToast(result.getMessage());
                    navigateToLogin();
                } else {
                    String errorMsg = (result != null) ? result.getMessage() : "Error desconocido";
                    showToast("Error al cerrar sesión: " + errorMsg);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en performLogout: ", e);
            e.printStackTrace();
            showToast("Error al cerrar sesión");
        }
    }

    private void navigateToLogin() {
        try {
            Log.d(TAG, "Navegando a LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navegando a LoginActivity: ", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Log.d(TAG, "DashboardActivity destruida");
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "Error en onDestroy: ", e);
            e.printStackTrace();
        }
    }
}