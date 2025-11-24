package com.example.epicquestcardcollection.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

/**
 * Gestor de permisos - Verificación de todos los permisos necesarios
 */
public class PermissionManager {

    // Permisos requeridos
    public static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
    };

    // Permisos individuales para referencias rápidas
    public static final String PERMISSION_CAMERA = android.Manifest.permission.CAMERA;
    public static final String PERMISSION_LOCATION_FINE = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_LOCATION_COARSE = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_NOTIFICATIONS = android.Manifest.permission.POST_NOTIFICATIONS;

    private final Context context;

    public PermissionManager(Context context) {
        this.context = context;
    }

    /**
     * Verifica si todos los permisos están concedidos
     */
    public boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene los permisos que faltan por conceder
     */
    public String[] getMissingPermissions() {
        java.util.ArrayList<String> missing = new java.util.ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                missing.add(permission);
            }
        }
        return missing.toArray(new String[0]);
    }

    /**
     * Verifica si un permiso específico está concedido
     */
    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Verifica si el permiso de cámara está concedido
     */
    public boolean isCameraPermissionGranted() {
        return isPermissionGranted(PERMISSION_CAMERA);
    }

    /**
     * Verifica si el permiso de ubicación está concedido
     */
    public boolean isLocationPermissionGranted() {
        return isPermissionGranted(PERMISSION_LOCATION_FINE) ||
                isPermissionGranted(PERMISSION_LOCATION_COARSE);
    }

    /**
     * Verifica si el permiso de notificaciones está concedido
     */
    public boolean isNotificationPermissionGranted() {
        return isPermissionGranted(PERMISSION_NOTIFICATIONS);
    }
}
