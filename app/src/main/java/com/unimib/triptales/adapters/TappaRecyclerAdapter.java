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
import com.unimib.triptales.database.TappaDao;
import com.unimib.triptales.model.Tappa;

import java.util.List;

public class TappaRecyclerAdapter extends RecyclerView.Adapter<TappaRecyclerAdapter.ViewHolder> {

    private final List<Tappa> tappaList;
    private final Context context;
    private final FloatingActionButton modificaTappa;
    private final FloatingActionButton eliminaTappa;
    private final TappaDao tappaDao;

    public TappaRecyclerAdapter(Context context, List<Tappa> tappaList, FloatingActionButton modificaTappa, FloatingActionButton eliminaTappa) {
        this.context = context;
        this.tappaList = tappaList;
        this.modificaTappa = modificaTappa;
        this.eliminaTappa = eliminaTappa;
        this.tappaDao = AppRoomDatabase.getDatabase(context).tappaDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflaziona il layout della tappa (CardView)
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_card_tappa, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tappa tappa = tappaList.get(position);

        holder.nomeTappa.setText(tappa.getNome());

        MaterialCardView card = (MaterialCardView) holder.itemView;
        aggiornaAspettoCard(card, tappa);

        holder.itemView.setOnLongClickListener(view -> {
            if (tappa.isTappa_isSelected()) {
                deselezionaTappa(card, tappa);
            } else {
                selezionaTappa(card, tappa);
            }
            aggiornaVisibilitaFAB();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tappaList.size();
    }

    private void aggiornaAspettoCard(MaterialCardView card, Tappa tappa) {
        if (tappa.isTappa_isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
        }
    }

    private void selezionaTappa(MaterialCardView card, Tappa tappa) {
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
        card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        tappa.setTappa_isSelected(true);
        tappaDao.updateIsSelected(tappa.getId(), true);
    }

    private void deselezionaTappa(MaterialCardView card, Tappa tappa) {
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
        tappa.setTappa_isSelected(false);
        tappaDao.updateIsSelected(tappa.getId(), false);
    }

    private void aggiornaVisibilitaFAB() {
        List<Tappa> selectedTappe = tappaDao.getSelectedTappe();

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
            nomeTappa = view.findViewById(R.id.nomeTappaCard);
            immagineCard = view.findViewById(R.id.anteprimaImmagineCard);
        }
    }
}