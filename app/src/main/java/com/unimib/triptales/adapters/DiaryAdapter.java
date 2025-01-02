package com.unimib.triptales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.triptales.R;

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

        // Set the diary data to the views in the item card
        holder.textViewDiaryName.setText("Diario: " + diary.getStartDate() + " - " + diary.getEndDate());
        holder.textViewDates.setText(diary.getStartDate() + " - " + diary.getEndDate());

        // Set image URI to the ImageView (use Glide or Picasso if needed)
        if (diary.getCoverImageUri() != null) {
            holder.imageViewDiary.setImageURI(diary.getCoverImageUri());
        } else {
            holder.imageViewDiary.setImageResource(R.drawable.default_cover); // fallback image
        }
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewDiary;
        TextView textViewDiaryName, textViewDates;

        public DiaryViewHolder(View itemView) {
            super(itemView);
            imageViewDiary = itemView.findViewById(R.id.imageViewDiary);
            textViewDiaryName = itemView.findViewById(R.id.textViewDiaryName);
            textViewDates = itemView.findViewById(R.id.textViewDates);
        }
    }
}
