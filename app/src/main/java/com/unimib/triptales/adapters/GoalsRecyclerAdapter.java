package com.unimib.triptales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.GoalDao;
import com.unimib.triptales.model.Goal;

import java.util.List;

public class GoalsRecyclerAdapter extends RecyclerView.Adapter<GoalsRecyclerAdapter.ViewHolder> {

    private List<Goal> goalsList;
    private Context context;
    private FloatingActionButton addGoal;
    private FloatingActionButton deleteGoal;
    private FloatingActionButton modifyGoal;
    private CircularProgressIndicator progressIndicator;
    private TextView progressText;

    public List<Goal> getGoalsList() { return goalsList; }
    public Context getContext() { return context; }
    public FloatingActionButton getAddGoal() { return addGoal; }
    public FloatingActionButton getDeleteGoal() { return deleteGoal; }
    public FloatingActionButton getModifyGoal() { return modifyGoal; }
    public CircularProgressIndicator getProgressIndicator() { return progressIndicator; }
    public TextView getProgressText() { return progressText; }

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

    public GoalsRecyclerAdapter(List<Goal> goalsList, Context context,
                                FloatingActionButton addGoal, FloatingActionButton modifyGoal,
                                FloatingActionButton deleteGoal,
                                CircularProgressIndicator progressIndicator,
                                TextView progressText) {
        this.goalsList = goalsList;
        this.context = context;
        this.addGoal = addGoal;
        this.modifyGoal = modifyGoal;
        this.deleteGoal = deleteGoal;
        this.progressIndicator = progressIndicator;
        this.progressText = progressText;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card_goal, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Goal goal = goalsList.get(position);

        viewHolder.goalNameTextView.setText(goal.getName());
        viewHolder.goalDescriptionTextView.setText(goal.getDescription());

        MaterialCardView card = (MaterialCardView) viewHolder.itemView;
        card.setCheckable(true);

        if (goal.isGoal_isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
        }

        if (goal.isGoal_isChecked()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            viewHolder.goalCheckBox.setChecked(true);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            viewHolder.goalCheckBox.setChecked(false);
        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FloatingActionButton addGoal = getAddGoal();
                FloatingActionButton modifyGoal = getModifyGoal();
                FloatingActionButton deleteGoal = getDeleteGoal();

                GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();

                addGoal.setEnabled(false);

                if (goal.isGoal_isSelected()) {
                    //Deseleziona la spesa
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
                    goal.setGoal_isSelected(false);
                    goalDao.updateIsSelected(goal.getId(), false);
                    boolean notSelectedAll = true;
                    for (Goal g : goalsList) {
                        if (g.isGoal_isSelected()) {
                            notSelectedAll = false;
                            break;
                        }
                    }
                    if (notSelectedAll) {
                        modifyGoal.setVisibility(View.GONE);
                        deleteGoal.setVisibility(View.GONE);
                        addGoal.setEnabled(true);
                    }
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                    card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
                    goal.setGoal_isSelected(true);
                    goalDao.updateIsSelected(goal.getId(), true);
                }

                List<Goal> selectedGoals = goalDao.getSelectedGoals();
                if (selectedGoals.size() == 1) {
                    modifyGoal.setVisibility(View.VISIBLE);
                    deleteGoal.setVisibility(View.VISIBLE);
                } else if (selectedGoals.size() == 2) {
                    modifyGoal.setVisibility(View.GONE);
                }

                return false;
            }
        });

        viewHolder.goalCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();
                if (!goal.isGoal_isChecked()){
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
                    card.setChecked(true);
                    goal.setGoal_isChecked(true);
                    goalDao.updateIsChecked(goal.getId(), true);
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    card.setChecked(false);
                    goal.setGoal_isChecked(false);
                    goalDao.updateIsChecked(goal.getId(), false);
                }

                int progressPercentage;

                List<Goal> checkedGoals =
                        AppRoomDatabase.getDatabase(getContext()).goalDao().getCheckedGoals();
                double numAllCards = goalsList.size();
                double numCheckedCards = checkedGoals.size();
                if(goalsList.isEmpty()){
                    progressPercentage = 0;
                } else {
                    progressPercentage = (int) ((numCheckedCards / numAllCards) * 100);
                    progressIndicator.setProgress(progressPercentage);
                    String tmp = context.getString(R.string.numObiettivi, checkedGoals.size(), goalsList.size());
                    progressText.setText(tmp);
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return goalsList.size();
    }

}
