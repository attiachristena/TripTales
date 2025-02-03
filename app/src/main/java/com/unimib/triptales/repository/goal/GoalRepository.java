package com.unimib.triptales.repository.goal;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.source.goal.BaseGoalLocalDataSource;
import com.unimib.triptales.source.goal.BaseGoalRemoteDataSource;

import java.util.List;

public class GoalRepository implements IGoalRepository, GoalResponseCallback{

    private final BaseGoalLocalDataSource goalLocalDataSource;
    private final BaseGoalRemoteDataSource goalRemoteDataSource;

    public GoalRepository(BaseGoalLocalDataSource goalLocalDataSource, BaseGoalRemoteDataSource goalRemoteDataSource) {
        this.goalLocalDataSource = goalLocalDataSource;
        this.goalRemoteDataSource = goalRemoteDataSource;
    }

    @Override
    public long insertGoal(Goal goal) {
        return goalLocalDataSource.insertGoal(goal);
    }

    @Override
    public void updateGoal(Goal goal) {
        goalLocalDataSource.updateGoal(goal);
    }

    @Override
    public void updateAllGoals(List<Goal> goals) { goalLocalDataSource.updateAllGoals(goals); }

    @Override
    public void updateGoalName(int goalId, String newName) {
        goalLocalDataSource.updateGoalName(goalId, newName);
    }

    @Override
    public void updateGoalDescription(int goalId, String newDescription) {
        goalLocalDataSource.updateGoalDescription(goalId, newDescription);
    }

    @Override
    public void updateGoalIsSelected(int goalId, boolean newIsSelected) {
        goalLocalDataSource.updateGoalIsSelected(goalId, newIsSelected);
    }

    @Override
    public void updateGoalIsChecked(int goalId, boolean newIsChecked) {
        goalLocalDataSource.updateGoalIsChecked(goalId, newIsChecked);
    }

    @Override
    public void deleteGoal(Goal goal) {
        goalLocalDataSource.deleteGoal(goal);
    }

    @Override
    public void deleteAllGoals(List<Goal> goals) {
        goalLocalDataSource.deleteAllGoals(goals);
    }

    @Override
    public List<Goal> getAllGoals() {
        return goalLocalDataSource.getAllGoals();
    }

    @Override
    public List<Goal> getSelectedGoals() {
        return goalLocalDataSource.getSelectedGoals();
    }

    @Override
    public List<Goal> getCheckedGoals() {
        return goalLocalDataSource.getCheckedGoals();
    }

    @Override
    public void onSuccessFromLocal(List<Expense> expenses) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }
}
