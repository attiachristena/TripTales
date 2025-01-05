    package com.unimib.triptales.adapters;

    import android.content.Context;
    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.unimib.triptales.R;
    import com.unimib.triptales.ui.diario.DiaryActivity;

    import java.util.List;

    import com.google.android.material.card.MaterialCardView;

    import java.util.ArrayList;

    public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

        private Context context;
        private List<Diary> diaries;
        private List<Diary> selectedDiaries = new ArrayList<>();
        private OnDiaryItemLongClickListener listener;

        // Constructor
        public DiaryAdapter(Context context, List<Diary> diaries, OnDiaryItemLongClickListener listener) {
            this.context = context;
            this.diaries = diaries;
            this.listener = listener;
        }

        @NonNull
        @Override
        public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_card_diary, parent, false);
            return new DiaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
            Diary diary = diaries.get(position);

            // Mostra lo stato selezionato usando il colore del bordo
            MaterialCardView cardView = holder.itemView.findViewById(R.id.cardViewDiary);
            if (selectedDiaries.contains(diary)) {
                cardView.setStrokeColor(context.getResources().getColor(R.color.brown)); // Bordo selezionato
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.card_selected_background)); // Cambia il colore di sfondo per l'effetto di oscuramento
                cardView.setAlpha(0.5f); // Oscura la card (puoi regolare il valore di alpha tra 0 e 1)
            } else {
                // Imposta il colore del bordo iniziale trasparente o un colore a tua scelta
                cardView.setStrokeColor(context.getResources().getColor(R.color.transparent)); // Nessun bordo
                cardView.setAlpha(1f); // Riporta la card alla visibilità completa
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.transparent)); // Rimuove eventuali cambiamenti sullo sfondo
            }

            // Click prolungato per selezionare/deselezionare la carta
            holder.itemView.setOnLongClickListener(v -> {
                if (selectedDiaries.contains(diary)) {
                    selectedDiaries.remove(diary);
                } else {
                    selectedDiaries.add(diary);
                }
                notifyItemChanged(position); // Aggiorna visivamente la carta
                listener.onDiaryItemLongClicked(diary); // Notifica il listener
                return true;
            });

            // Imposta gli altri dati nella carta
            holder.textViewDiaryName.setText(diary.getName());

            // Usa il metodo getStartMonthAbbreviation per ottenere l'abbreviazione del mese
            String startMonthAbbreviation = diary.getStartMonthAbbreviation();
            String startYear = diary.getStartDate().substring(diary.getStartDate().length() - 4);
            String startMonthAndYear = startMonthAbbreviation + " " + startYear;
            holder.textViewDates.setText(startMonthAndYear);

            // Imposta l'immagine di copertura (se disponibile) o l'immagine di fallback
            holder.imageViewDiary.setImageResource(R.drawable.default_cover);
            if (diary.getCoverImageUri() != null) {
                holder.imageViewDiary.setImageURI(diary.getCoverImageUri());
            }

            // Calcola la durata del viaggio e la mostra come numero di giorni
            int duration = diary.getTravelDuration();
            holder.textViewDuration.setText(String.valueOf(duration));

            // Click singolo per avviare DiaryActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DiaryActivity.class);
                intent.putExtra("diaryName", diary.getName());
                intent.putExtra("startDate", diary.getStartDate());
                intent.putExtra("endDate", diary.getEndDate());
                if (diary.getCoverImageUri() != null) {
                    intent.putExtra("coverImageUri", diary.getCoverImageUri());
                }
                context.startActivity(intent);
            });
        }



        @Override
        public int getItemCount() {
            return diaries.size();
        }

        // Restituisce la lista dei diari selezionati
        public List<Diary> getSelectedDiaries() {
            return selectedDiaries;
        }

        // Interfaccia per notificare il Fragment quando un elemento è selezionato con click prolungato
        public interface OnDiaryItemLongClickListener {
            void onDiaryItemLongClicked(Diary diary);
        }

        // ViewHolder per la gestione dei widget nella carta
        public static class DiaryViewHolder extends RecyclerView.ViewHolder {

            MaterialCardView cardView;
            ImageView imageViewDiary,imageViewCheckMark;
            TextView textViewDiaryName, textViewDates, textViewDuration;

            public DiaryViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardViewDiary);
                imageViewDiary = itemView.findViewById(R.id.imageViewDiary);
                textViewDiaryName = itemView.findViewById(R.id.textViewDiaryName);
                textViewDates = itemView.findViewById(R.id.textViewStartDate);
                textViewDuration = itemView.findViewById(R.id.textViewDuration);
            }
        }
    }
