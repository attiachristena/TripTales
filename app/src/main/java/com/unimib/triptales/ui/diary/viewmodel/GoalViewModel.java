package com.unimib.triptales.ui.diary.viewmodel;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Goal;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.Collections;
import java.util.List;

public class GoalViewModel extends ViewModel {

    private final IGoalRepository goalRepository;

    private final MutableLiveData<List<Goal>> goalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> selectedGoalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> checkedGoalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> goalOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<String> goalEvent = new MutableLiveData<>();

    public GoalViewModel(IGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public MutableLiveData<List<Goal>> getGoalsLiveData() {
        return goalsLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<List<Goal>> getSelectedGoalsLiveData() { return selectedGoalsLiveData; }

    public MutableLiveData<List<Goal>> getCheckedGoalsLiveData() { return checkedGoalsLiveData; }

    public MutableLiveData<Boolean> getGoalOverlayVisibility() { return goalOverlayVisibility; }

    public MutableLiveData<String> getGoalEvent() { return goalEvent; }

    public void setGoalOverlayVisibility(boolean visible) {
        goalOverlayVisibility.postValue(visible);
    }

    public boolean validateInputGoal(String name){
        boolean correct = true;
        if (name.isEmpty()) {
            errorLiveData.setValue("Inserisci il nome dell'obiettivo");
        } else {
            errorLiveData.setValue(null);
        }

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
    }

    public void fetchAllGoals() {
        goalsLiveData.setValue(goalRepository.getAllGoals());
    }

    public void insertGoal(String name, String description, Context context) {
        int diaryId = Integer.parseInt(SharedPreferencesUtils.getDiaryId(context));
        Goal goal = new Goal(name, description, false, false, diaryId);
        goalRepository.insertGoal(goal);
        fetchAllGoals();
        goalEvent.setValue(ADDED);
    }

    public void updateGoal(Goal goal, String name, String description) {
        if(!goal.getName().equals(name)){
            updateGoalName(goal.getId(), name);
            goal.setName(name);
        }
        if(!goal.getDescription().equals(description)){
            updateGoalDescription(goal.getId(), description);
            goal.setDescription(description);
        }
        fetchAllGoals();
        goalEvent.setValue(UPDATED);
    }

    public void updateGoalName(String goalId, String newName) {
        goalRepository.updateGoalName(goalId, newName);
        fetchAllGoals();
    }

    public void updateGoalDescription(String goalId, String newDescription) {
        goalRepository.updateGoalDescription(goalId, newDescription);
        fetchAllGoals();
    }

    public void updateGoalIsSelected(String goalId, boolean newIsSelected) {
        goalRepository.updateGoalIsSelected(goalId, newIsSelected);
        fetchAllGoals();
    }

    public void updateGoalIsChecked(String goalId, boolean newIsChecked) {
        goalRepository.updateGoalIsChecked(goalId, newIsChecked);
        fetchAllGoals();
    }

    public void deleteSelectedGoals() {
        List<Goal> selectedGoals = getSelectedGoalsLiveData().getValue();
        if(selectedGoals != null && !selectedGoals.isEmpty()) {
            goalRepository.deleteAllGoals(selectedGoals);
            selectedGoalsLiveData.postValue(Collections.emptyList());
            fetchAllGoals();
            goalEvent.setValue(DELETED);
        } else {
            goalEvent.setValue(INVALID_DELETE);
        }
    }

    public void getCheckedGoals() {
        checkedGoalsLiveData.setValue(goalRepository.getCheckedGoals());
        checkedGoalsLiveData.getValue();
    }

    public void toggleGoalSelection(Goal goal){
        boolean isSelected = goal.isGoal_isSelected();
        goal.setGoal_isSelected(!isSelected);
        updateGoalIsSelected(goal.getId(), !isSelected);
        selectedGoalsLiveData.setValue(goalRepository.getSelectedGoals());
        fetchAllGoals();
    }

    public void toggleGoalCheck(Goal goal){
        boolean isChecked = goal.isGoal_isChecked();
        goal.setGoal_isChecked(!isChecked);
        updateGoalIsChecked(goal.getId(), !isChecked);
        checkedGoalsLiveData.setValue(goalRepository.getCheckedGoals());
        fetchAllGoals();
    }

    public void deselectAllGoals() {
        fetchAllGoals();
        List<Goal> goals = goalsLiveData.getValue();
        if(goals != null) {
            for (Goal g : goals) {
                g.setGoal_isSelected(false);
                updateGoalIsSelected(g.getId(), false);
            }
            goalsLiveData.setValue(goals);
            selectedGoalsLiveData.postValue(Collections.emptyList());
            goalRepository.updateAllGoals(goals);
        }
    }

}
