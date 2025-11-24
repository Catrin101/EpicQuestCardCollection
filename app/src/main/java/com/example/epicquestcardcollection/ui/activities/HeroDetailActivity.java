package com.example.epicquestcardcollection.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.PowerStats;
import com.squareup.picasso.Picasso;

/**
 * Actividad que muestra los detalles completos de un héroe
 * incluyendo estadísticas, imagen y toda la información disponible.
 */
public class HeroDetailActivity extends BaseActivity {

    // Constantes
    private static final String HERO_EXTRA = "hero_card";

    // UI Components
    private Button btnBack;
    private ImageView ivHeroImage;
    private TextView tvHeroName;
    private TextView tvRarityBadge;
    private View rarityIndicatorCircle;
    private TextView tvFullName;
    private TextView tvPublisher;
    private TextView tvHeroId;
    private TextView tvTotalPower;

    // Estadísticas
    private TextView tvIntelligenceValue;
    private ProgressBar pbIntelligence;
    private TextView tvStrengthValue;
    private ProgressBar pbStrength;
    private TextView tvSpeedValue;
    private ProgressBar pbSpeed;
    private TextView tvDurabilityValue;
    private ProgressBar pbDurability;
    private TextView tvPowerValue;
    private ProgressBar pbPower;
    private TextView tvCombatValue;
    private ProgressBar pbCombat;

    private HeroCard currentHero;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_hero_detail;
    }

    @Override
    protected void initializeUI() {
        try {
            // Inicializar vistas principales
            btnBack = findViewById(R.id.btnBack);
            ivHeroImage = findViewById(R.id.ivHeroImage);
            tvHeroName = findViewById(R.id.tvHeroName);
            tvRarityBadge = findViewById(R.id.tvRarityBadge);
            rarityIndicatorCircle = findViewById(R.id.rarityIndicatorCircle);
            tvFullName = findViewById(R.id.tvFullName);
            tvPublisher = findViewById(R.id.tvPublisher);
            tvHeroId = findViewById(R.id.tvHeroId);
            tvTotalPower = findViewById(R.id.tvTotalPower);

            // Inicializar estadísticas
            tvIntelligenceValue = findViewById(R.id.tvIntelligenceValue);
            pbIntelligence = findViewById(R.id.pbIntelligence);
            tvStrengthValue = findViewById(R.id.tvStrengthValue);
            pbStrength = findViewById(R.id.pbStrength);
            tvSpeedValue = findViewById(R.id.tvSpeedValue);
            pbSpeed = findViewById(R.id.pbSpeed);
            tvDurabilityValue = findViewById(R.id.tvDurabilityValue);
            pbDurability = findViewById(R.id.pbDurability);
            tvPowerValue = findViewById(R.id.tvPowerValue);
            pbPower = findViewById(R.id.pbPower);
            tvCombatValue = findViewById(R.id.tvCombatValue);
            pbCombat = findViewById(R.id.pbCombat);

            // Obtener héroe del intent - CON VALIDACIÓN
            android.util.Log.d("HeroDetail", "Intentando obtener héroe del intent...");

            if (getIntent() == null) {
                android.util.Log.e("HeroDetail", "Intent es nulo!");
                showToast("Error: Intent inválido");
                finish();
                return;
            }

            currentHero = (HeroCard) getIntent().getSerializableExtra(HERO_EXTRA);

            if (currentHero == null) {
                android.util.Log.e("HeroDetail", "currentHero es nulo después de getSerializableExtra");
                android.util.Log.e("HeroDetail", "Intent extras: " + getIntent().getExtras());
                showToast("Error: No se pudo cargar el héroe");
                finish();
                return;
            }

            android.util.Log.d("HeroDetail", "✓ Héroe cargado: " + currentHero.getName());
            displayHeroDetails();

        } catch (Exception e) {
            android.util.Log.e("HeroDetail", "Error crítico en initializeUI", e);
            showToast("Error: " + e.getMessage());
            finish();
        }
    }

    @Override
    protected void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Muestra todos los detalles del héroe en la interfaz
     */
    private void displayHeroDetails() {
        // Información básica
        tvHeroName.setText(currentHero.getName());
        tvHeroId.setText("#" + String.format("%03d", Integer.parseInt(currentHero.getId())));
        tvTotalPower.setText(String.valueOf(currentHero.getTotalPower()));

        // Parsear información de biografía
        String biographyText = currentHero.getBiography();
        if (biographyText != null && !biographyText.isEmpty()) {
            String[] parts = biographyText.split(" - ");
            if (parts.length >= 2) {
                tvFullName.setText("Nombre Completo: " + parts[0]);
                tvPublisher.setText("Editorial: " + parts[1]);
            } else {
                tvFullName.setText("Nombre Completo: " + biographyText);
                tvPublisher.setText("Editorial: Desconocida");
            }
        }

        // Configurar rareza y colores
        setRarityUI();

        // Cargar imagen
        loadHeroImage();

        // Mostrar estadísticas
        displayStatistics();
    }

    /**
     * Configura la UI según la rareza del héroe
     */
    private void setRarityUI() {
        String rarity = currentHero.getRarity();
        tvRarityBadge.setText(rarity);

        int rarityColor = currentHero.getRarityColor();
        tvRarityBadge.setTextColor(getResources().getColor(rarityColor, null));
        rarityIndicatorCircle.setBackgroundColor(getResources().getColor(rarityColor, null));
    }

    /**
     * Carga la imagen del héroe usando Picasso
     */
    private void loadHeroImage() {
        String imageUrl = currentHero.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(ivHeroImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            android.util.Log.d("HeroDetail", "✓ Imagen cargada: " + imageUrl);
                        }

                        @Override
                        public void onError(Exception e) {
                            android.util.Log.e("HeroDetail", "✗ Error: " + e.getMessage());
                        }
                    });
        } else {
            ivHeroImage.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    /**
     * Muestra todas las estadísticas del héroe con colores dinámicos
     */
    private void displayStatistics() {
        PowerStats stats = currentHero.getPowerStats();
        if (stats == null) return;

        // Inteligencia
        displayStat(tvIntelligenceValue, pbIntelligence, stats.getIntelligence(), R.color.info_blue);

        // Fuerza
        displayStat(tvStrengthValue, pbStrength, stats.getStrength(), R.color.error_red);

        // Velocidad
        displayStat(tvSpeedValue, pbSpeed, stats.getSpeed(), R.color.orange_accent);

        // Durabilidad
        displayStat(tvDurabilityValue, pbDurability, stats.getDurability(), R.color.uncommon_color);

        // Poder
        displayStat(tvPowerValue, pbPower, stats.getPower(), R.color.epic_color);

        // Combate
        displayStat(tvCombatValue, pbCombat, stats.getCombat(), R.color.rare_color);
    }

    /**
     * Actualiza una estadística individual con valor, barra de progreso y color
     */
    private void displayStat(TextView valueTV, ProgressBar pb, int value, int colorResId) {
        // Asegurar que el valor esté en el rango 0-100
        value = Math.max(0, Math.min(100, value));

        valueTV.setText(value + "/100");
        valueTV.setTextColor(getResources().getColor(colorResId, null));

        pb.setProgress(value);

        // Colorear la barra de progreso
        pb.setProgressTintList(
                android.content.res.ColorStateList.valueOf(
                        getResources().getColor(colorResId, null)
                )
        );
    }

    /**
     * Método estático para iniciar la actividad desde otro lugar
     */
    public static void start(android.app.Activity activity, HeroCard heroCard) {
        Intent intent = new Intent(activity, HeroDetailActivity.class);
        intent.putExtra(HERO_EXTRA, heroCard);
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
