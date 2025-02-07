package com.unimib.triptales.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
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
    private OnImageCardItemClickListener OnImageCardItemClickListener;

    public Context getContext() { return context; }

    // Costruttore
    public ImageCardItemAdapter(Context context) {
        this.imageCardItems = new ArrayList<>();
        this.context = context;
    }

    public void setImageCardItems(List<ImageCardItem> imageCardItems) {
        this.imageCardItems = imageCardItems;
        notifyDataSetChanged();
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

    public interface OnImageCardItemClickListener {
        void onImageCardItemClick(ImageCardItem imageCardItem);
    }

    @NonNull
    @Override
    public CardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_checkpoint_diary, parent, false);

        if (this.context == null) this.context = parent.getContext();

        return new CardItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardItemViewHolder holder, int position) {
        ImageCardItem imageCardItem = imageCardItems.get(position);

        holder.title.setText(imageCardItem.getTitle());
        holder.description.setText(imageCardItem.getDescription());
        holder.date.setText(imageCardItem.getDate());

        MaterialCardView card = (MaterialCardView) holder.itemView;

        if (imageCardItem.isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        }

        card.setOnLongClickListener(v -> {
            if (OnImageCardItemClickListener != null) {
                OnImageCardItemClickListener.onImageCardItemClick(imageCardItem);
            }
            return false;
        });

        Glide.with(context)
                .load(imageCardItem.getImageUri())
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return imageCardItems.size();
    }

}
