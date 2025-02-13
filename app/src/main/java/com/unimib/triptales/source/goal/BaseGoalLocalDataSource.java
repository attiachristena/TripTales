package com.unimib.triptales.source.goal;

import com.unimib.triptales.model.Goal;
import com.unimib.triptales.repository.goal.GoalResponseCallback;

import java.util.List;

public abstract class BaseGoalLocalDataSource {
    protected GoalResponseCallback goalCallback;

    public void setGoalCallback(GoalResponseCallback goalCallback){
        this.goalCallback = goalCallback;
    }

    public abstract void insertGoal(Goal goal);
    public abstract void updateAllGoals(List<Goal> goals);
    public abstract void updateGoalName(String goalId, String newName);
    public abstract void updateGoalDescription(String goalId, String newDescription);
    public abstract void updateGoalIsSelected(String goalId, boolean newIsSelected);
    public abstract void updateGoalIsChecked(String goalId, boolean newIsChecked);
    public abstract void deleteAllGoals(List<Goal> goals);
    public abstract void getAllGoals();
    public abstract void getSelectedGoals();
    public abstract void getCheckedGoals();

}
