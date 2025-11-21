package com.example.epicquestcardcollection.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Actividad para el inicio de sesión de usuarios.
 * Permite a los usuarios autenticarse con sus credenciales.
 */
public class LoginActivity extends BaseActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private TextView tvError;

    private UserRepository userRepository;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializeUI() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvError = findViewById(R.id.tvError);

        userRepository = new UserRepositoryImpl(this);
    }

    @Override
    protected void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegisterLink.setOnClickListener(v -> navigateToRegister());
    }

    private void attemptLogin() {
        // Ocultar error anterior
        hideError();

        // Obtener credenciales
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos vacíos
        if (TextUtils.isEmpty(username)) {
            showError(getString(R.string.error_username_required));
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.error_password_required));
            etPassword.requestFocus();
            return;
        }

        // Intentar login
        performLogin(username, password);
    }

    private void performLogin(String username, String password) {
        // Mostrar progreso
        btnLogin.setEnabled(false);
        btnLogin.setText(R.string.logging_in);

        // Usar el repositorio para hacer login
        UserRepository.OperationResult result = userRepository.loginUser(username, password);

        if (result.isSuccess()) {
            // Login exitoso
            showToast(result.getMessage());
            navigateToWelcome();
        } else {
            // Error en login
            showError(result.getMessage());
            btnLogin.setEnabled(true);
            btnLogin.setText(R.string.login);
        }
    }

    private void showError(String errorMessage) {
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        // No finalizamos esta actividad para permitir volver atrás
    }

    private void navigateToWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
