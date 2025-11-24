package com.example.epicquestcardcollection.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.repository.UserRepository;
import com.example.epicquestcardcollection.data.repository.UserRepositoryImpl;
import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.User;
import com.example.epicquestcardcollection.ui.adapters.CollectionAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para mostrar la colección de cartas del usuario.
 * Permite ver todas las cartas obtenidas, filtrar por rareza y ver estadísticas.
 */
public class CardCollectionActivity extends BaseActivity implements CollectionAdapter.OnCardClickListener {

    private RecyclerView recyclerViewCollection;
    private TextView tvEmptyState;
    private TextView tvCollectionStats;
    private ProgressBar progressCollection;
    private BottomNavigationView bottomNavigation;

    private UserRepository userRepository;
    private User currentUser;
    private CollectionAdapter collectionAdapter;
    private List<HeroCard> allCards;
    private List<HeroCard> filteredCards;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_card_collection;
    }

    @Override
    protected void initializeUI() {
        recyclerViewCollection = findViewById(R.id.recyclerViewCollection);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvCollectionStats = findViewById(R.id.tvCollectionStats);
        progressCollection = findViewById(R.id.progressCollection);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        userRepository = new UserRepositoryImpl(this);
        currentUser = userRepository.getCurrentUser();

        setupRecyclerView();
        setupBottomNavigation();
        loadUserCollection();
        updateCollectionStats();
    }

    @Override
    protected void setupListeners() {
        // Los listeners se manejan a través del adaptador y bottom navigation
    }

    private void setupRecyclerView() {
        allCards = new ArrayList<>();
        filteredCards = new ArrayList<>();

        // Configurar GridLayoutManager con 2 columnas
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewCollection.setLayoutManager(gridLayoutManager);

        // Crear y asignar adaptador
        collectionAdapter = new CollectionAdapter(filteredCards, this);
        recyclerViewCollection.setAdapter(collectionAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                navigateToDashboard();
                return true;
            } else if (itemId == R.id.navigation_obtain_cards) {
                navigateToCardObtain();
                return true;
            } else if (itemId == R.id.navigation_collection) {
                // Ya estamos en esta actividad
                return true;
            }
            return false;
        });

        // Marcar la opción actual como seleccionada
        bottomNavigation.setSelectedItemId(R.id.navigation_collection);
    }

    private void loadUserCollection() {
        if (currentUser != null) {
            allCards.clear();
            allCards.addAll(currentUser.getCollection());
            filteredCards.clear();
            filteredCards.addAll(allCards);

            // Actualizar adaptador
            collectionAdapter.notifyDataSetChanged();

            // Mostrar estado vacío si no hay cartas
            if (allCards.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerViewCollection.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                recyclerViewCollection.setVisibility(View.VISIBLE);
            }
        } else {
            showToast("Error: No se pudo cargar la colección");
        }
    }

    private void updateCollectionStats() {
        if (currentUser == null || allCards.isEmpty()) {
            tvCollectionStats.setText("Colección: 0 cartas");
            if (progressCollection != null) {
                progressCollection.setProgress(0);
            }
            return;
        }

        int totalCards = allCards.size();
        int common = 0, uncommon = 0, rare = 0, epic = 0, legendary = 0;

        for (HeroCard card : allCards) {
            switch (card.getRarity()) {
                case "COMMON":
                    common++;
                    break;
                case "UNCOMMON":
                    uncommon++;
                    break;
                case "RARE":
                    rare++;
                    break;
                case "EPIC":
                    epic++;
                    break;
                case "LEGENDARY":
                    legendary++;
                    break;
            }
        }

        String stats = String.format(
                "Colección: %d cartas\nC: %d | U: %d | R: %d | E: %d | L: %d",
                totalCards, common, uncommon, rare, epic, legendary
        );
        tvCollectionStats.setText(stats);

        // Actualizar barra de progreso
        if (progressCollection != null) {
            int maxCards = 100; // Puedes cambiar esto según tus necesidades
            int progress = Math.min((totalCards * 100) / maxCards, 100);
            progressCollection.setProgress(progress);
        }
    }

    /**
     * Filtra las cartas por rareza
     */
    public void filterByRarity(String rarity) {
        filteredCards.clear();

        if (rarity.equals("ALL")) {
            filteredCards.addAll(allCards);
        } else {
            for (HeroCard card : allCards) {
                if (card.getRarity().equals(rarity)) {
                    filteredCards.add(card);
                }
            }
        }

        collectionAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredCards.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewCollection.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewCollection.setVisibility(View.VISIBLE);
        }
    }

    // Metodo para verificar si HeroCard es serializable
    private void debugCardClick(HeroCard heroCard) {
        android.util.Log.d("CardClick", "======= DEBUG =======");
        android.util.Log.d("CardClick", "Héroe: " + heroCard.getName());
        android.util.Log.d("CardClick", "ID: " + heroCard.getId());
        android.util.Log.d("CardClick", "Rarity: " + heroCard.getRarity());
        android.util.Log.d("CardClick", "ImageURL: " + heroCard.getImageUrl());

        try {
            // Intentar serializar
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(heroCard);
            oos.close();
            android.util.Log.d("CardClick", "✓ Serialización exitosa");
        } catch (Exception e) {
            android.util.Log.e("CardClick", "✗ Error serializando: " + e.getMessage(), e);
        }
    }

    /**
     * Se ejecuta cuando el usuario hace clic en una tarjeta
     * Abre la actividad de detalles del héroe
     */
    @Override
    public void onCardClick(HeroCard heroCard) {
        debugCardClick(heroCard); // Agregar esta línea
        HeroDetailActivity.start(this, heroCard);
    }

    /**
     * Se ejecuta cuando el usuario mantiene presionada una tarjeta
     */
    @Override
    public void onCardLongClick(HeroCard heroCard) {
        showToast("Mantén presionado: " + heroCard.getName());
        // TODO: En una futura iteración, mostrar opciones (eliminar, favorito, etc.)
    }

    // Métodos para los filtros (se llaman desde el XML)
    public void onFilterAllClick(View view) {
        filterByRarity("ALL");
    }

    public void onFilterCommonClick(View view) {
        filterByRarity("COMMON");
    }

    public void onFilterUncommonClick(View view) {
        filterByRarity("UNCOMMON");
    }

    public void onFilterRareClick(View view) {
        filterByRarity("RARE");
    }

    public void onFilterEpicClick(View view) {
        filterByRarity("EPIC");
    }

    public void onFilterLegendaryClick(View view) {
        filterByRarity("LEGENDARY");
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCardObtain() {
        Intent intent = new Intent(this, CardObtainActivity.class);
        startActivity(intent);
        finish();
    }
}
