package com.unimib.triptales.repository.goal;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.source.goal.BaseGoalLocalDataSource;
import com.unimib.triptales.source.goal.BaseGoalRemoteDataSource;

import java.util.List;

public class GoalRepository implements IGoalRepository, GoalResponseCallback{

    private final BaseGoalLocalDataSource goalLocalDataSource;
    private final BaseGoalRemoteDataSource goalRemoteDataSource;
    private final MutableLiveData<List<Goal>> goalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> selectedGoalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> checkedGoalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteRemoteLiveData = new MutableLiveData<>(false);

    public GoalRepository(BaseGoalLocalDataSource goalLocalDataSource, BaseGoalRemoteDataSource goalRemoteDataSource) {
        this.goalLocalDataSource = goalLocalDataSource;
        this.goalRemoteDataSource = goalRemoteDataSource;
        this.goalLocalDataSource.setGoalCallback(this);
        this.goalRemoteDataSource.setGoalCallback(this);
    }

    @Override
    public void insertGoal(Goal goal) {
        goalLocalDataSource.insertGoal(goal);
        goalRemoteDataSource.insertGoal(goal);
    }

    @Override
    public void updateGoal(Goal goal) {
        goalLocalDataSource.updateGoal(goal);
        goalRemoteDataSource.updateGoal(goal);
    }

    @Override
    public void updateAllGoals(List<Goal> goals) {
        goalLocalDataSource.updateAllGoals(goals);
        goalRemoteDataSource.updateAllGoals(goals);
    }

    @Override
    public void updateGoalName(String goalId, String newName) {
        goalLocalDataSource.updateGoalName(goalId, newName);
        goalRemoteDataSource.updateGoalName(goalId, newName);
    }

    @Override
    public void updateGoalDescription(String goalId, String newDescription) {
        goalLocalDataSource.updateGoalDescription(goalId, newDescription);
        goalRemoteDataSource.updateGoalDescription(goalId, newDescription);
    }

    @Override
    public void updateGoalIsSelected(String goalId, boolean newIsSelected) {
        goalLocalDataSource.updateGoalIsSelected(goalId, newIsSelected);
        goalRemoteDataSource.updateGoalIsSelected(goalId, newIsSelected);
    }

    @Override
    public void updateGoalIsChecked(String goalId, boolean newIsChecked) {
        goalLocalDataSource.updateGoalIsChecked(goalId, newIsChecked);
        goalRemoteDataSource.updateGoalIsChecked(goalId, newIsChecked);
    }

    @Override
    public void deleteGoal(Goal goal) {
        goalLocalDataSource.deleteGoal(goal);
        goalRemoteDataSource.deleteGoal(goal);
    }

    @Override
    public void deleteAllGoals(List<Goal> goals) {
        goalLocalDataSource.deleteAllGoals(goals);
        goalRemoteDataSource.deleteAllGoals(goals);
    }

    @Override
    public List<Goal> getAllGoals() {
        goalLocalDataSource.getAllGoals();
        goalRemoteDataSource.getAllGoals();
        return goalsLiveData.getValue();
    }

    @Override
    public List<Goal> getSelectedGoals() {
        goalLocalDataSource.getSelectedGoals();
        return selectedGoalsLiveData.getValue();
    }

    @Override
    public List<Goal> getCheckedGoals() {
        goalLocalDataSource.getCheckedGoals();
        return checkedGoalsLiveData.getValue();
    }

    @Override
    public void onSuccessDeleteFromRemote() {
        deleteRemoteLiveData.setValue(true);
    }

    @Override
    public void onSuccessFromRemote(List<Goal> goals) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(Boolean.TRUE.equals(deleteRemoteLiveData.getValue())) {
                for (Goal goal : goals) {
                    goalLocalDataSource.insertGoal(goal);
                }
                goalLocalDataSource.getAllGoals();
                deleteRemoteLiveData.postValue(false);
            }
        });
    }

    @Override
    public void onFailureFromRemote(Exception exception) {}

    @Override
    public void onSuccessFromLocal(List<Goal> goals) {
        goalsLiveData.setValue(goals);
        for(Goal goal : goals){
            goalRemoteDataSource.insertGoal(goal);
        }
    }

    @Override
    public void onSuccessSelectionFromLocal(List<Goal> goals) {
        selectedGoalsLiveData.setValue(goals);
    }

    @Override
    public void onSuccessCheckedFromLocal(List<Goal> goals) {
        checkedGoalsLiveData.setValue(goals);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {}
}
