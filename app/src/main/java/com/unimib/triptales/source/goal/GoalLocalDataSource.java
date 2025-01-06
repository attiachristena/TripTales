package com.unimib.triptales.source.goal;

import com.unimib.triptales.database.GoalDao;
import com.unimib.triptales.model.Goal;

import java.util.List;

public class GoalLocalDataSource implements BaseGoalLocalDataSource{

    private final GoalDao goalDao;

    public GoalLocalDataSource(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    @Override
    public long insertGoal(Goal goal) {
        return goalDao.insert(goal);
    }

    @Override
    public void updateGoal(Goal goal) {
        goalDao.update(goal);
    }

    @Override
    public void updateGoalName(int goalId, String newName) {
        goalDao.updateName(goalId, newName);
    }

    @Override
    public void updateGoalDescription(int goalId, String newDescription) {
        goalDao.updateDescription(goalId, newDescription);
    }

    @Override
    public void updateGoalIsSelected(int goalId, boolean newIsSelected) {
        goalDao.updateIsSelected(goalId, newIsSelected);
    }

    @Override
    public void updateGoalIsChecked(int goalId, boolean newIsChecked) {
        goalDao.updateIsChecked(goalId, newIsChecked);
    }

    @Override
    public void deleteGoal(Goal goal) {
        goalDao.delete(goal);
    }

    @Override
    public void deleteAllGoals(List<Goal> goals) {
        goalDao.deleteAll(goals);
    }

    @Override
    public List<Goal> getAllGoals() {
        return goalDao.getAll();
    }

    @Override
    public List<Goal> getSelectedGoals() {
        return goalDao.getSelectedGoals();
    }

    @Override
    public List<Goal> getCheckedGoals() {
        return goalDao.getCheckedGoals();
    }
}
