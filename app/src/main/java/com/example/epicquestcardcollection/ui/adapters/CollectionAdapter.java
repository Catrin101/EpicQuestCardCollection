package com.example.epicquestcardcollection.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.model.HeroCard;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adaptador para mostrar las cartas en un RecyclerView en formato de grid.
 * Maneja la visualización de cada carta con su imagen, nombre y rareza.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(HeroCard heroCard);
        void onCardLongClick(HeroCard heroCard);
    }

    private final List<HeroCard> cards;
    private final OnCardClickListener listener;

    public CollectionAdapter(List<HeroCard> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        HeroCard card = cards.get(position);
        holder.bind(card, listener);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCardImage;
        private final TextView tvCardName;
        private final TextView tvCardRarity;
        private final View rarityIndicator;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCardImage = itemView.findViewById(R.id.ivCardImage);
            tvCardName = itemView.findViewById(R.id.tvCardName);
            tvCardRarity = itemView.findViewById(R.id.tvCardRarity);
            rarityIndicator = itemView.findViewById(R.id.rarityIndicator);
        }

        public void bind(HeroCard card, OnCardClickListener listener) {
            // Cargar imagen
            Picasso.get()
                    .load(card.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivCardImage);

            // Configurar nombre y rareza
            tvCardName.setText(card.getName());
            tvCardRarity.setText(card.getRarity());

            // Configurar color de rareza
            int rarityColor = getRarityColor(card.getRarity());
            rarityIndicator.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), rarityColor)
            );

            // Configurar listeners
            itemView.setOnClickListener(v -> listener.onCardClick(card));
            itemView.setOnLongClickListener(v -> {
                listener.onCardLongClick(card);
                return true;
            });
        }

        private int getRarityColor(String rarity) {
            switch (rarity) {
                case "LEGENDARY":
                    return R.color.legendary_color;
                case "EPIC":
                    return R.color.epic_color;
                case "RARE":
                    return R.color.rare_color;
                case "UNCOMMON":
                    return R.color.uncommon_color;
                default: // COMMON
                    return R.color.common_color;
            }
        }
    }
}
