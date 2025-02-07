package com.unimib.triptales.repository.goal;

import com.unimib.triptales.model.Goal;

import java.util.List;

public interface IGoalRepository {

    void insertGoal(Goal goal);
    void updateGoal(Goal goal);
    void updateAllGoals(List<Goal> goals);
    void updateGoalName(String goalId, String newName);
    void updateGoalDescription(String goalId, String newDescription);
    void updateGoalIsSelected(String goalId, boolean newIsSelected);
    void updateGoalIsChecked(String goalId, boolean newIsChecked);
    void deleteGoal(Goal goal);
    void deleteAllGoals(List<Goal> goals);
    List<Goal> getAllGoals();
    List<Goal> getSelectedGoals();
    List<Goal> getCheckedGoals();
}
