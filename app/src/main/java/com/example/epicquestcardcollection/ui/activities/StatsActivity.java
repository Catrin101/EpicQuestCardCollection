package com.example.epicquestcardcollection.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;
import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad de Estadísticas y Perfil del Usuario
 * Muestra información completa del jugador y permite interacciones avanzadas
 */
public class StatsActivity extends BaseActivity {

    private static final String TAG = "StatsActivity";

    // UI Components - Perfil
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvPlayerLevel;
    private TextView tvMemberSince;

    // UI Components - Estadísticas
    private TextView tvTotalCards;
    private TextView tvCommonCards;
    private TextView tvUncommonCards;
    private TextView tvRareCards;
    private TextView tvEpicCards;
    private TextView tvLegendaryCards;
    private TextView tvTotalPower;
    private TextView tvAveragePower;
    private TextView tvOpportunitiesLeft;

    // UI Components - Botones de Acción
    private Button btnShareCollection;
    private Button btnChangePhotoCamera;
    private Button btnChangePhotoFile;
    private Button btnFindCollectors;
    private Button btnBack;

    // Data
    private UserRepository userRepository;
    private User currentUser;
    private Uri photoUri;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> fileLauncher;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_stats;
    }

    @Override
    protected void initializeUI() {
        // Inicializar Permission Manager
        initializePermissionManager();

        // Inicializar vistas - Perfil
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvPlayerLevel = findViewById(R.id.tvPlayerLevel);
        tvMemberSince = findViewById(R.id.tvMemberSince);

        // Inicializar vistas - Estadísticas
        tvTotalCards = findViewById(R.id.tvTotalCards);
        tvCommonCards = findViewById(R.id.tvCommonCards);
        tvUncommonCards = findViewById(R.id.tvUncommonCards);
        tvRareCards = findViewById(R.id.tvRareCards);
        tvEpicCards = findViewById(R.id.tvEpicCards);
        tvLegendaryCards = findViewById(R.id.tvLegendaryCards);
        tvTotalPower = findViewById(R.id.tvTotalPower);
        tvAveragePower = findViewById(R.id.tvAveragePower);
        tvOpportunitiesLeft = findViewById(R.id.tvOpportunitiesLeft);

        // Inicializar botones
        btnShareCollection = findViewById(R.id.btnShareCollection);
        btnChangePhotoCamera = findViewById(R.id.btnChangePhotoCamera);
        btnChangePhotoFile = findViewById(R.id.btnChangePhotoFile);
        btnFindCollectors = findViewById(R.id.btnFindCollectors);
        btnBack = findViewById(R.id.btnBack);

        // Inicializar repositorio
        userRepository = new UserRepositoryImpl(this);
        currentUser = userRepository.getCurrentUser();

        // Configurar Activity Result Launchers
        setupActivityResultLaunchers();

        // Cargar datos
        loadUserData();
    }

    @Override
    protected void setupListeners() {
        btnShareCollection.setOnClickListener(v -> shareCollection());
        btnChangePhotoCamera.setOnClickListener(v -> openCamera());
        btnChangePhotoFile.setOnClickListener(v -> openFilePicker());
        btnFindCollectors.setOnClickListener(v -> requestLocationAndFindCollectors());
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Configura los launchers para resultados de actividades
     */
    private void setupActivityResultLaunchers() {
        // Launcher para cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        showToast("Foto capturada exitosamente");
                        Log.d(TAG, "Foto guardada en: " + photoUri);
                        // Aquí podrías cargar la imagen en ivProfilePicture
                        // Picasso.get().load(photoUri).into(ivProfilePicture);
                    } else {
                        showToast("Captura cancelada");
                    }
                }
        );

        // Launcher para selector de archivos
        fileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        showToast("Imagen seleccionada exitosamente");
                        Log.d(TAG, "Imagen seleccionada: " + selectedImageUri);
                        // Aquí podrías cargar la imagen en ivProfilePicture
                        // Picasso.get().load(selectedImageUri).into(ivProfilePicture);
                    } else {
                        showToast("Selección cancelada");
                    }
                }
        );

        // Launcher para permiso de ubicación
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showToast("Permiso de ubicación concedido");
                        findNearbyCollectors();
                    } else {
                        showToast("Permiso de ubicación denegado");
                    }
                }
        );

        // Launcher para permiso de cámara
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showToast("Permiso de cámara concedido");
                        launchCamera();
                    } else {
                        showToast("Permiso de cámara denegado");
                    }
                }
        );
    }

    /**
     * Carga los datos del usuario y sus estadísticas
     */
    private void loadUserData() {
        if (currentUser == null) {
            showToast("Error: Usuario no encontrado");
            finish();
            return;
        }

        // Información del perfil
        tvUsername.setText(currentUser.getUsername());
        tvPlayerLevel.setText("Nivel " + currentUser.getPlayerLevel());

        // Fecha de registro
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String memberSince = "Miembro desde: " + dateFormat.format(new Date(currentUser.getCreatedAt()));
        tvMemberSince.setText(memberSince);

        // Calcular estadísticas de la colección
        calculateAndDisplayStats();
    }

    /**
     * Calcula y muestra las estadísticas de la colección
     */
    private void calculateAndDisplayStats() {
        List<HeroCard> collection = currentUser.getCollection();

        // Contar cartas por rareza
        Map<String, Integer> rarityCount = new HashMap<>();
        rarityCount.put("COMMON", 0);
        rarityCount.put("UNCOMMON", 0);
        rarityCount.put("RARE", 0);
        rarityCount.put("EPIC", 0);
        rarityCount.put("LEGENDARY", 0);

        int totalPower = 0;

        for (HeroCard card : collection) {
            String rarity = card.getRarity();
            rarityCount.put(rarity, rarityCount.get(rarity) + 1);
            totalPower += card.getTotalPower();
        }

        // Mostrar estadísticas
        int totalCards = collection.size();
        tvTotalCards.setText(String.valueOf(totalCards));
        tvCommonCards.setText(String.valueOf(rarityCount.get("COMMON")));
        tvUncommonCards.setText(String.valueOf(rarityCount.get("UNCOMMON")));
        tvRareCards.setText(String.valueOf(rarityCount.get("RARE")));
        tvEpicCards.setText(String.valueOf(rarityCount.get("EPIC")));
        tvLegendaryCards.setText(String.valueOf(rarityCount.get("LEGENDARY")));

        tvTotalPower.setText(String.valueOf(totalPower));

        int averagePower = totalCards > 0 ? totalPower / totalCards : 0;
        tvAveragePower.setText(String.valueOf(averagePower));

        tvOpportunitiesLeft.setText(String.valueOf(currentUser.getDailyOpportunities()));
    }

    /**
     * Comparte la colección usando el Intent de compartir de Android
     */
    private void shareCollection() {
        if (currentUser == null) return;

        // Crear texto para compartir
        List<HeroCard> collection = currentUser.getCollection();
        StringBuilder shareText = new StringBuilder();
        shareText.append("🦸 Mi Colección Epic Quest 🦸\n\n");
        shareText.append("👤 Coleccionista: ").append(currentUser.getUsername()).append("\n");
        shareText.append("🃏 Total de cartas: ").append(collection.size()).append("\n\n");

        // Contar por rareza
        Map<String, Integer> rarityCount = new HashMap<>();
        rarityCount.put("COMMON", 0);
        rarityCount.put("UNCOMMON", 0);
        rarityCount.put("RARE", 0);
        rarityCount.put("EPIC", 0);
        rarityCount.put("LEGENDARY", 0);

        for (HeroCard card : collection) {
            rarityCount.put(card.getRarity(), rarityCount.get(card.getRarity()) + 1);
        }

        shareText.append("📊 Rarezas:\n");
        shareText.append("⚪ Comunes: ").append(rarityCount.get("COMMON")).append("\n");
        shareText.append("🟢 Poco Comunes: ").append(rarityCount.get("UNCOMMON")).append("\n");
        shareText.append("🔵 Raras: ").append(rarityCount.get("RARE")).append("\n");
        shareText.append("🟣 Épicas: ").append(rarityCount.get("EPIC")).append("\n");
        shareText.append("🟡 Legendarias: ").append(rarityCount.get("LEGENDARY")).append("\n\n");
        shareText.append("¡Únete y construye tu colección!");

        // Crear intent de compartir
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mi Colección Epic Quest");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        try {
            startActivity(Intent.createChooser(shareIntent, "Compartir colección con..."));
            Log.d(TAG, "Intent de compartir abierto exitosamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al compartir: ", e);
            showToast("Error al compartir la colección");
        }
    }

    /**
     * Abre la cámara para tomar una foto de perfil
     */
    private void openCamera() {
        // Verificar si tenemos permiso de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            // Ya tenemos permiso, abrir cámara
            launchCamera();
        }
    }

    /**
     * Lanza la cámara después de verificar permisos
     */
    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Crear archivo para guardar la foto
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraLauncher.launch(cameraIntent);
                Log.d(TAG, "Cámara abierta exitosamente");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error al crear archivo para foto: ", e);
            showToast("Error al abrir la cámara");
        }
    }

    /**
     * Crea un archivo temporal para guardar la foto
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "PROFILE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Abre el selector de archivos para elegir una foto de perfil
     */
    private void openFilePicker() {
        Intent fileIntent = new Intent(Intent.ACTION_PICK);
        fileIntent.setType("image/*");

        try {
            fileLauncher.launch(fileIntent);
            Log.d(TAG, "Selector de archivos abierto exitosamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir selector de archivos: ", e);
            showToast("Error al abrir selector de archivos");
        }
    }

    /**
     * Solicita permiso de ubicación y busca coleccionistas cercanos
     */
    private void requestLocationAndFindCollectors() {
        // Verificar si tenemos permiso de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // Ya tenemos permiso
            findNearbyCollectors();
        }
    }

    /**
     * Simula la búsqueda de coleccionistas cercanos
     */
    private void findNearbyCollectors() {
        showToast("Buscando coleccionistas cerca de ti...");
        Log.d(TAG, "Función de búsqueda de coleccionistas activada");

        // Simular búsqueda con un mensaje de ejemplo
        new android.os.Handler().postDelayed(() -> {
            showToast("Funcionalidad de búsqueda disponible en próxima versión");
        }, 1500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
