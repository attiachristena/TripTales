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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.CheckpointDao;
import com.unimib.triptales.model.Checkpoint;

import java.util.List;

public class TappaRecyclerAdapter extends RecyclerView.Adapter<TappaRecyclerAdapter.ViewHolder> {

    private final List<Checkpoint> checkpointList;
    private final Context context;
    private final FloatingActionButton modificaTappa;
    private final FloatingActionButton eliminaTappa;
    private final CheckpointDao checkpointDao;

    public TappaRecyclerAdapter(Context context, List<Checkpoint> checkpointList, FloatingActionButton modificaTappa, FloatingActionButton eliminaTappa) {
        this.context = context;
        this.checkpointList = checkpointList;
        this.modificaTappa = modificaTappa;
        this.eliminaTappa = eliminaTappa;
        this.checkpointDao = AppRoomDatabase.getDatabase(context).checkpointDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflaziona il layout della tappa (CardView)
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_card_checkpoint, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checkpoint checkpoint = checkpointList.get(position);

        holder.nomeTappa.setText(checkpoint.getNome());

        MaterialCardView card = (MaterialCardView) holder.itemView;
        aggiornaAspettoCard(card, checkpoint);

        holder.itemView.setOnLongClickListener(view -> {
            if (checkpoint.isTappa_isSelected()) {
                deselezionaTappa(card, checkpoint);
            } else {
                selezionaTappa(card, checkpoint);
            }
            aggiornaVisibilitaFAB();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return checkpointList.size();
    }

    private void aggiornaAspettoCard(MaterialCardView card, Checkpoint checkpoint) {
        if (checkpoint.isTappa_isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
        }
    }

    private void selezionaTappa(MaterialCardView card, Checkpoint checkpoint) {
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
        card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        checkpoint.setTappa_isSelected(true);
        checkpointDao.updateIsSelected(checkpoint.getId(), true);
    }

    private void deselezionaTappa(MaterialCardView card, Checkpoint checkpoint) {
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
        checkpoint.setTappa_isSelected(false);
        checkpointDao.updateIsSelected(checkpoint.getId(), false);
    }

    private void aggiornaVisibilitaFAB() {
        List<Checkpoint> selectedTappe = checkpointDao.getSelectedCheckpoint();

        if (selectedTappe.isEmpty()) {
            modificaTappa.setVisibility(View.GONE);
            eliminaTappa.setVisibility(View.GONE);
        } else if (selectedTappe.size() == 1) {
            modificaTappa.setVisibility(View.VISIBLE);
            eliminaTappa.setVisibility(View.VISIBLE);
        } else {
            modificaTappa.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTappa;
        ImageView immagineCard;

        public ViewHolder(View view) {
            super(view);
            nomeTappa = view.findViewById(R.id.checkpointNameCard);
            immagineCard = view.findViewById(R.id.checkpointCardImage);
        }
    }
}