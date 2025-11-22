package com.example.epicquestcardcollection.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;

/**
 * Actividad placeholder para estadísticas (será implementada en futuras iteraciones)
 */
public class StatsActivity extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_stats;
    }

    @Override
    protected void initializeUI() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Estadísticas - En Desarrollo");
    }

    @Override
    protected void setupListeners() {
        // Listeners futuros para esta actividad
    }
}
