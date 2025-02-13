package com.unimib.triptales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.unimib.triptales.R;
import com.unimib.triptales.model.ImageCardItem;

import java.util.ArrayList;
import java.util.List;

public class ImageCardItemAdapter extends RecyclerView.Adapter<ImageCardItemAdapter.CardItemViewHolder> {
    private List<ImageCardItem> imageCardItems;
    private Context context;
    private OnImageCardItemClickListener onImageCardItemClickListener;
    private List<ImageCardItem> selectedItems = new ArrayList<>();

    public ImageCardItemAdapter(Context context) {
        this.imageCardItems = new ArrayList<>();
        this.context = context;
    }

    public void setOnImageCardItemClickListener(OnImageCardItemClickListener listener) {
        this.onImageCardItemClickListener = listener;
    }

    public void setImageCardItems(List<ImageCardItem> imageCardItems) {
        this.imageCardItems = imageCardItems;
        notifyDataSetChanged();
    }

    public List<ImageCardItem> getImageCardItems() {
        return imageCardItems;
    }

    public void toggleSelection(int position) {
        ImageCardItem item = imageCardItems.get(position);
        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
        } else {
            selectedItems.add(item);
        }
        notifyItemChanged(position);

        if (onImageCardItemClickListener != null) {
            onImageCardItemClickListener.onSelectionChanged(getSelectedItems());
        }
    }

    public List<ImageCardItem> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
        if (onImageCardItemClickListener != null) {
            onImageCardItemClickListener.onSelectionChanged(getSelectedItems());
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
        ImageCardItem imageCardItem = imageCardItems.get(position);

        holder.title.setText(imageCardItem.getTitle());
        holder.description.setText(imageCardItem.getDescription());
        holder.date.setText(imageCardItem.getDate());

        // Imposta lo sfondo della card in base allo stato di selezione
        if (selectedItems.contains(imageCardItem)) {
            holder.card.setStrokeColor(ContextCompat.getColor(context, R.color.black));
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            holder.card.setStrokeWidth(4);
        } else {
            holder.card.setStrokeColor(ContextCompat.getColor(context, R.color.white));
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white)); // Ripristina lo sfondo bianco
            holder.card.setStrokeWidth(0);
        }

        holder.card.setOnLongClickListener(v -> {
            toggleSelection(position);
            return true;
        });

        Glide.with(context)
                .load(imageCardItem.getImageUri())
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return imageCardItems.size();
    }

    public static class CardItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView postImage;
        public TextView description;
        public TextView date;
        public MaterialCardView card;

        public CardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            postImage = itemView.findViewById(R.id.postImage);
            description = itemView.findViewById(R.id.subtitle);
            date = itemView.findViewById(R.id.dateHeader);
            card = itemView.findViewById(R.id.item_card_checkpoint_diary);
        }
    }

    public interface OnImageCardItemClickListener {
        void onImageCardItemLongClick(ImageCardItem imageCardItem);
        void onSelectionChanged(List<ImageCardItem> selectedItems);
    }
}