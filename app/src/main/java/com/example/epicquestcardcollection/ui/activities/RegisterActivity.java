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
 * Actividad para el registro de nuevos usuarios.
 * Permite crear una cuenta para guardar la colección de cartas.
 */
public class RegisterActivity extends BaseActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private TextView tvError;

    private UserRepository userRepository;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    protected void initializeUI() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tvError = findViewById(R.id.tvError);

        userRepository = new UserRepositoryImpl(this);
    }

    @Override
    protected void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());
        tvLoginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void attemptRegistration() {
        // Ocultar error anterior
        hideError();

        // Obtener datos del formulario
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(username)) {
            showError(getString(R.string.error_username_required));
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            showError(getString(R.string.error_email_required));
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.error_password_required));
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            showError(getString(R.string.error_confirm_password_required));
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(getString(R.string.error_passwords_dont_match));
            etPassword.requestFocus();
            return;
        }

        // Intentar registro
        performRegistration(username, email, password);
    }

    private void performRegistration(String username, String email, String password) {
        // Mostrar progreso
        btnRegister.setEnabled(false);
        btnRegister.setText(R.string.registering);

        // Usar el repositorio para registrar usuario
        UserRepository.OperationResult result = userRepository.registerUser(username, password, email);

        if (result.isSuccess()) {
            // Registro exitoso
            showToast(result.getMessage());
            navigateToWelcome();
        } else {
            // Error en registro
            showError(result.getMessage());
            btnRegister.setEnabled(true);
            btnRegister.setText(R.string.register);
        }
    }

    private void showError(String errorMessage) {
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finalizamos esta actividad ya que no queremos volver atrás en el registro
    }

    private void navigateToWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
