    package com.unimib.triptales.adapters;

    import android.content.Context;
    import android.content.Intent;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.unimib.triptales.R;
    import com.unimib.triptales.ui.diario.DiaryActivity;
    import com.unimib.triptales.ui.diario.fragment.TappeFragment;

    import java.util.List;

    public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

        private Context context;
        private List<Diary> diaries;

        public DiaryAdapter(Context context, List<Diary> diaries) {
            this.context = context;
            this.diaries = diaries;
        }

        @NonNull
        @Override
        public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the layout for individual items in the RecyclerView
            View view = LayoutInflater.from(context).inflate(R.layout.item_card_diary, parent, false);
            return new DiaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
            Diary diary = diaries.get(position);

            // Set the diary name to the TextView
            holder.textViewDiaryName.setText(diary.getName());

            // Use the getStartMonthAbbreviation method to get the abbreviated month
            String startMonthAbbreviation = diary.getStartMonthAbbreviation();

            // Extract the year from the start date (use the last 4 characters of the start date)
            String startYear = diary.getStartDate().substring(diary.getStartDate().length() - 4);

            // Combine the month abbreviation and year and set it in the TextView
            String startMonthAndYear = startMonthAbbreviation + " " + startYear;
            holder.textViewDates.setText(startMonthAndYear); // Display the month abbreviation and year

            // Set the cover image (if available) or fallback to default cover
            holder.imageViewDiary.setImageResource(R.drawable.default_cover); // Fallback image
            if (diary.getCoverImageUri() != null) {
                holder.imageViewDiary.setImageURI(diary.getCoverImageUri());
            }

            // Calculate the travel duration and display only the number of days
            int duration = diary.getTravelDuration();
            holder.textViewDuration.setText(String.valueOf(duration)); // Display only the number of days

            // Aggiungi il click listener per avviare DiaryActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DiaryActivity.class);
                intent.putExtra("diaryName", diary.getName());
                intent.putExtra("startDate", diary.getStartDate());
                intent.putExtra("endDate", diary.getEndDate());
                if (diary.getCoverImageUri() != null) {
                    intent.putExtra("coverImageUri", diary.getCoverImageUri());  // Passa direttamente l'Uri
                }
                context.startActivity(intent);
            });

        }

        @Override
        public int getItemCount() {
            return diaries.size();
        }

        public static class DiaryViewHolder extends RecyclerView.ViewHolder {

            ImageView imageViewDiary;
            TextView textViewDiaryName, textViewDates, textViewDuration;

            public DiaryViewHolder(View itemView) {
                super(itemView);
                imageViewDiary = itemView.findViewById(R.id.imageViewDiary);
                textViewDiaryName = itemView.findViewById(R.id.textViewDiaryName);
                textViewDates = itemView.findViewById(R.id.textViewStartDate);  // This is where we will display the month abbreviation and year
                textViewDuration = itemView.findViewById(R.id.textViewDuration);
            }
        }
        

    }
