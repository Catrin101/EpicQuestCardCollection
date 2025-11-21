package com.example.epicquestcardcollection.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;

/**
 * Actividad de bienvenida que se muestra después del login/registro exitoso.
 * Redirige automáticamente al Dashboard después de un breve tiempo.
 */
public class WelcomeActivity extends BaseActivity {

    private TextView tvWelcome;
    private Button btnContinue;
    private UserRepository userRepository;
    private Handler handler;
    private Runnable autoRedirectRunnable;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initializeUI() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnContinue = findViewById(R.id.btnContinue);

        userRepository = new UserRepositoryImpl(this);
        handler = new Handler(Looper.getMainLooper());

        // Personalizar mensaje de bienvenida con el nombre del usuario
        String username = getCurrentUsername();
        String welcomeMessage = getString(R.string.welcome_title) + " " + username + "!";
        tvWelcome.setText(welcomeMessage);

        // Redirigir automáticamente después de 3 segundos
        startAutoRedirect();
    }

    @Override
    protected void setupListeners() {
        btnContinue.setOnClickListener(v -> {
            cancelAutoRedirect();
            navigateToDashboard();
        });
    }

    private String getCurrentUsername() {
        try {
            if (userRepository != null && userRepository.getCurrentUser() != null) {
                return userRepository.getCurrentUser().getUsername();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Coleccionista";
    }

    private void startAutoRedirect() {
        autoRedirectRunnable = () -> {
            if (!isFinishing() && !isDestroyed()) {
                navigateToDashboard();
            }
        };
        handler.postDelayed(autoRedirectRunnable, 3000); // 3 segundos
    }

    private void cancelAutoRedirect() {
        if (handler != null && autoRedirectRunnable != null) {
            handler.removeCallbacks(autoRedirectRunnable);
        }
    }

    private void navigateToDashboard() {
        try {
            Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error al navegar al Dashboard");
        }
    }

    @Override
    protected void onPause() {
        // Cancelar redirección automática si la actividad se pausa
        cancelAutoRedirect();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Limpiar handler
        cancelAutoRedirect();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
