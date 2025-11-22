package com.example.epicquestcardcollection.base;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.widget.Toast;

import com.example.epicquestcardcollection.utils.PermissionManager;

import org.jspecify.annotations.NonNull;

/**
 * Clase base abstracta para todas las Activities de la aplicación.
 * Proporciona funcionalidades comunes y métodos helper para simplificar
 * el desarrollo de nuevas pantallas.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initializeUI();
        setupListeners();
    }

    /**
     * @return El resource ID del layout para esta Activity
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * Inicializa los componentes de la UI
     */
    protected void initializeUI() {
        // Implementación base, puede ser sobrescrita
    }

    /**
     * Configura los listeners de los componentes
     */
    protected void setupListeners() {
        // Implementación base, puede ser sobrescrita
    }

    /**
     * Muestra un Toast con el mensaje proporcionado
     * @param message Mensaje a mostrar
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra un Toast con el resource string proporcionado
     * @param resId Resource ID del string
     */
    protected void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Configura una Toolbar con título y botón de navegación
     * @param toolbar Toolbar a configurar
     * @param titleId Resource ID del título
     * @param showHomeEnabled True para mostrar botón de navegación
     */
    protected void setupToolbar(Toolbar toolbar, int titleId, boolean showHomeEnabled) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleId);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeEnabled);
        }
    }

    // En BaseActivity.java - Agregar estas constantes y métodos simples:
    protected static final int PERMISSION_REQUEST_CODE = 1001;
    protected PermissionManager permissionManager;
    /**
     * Inicializa el PermissionManager de forma simple
     */
    protected void initializePermissionManager() {
        permissionManager = new PermissionManager(this);
    }

    /**
     * Solicita permisos faltantes de forma directa
     */
    protected void requestMissingPermissions() {
        if (permissionManager != null) {
            String[] missingPermissions = permissionManager.getMissingPermissions();
            if (missingPermissions.length > 0) {
                // Esto activará el cuadro de diálogo nativo de Android
                requestPermissions(missingPermissions, PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Maneja el resultado de los permisos - metodo simple
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                onAllPermissionsGranted();
            } else {
                onSomePermissionsDenied();
            }
        }
    }

    /**
     * Se llama cuando todos los permisos son concedidos
     */
    protected void onAllPermissionsGranted() {
        Log.d(TAG, "Todos los permisos concedidos");
    }

    /**
     * Se llama cuando algunos permisos son denegados
     */
    protected void onSomePermissionsDenied() {
        Log.w(TAG, "Algunos permisos fueron denegados");
        showToast("Algunas funciones pueden no estar disponibles");
    }
}
