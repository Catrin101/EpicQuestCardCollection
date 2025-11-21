package com.example.epicquestcardcollection.utils;

import java.util.regex.Pattern;

/**
 * Clase de utilidades para validación de datos de entrada.
 * Centraliza todas las validaciones de formularios y datos.
 */
public class ValidationUtils {

    /**
     * Valida un nombre de usuario
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.length() >= AppConstants.MIN_USERNAME_LENGTH;
    }

    /**
     * Valida una contraseña
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= AppConstants.MIN_PASSWORD_LENGTH;
    }

    /**
     * Valida un email usando expresión regular
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.compile(AppConstants.EMAIL_PATTERN)
                .matcher(email)
                .matches();
    }

    /**
     * Valida credenciales de login
     */
    public static ValidationResult validateLoginCredentials(String username, String password) {
        if (!isValidUsername(username)) {
            return new ValidationResult(false, "Usuario inválido");
        }

        if (!isValidPassword(password)) {
            return new ValidationResult(false, "Contraseña inválida");
        }

        return new ValidationResult(true, "Credenciales válidas");
    }

    /**
     * Valida datos de registro
     */
    public static ValidationResult validateRegistrationData(String username, String password, String email) {
        ValidationResult usernameValidation = validateUsername(username);
        if (!usernameValidation.isValid()) {
            return usernameValidation;
        }

        ValidationResult passwordValidation = validatePassword(password);
        if (!passwordValidation.isValid()) {
            return passwordValidation;
        }

        ValidationResult emailValidation = validateEmail(email);
        if (!emailValidation.isValid()) {
            return emailValidation;
        }

        return new ValidationResult(true, "Datos de registro válidos");
    }

    private static ValidationResult validateUsername(String username) {
        if (!isValidUsername(username)) {
            return new ValidationResult(false,
                    "El usuario debe tener al menos " + AppConstants.MIN_USERNAME_LENGTH + " caracteres");
        }
        return new ValidationResult(true, "Usuario válido");
    }

    private static ValidationResult validatePassword(String password) {
        if (!isValidPassword(password)) {
            return new ValidationResult(false,
                    "La contraseña debe tener al menos " + AppConstants.MIN_PASSWORD_LENGTH + " caracteres");
        }
        return new ValidationResult(true, "Contraseña válida");
    }

    private static ValidationResult validateEmail(String email) {
        if (!isValidEmail(email)) {
            return new ValidationResult(false, "Email inválido");
        }
        return new ValidationResult(true, "Email válido");
    }

    /**
     * Clase para representar el resultado de una validación
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
