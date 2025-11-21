package com.example.epicquestcardcollection.base;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

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
}
