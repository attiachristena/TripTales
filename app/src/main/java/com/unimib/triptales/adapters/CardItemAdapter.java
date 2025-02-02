package com.unimib.triptales.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unimib.triptales.R;
import com.unimib.triptales.model.CardItem;

import java.util.List;

public class CardItemAdapter extends RecyclerView.Adapter<CardItemAdapter.CardItemViewHolder> {
    private List<CardItem> cardItems;

    // Costruttore
    public CardItemAdapter(List<CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    // ViewHolder definito come classe interna
    public static class CardItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView postImage;
        public TextView description;
        public TextView date;

        public CardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            postImage = itemView.findViewById(R.id.postImage);
            description = itemView.findViewById(R.id.subtitle);
            date = itemView.findViewById(R.id.dateHeader);
        }
    }

    @NonNull
    @Override
    public CardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_checkpoint_diary, parent, false);
        return new CardItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardItemViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);
        holder.title.setText(cardItem.getTitle());
        holder.description.setText(cardItem.getDescription());
        holder.date.setText(cardItem.getDate());
        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(cardItem.getImageUri()))
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }
}
