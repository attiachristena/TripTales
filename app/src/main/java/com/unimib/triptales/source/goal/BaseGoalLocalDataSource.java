package com.unimib.triptales.source.goal;

import com.unimib.triptales.model.Goal;

import java.util.List;

public interface BaseGoalLocalDataSource {

    long insertGoal(Goal goal);
    void updateGoal(Goal goal);
    void updateGoalName(int goalId, String newName);
    void updateGoalDescription(int goalId, String newDescription);
    void updateGoalIsSelected(int goalId, boolean newIsSelected);
    void updateGoalIsChecked(int goalId, boolean newIsChecked);
    void deleteGoal(Goal goal);
    void deleteAllGoals(List<Goal> goals);
    List<Goal> getAllGoals();
    //List<Goal> getAllGoalsByDiaryId(int diaryId);
    List<Goal> getSelectedGoals();
    List<Goal> getCheckedGoals();

}
