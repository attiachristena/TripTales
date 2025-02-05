package com.unimib.triptales.source.goal;

import com.unimib.triptales.database.GoalDao;
import com.unimib.triptales.model.Goal;

import java.util.List;

public class GoalLocalDataSource extends BaseGoalLocalDataSource{

    private final GoalDao goalDao;
    private final int diaryId;

    public GoalLocalDataSource(GoalDao goalDao, String diaryId) {
        this.goalDao = goalDao;
        this.diaryId = Integer.parseInt(diaryId);
    }

    @Override
    public void insertGoal(Goal goal) {
        try{
            goalDao.insert(goal);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateGoal(Goal goal) {
        try{
            goalDao.update(goal);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateAllGoals(List<Goal> goals) {
        try{
            goalDao.updateAll(goals);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateGoalName(String goalId, String newName) {
        try{
            goalDao.updateName(goalId, newName);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateGoalDescription(String goalId, String newDescription) {
        try{
            goalDao.updateDescription(goalId, newDescription);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateGoalIsSelected(String goalId, boolean newIsSelected) {
        try{
            goalDao.updateIsSelected(goalId, newIsSelected);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateGoalIsChecked(String goalId, boolean newIsChecked) {
        try{
            goalDao.updateIsChecked(goalId, newIsChecked);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteGoal(Goal goal) {
        try{
            goalDao.delete(goal);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteAllGoals(List<Goal> goals) {
        try{
            goalDao.deleteAll(goals);
            goalCallback.onSuccessFromLocal();
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getAllGoals() {
        try{
            goalCallback.onSuccessFromLocal(goalDao.getAll(diaryId));
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getSelectedGoals() {
        try{
            goalCallback.onSuccessSelectionFromLocal(goalDao.getSelectedGoals(diaryId));
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getCheckedGoals() {
        try{
            goalCallback.onSuccessCheckedFromLocal(goalDao.getCheckedGoals(diaryId));
        } catch (Exception e){
            goalCallback.onFailureFromLocal(e);
        }
    }
}
