package com.unimib.triptales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Goal;
import java.util.List;

public class GoalsRecyclerAdapter extends RecyclerView.Adapter<GoalsRecyclerAdapter.ViewHolder> {

    private List<Goal> goalsList;
    private Context context;
    private OnGoalClickListener onGoalClickListener;
    private OnGoalCheckBoxClickListener onGoalCheckBoxClickListener;

    public Context getContext() { return context; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalNameTextView, goalDescriptionTextView;
        CheckBox goalCheckBox;

        public ViewHolder(View view) {
            super(view);
            goalNameTextView = view.findViewById(R.id.goalNameTextView);
            goalDescriptionTextView = view.findViewById(R.id.goalDescriptionTextView);
            goalCheckBox = view.findViewById(R.id.cardGoalCheckBox);
        }
    }

    public GoalsRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setGoalsList(List<Goal> goals){
        this.goalsList = goals;
        notifyDataSetChanged();
    }

    public void setOnGoalClickListener(OnGoalClickListener listener){
        this.onGoalClickListener = listener;
    }

    public void setOnGoalCheckBoxClickListener(OnGoalCheckBoxClickListener listener){
        this.onGoalCheckBoxClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card_goal, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Goal goal = goalsList.get(position);

        viewHolder.goalNameTextView.setText(goal.getName());
        viewHolder.goalDescriptionTextView.setText(goal.getDescription());

        MaterialCardView card = (MaterialCardView) viewHolder.itemView;
        CheckBox checkBox = viewHolder.goalCheckBox;

        if (goal.isGoal_isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
            checkBox.setEnabled(false);
        } else if (goal.isGoal_isChecked()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            checkBox.setChecked(true);
            checkBox.setEnabled(true);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
        }


        card.setOnLongClickListener(view -> {
            if(onGoalClickListener != null){
                onGoalClickListener.onGoalClick(goal);
            }
            return false;
        });

        checkBox.setOnClickListener(view -> {
            if(onGoalCheckBoxClickListener != null){
                onGoalCheckBoxClickListener.onGoalCheckBoxClick(goal);
            }
        });

    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    public interface OnGoalCheckBoxClickListener {
        void onGoalCheckBoxClick(Goal goal);
    }
}
