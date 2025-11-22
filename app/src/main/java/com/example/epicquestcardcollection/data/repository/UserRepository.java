package com.example.epicquestcardcollection.data.repository;

import com.example.epicquestcardcollection.model.User;
import com.example.epicquestcardcollection.utils.ValidationUtils;

/**
 * Contrato que define las operaciones disponibles para la gestión de usuarios.
 * Sigue el patrón Repository para abstraer el origen de datos.
 */
public interface UserRepository {

    /**
     * Registra un nuevo usuario en el sistema
     */
    OperationResult registerUser(String username, String password, String email);

    /**
     * Autentica un usuario con sus credenciales
     */
    OperationResult loginUser(String username, String password);

    /**
     * Cierra la sesión del usuario actual
     */
    OperationResult logoutUser();

    /**
     * Obtiene el usuario actualmente logueado
     * @return El usuario actual o null si no hay sesión
     * @apiNote Se recomienda validar el resultado para evitar NullPointerExceptions.
     */
    User getCurrentUser();

    /**
     * Actualiza los datos de un usuario
     */
    OperationResult updateUser(User user);

    /**
     * Verifica si un username ya está en uso
     */
    boolean isUsernameTaken(String username);

    /**
     * Verifica si hay una sesión activa
     */
    boolean isUserLoggedIn();

    /**
     * Resultado de una operación del repositorio
     */
    class OperationResult {
        private final boolean success;
        private final String message;
        private final User user;

        public OperationResult(boolean success, String message) {
            this(success, message, null);
        }

        public OperationResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }

    /**
     * Resetea las oportunidades diarias del usuario actual (para testing)
     */
    OperationResult resetDailyOpportunities();
}
