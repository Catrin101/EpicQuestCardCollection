package com.example.epicquestcardcollection.ui.activities;

import android.os.Bundle;

import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.data.local.SessionManager;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;
import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.PowerStats;
import com.example.epicquestcardcollection.utils.DateUtils;
import com.example.epicquestcardcollection.utils.ValidationUtils;

/**
 * Activity principal que sirve como punto de entrada de la aplicación.
 * En futuras iteraciones, esta activity redirigirá al Onboarding o Dashboard
 * según el estado de la sesión del usuario.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeUI() {
        // Probar DateUtils
        long currentTime = System.currentTimeMillis();
        String formattedTime = DateUtils.formatTime(currentTime);
        String formattedDate = DateUtils.formatDate(currentTime);
        showToast("Hora: " + formattedTime + " - Fecha: " + formattedDate);

        // Probar ValidationUtils
        ValidationUtils.ValidationResult validation =
                ValidationUtils.validateRegistrationData("testuser", "password123", "test@example.com");
        showToast("Validación: " + validation.getMessage());

        // Probar UserRepository
        UserRepository userRepo = new UserRepositoryImpl(this);
        showToast("Usuario logueado: " + userRepo.isUserLoggedIn());

        // Probar cooldown
        long remaining = DateUtils.calculateRemainingCooldown(System.currentTimeMillis() - 1800000); // 30 min ago
        String remainingFormatted = DateUtils.formatRemainingTime(remaining);
        showToast("Cooldown restante: " + remainingFormatted);
    }

    @Override
    protected void setupListeners() {
        // Configurar listeners de botones aquí en futuras iteraciones
    }
}