    package com.unimib.triptales.adapters;

    import android.content.Context;
    import android.content.Intent;
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
    import com.unimib.triptales.model.Diary;
    import com.unimib.triptales.ui.diary.DiaryActivity;

    import java.util.List;

    import com.google.android.material.card.MaterialCardView;
    import com.unimib.triptales.util.SharedPreferencesUtils;

    import java.util.ArrayList;

    public class DiaryRecyclerAdapter extends RecyclerView.Adapter<DiaryRecyclerAdapter.DiaryViewHolder> {

        private Context context;
        private List<Diary> diaries;
        private OnDiaryLongClickListener onDiaryLongClicked;

        // Constructor
        public DiaryRecyclerAdapter(Context context) {
            this.context = context;
            this.diaries = new ArrayList<>();
        }

        // ViewHolder per la gestione dei widget nella carta
        public static class DiaryViewHolder extends RecyclerView.ViewHolder {

            MaterialCardView cardView;
            ImageView imageViewDiary;
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

        public void setDiaries(List<Diary> diaries) {
            this.diaries = diaries;
            notifyDataSetChanged();
        }

        public void setOnDiaryLongClicked(OnDiaryLongClickListener listener){
            this.onDiaryLongClicked = listener;
        }

        @NonNull
        @Override
        public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_diary, parent, false);

            if(this.context == null) this.context = parent.getContext();

            return new DiaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
            Diary diary = diaries.get(position);

            // Imposta il colore del bordo e dello sfondo in base allo stato di selezione
            if (diary.isDiary_isSelected()) {
                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.brown));
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.transparent));
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            }

            // Imposta i dati nella carta
            holder.textViewDiaryName.setText(diary.getName());

            // Usa il metodo getStartMonthAbbreviation per ottenere l'abbreviazione del mese
            String startMonthAbbreviation = diary.getStartMonthAbbreviation();
            String startYear = diary.getStartDate().substring(diary.getStartDate().length() - 4);
            String startMonthAndYear = startMonthAbbreviation + " " + startYear;
            holder.textViewDates.setText(startMonthAndYear);

            // Imposta l'immagine di copertina (se disponibile) o l'immagine di fallback
            if (diary.getCoverImageUri() != null && !diary.getCoverImageUri().isEmpty()) {
                holder.imageViewDiary.setImageURI(Uri.parse(diary.getCoverImageUri()));
            } else {
                holder.imageViewDiary.setImageResource(R.drawable.default_cover);
            }

            // Calcola la durata del viaggio e la mostra come numero di giorni
            int duration = diary.getTravelDuration();
            holder.textViewDuration.setText(String.valueOf(duration));

            // Click singolo per avviare DiaryActivity
            holder.itemView.setOnClickListener(v -> {
                String id = diary.getId();
                SharedPreferencesUtils.saveDiaryId(context, id);
                Intent intent = new Intent(context, DiaryActivity.class);
                intent.putExtra("diaryName", diary.getName());
                intent.putExtra("startDate", diary.getStartDate());
                intent.putExtra("endDate", diary.getEndDate());
                if (diary.getCoverImageUri() != null) {
                    intent.putExtra("coverImageUri", diary.getCoverImageUri());
                }
                context.startActivity(intent);
            });

            // Click prolungato per selezionare/deselezionare la carta
            holder.itemView.setOnLongClickListener(v -> {
                if(onDiaryLongClicked != null){
                    onDiaryLongClicked.onDiaryLongClicked(diary);
                }
                return false;
            });
        }


        @Override
        public int getItemCount() {
            return diaries.size();
        }

        // Interfaccia per notificare il Fragment quando un elemento è selezionato con click prolungato
        public interface OnDiaryLongClickListener {
            void onDiaryLongClicked(Diary diary);
        }
    }
