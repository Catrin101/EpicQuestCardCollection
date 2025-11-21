package com.example.epicquestcardcollection.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    @Override
    public void onCardClick(HeroCard heroCard) {
        // TODO: En una futura iteración, mostrar detalles de la carta
        showToast("Carta: " + heroCard.getName() + " (" + heroCard.getRarity() + ")");
    }

    @Override
    public void onCardLongClick(HeroCard heroCard) {
        // TODO: En una futura iteración, mostrar opciones (eliminar, favorito, etc.)
        showToast("Mantén presionado: " + heroCard.getName());
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
        // Navegar de vuelta al Dashboard en lugar de cerrar la app
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
