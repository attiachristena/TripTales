package com.unimib.triptales.ui.diary.viewmodel;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Goal;
import com.unimib.triptales.repository.goal.IGoalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoalViewModel extends ViewModel {

    private final IGoalRepository goalRepository;

    private final MutableLiveData<List<Goal>> goalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> selectedGoalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> checkedGoalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
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
        loadingLiveData.setValue(true);
        try {
            List<Goal> goals = goalRepository.getAllGoals();
            goalsLiveData.postValue(goals);
            loadingLiveData.postValue(false);
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public void insertGoal(String name, String description) {
        Goal goal = new Goal(name, description, false, false);
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
        goalEvent.setValue(UPDATED);
    }

    public void updateGoalName(int goalId, String newName) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.updateGoalName(goalId, newName);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento del nome dell'obiettivo: " + e.getMessage());
        }
    }

    public void updateGoalDescription(int goalId, String newDescription) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.updateGoalDescription(goalId, newDescription);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della descrizione dell'obiettivo: " + e.getMessage());
        }
    }

    public void updateGoalIsSelected(int goalId, boolean newIsSelected) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.updateGoalIsSelected(goalId, newIsSelected);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento di isSelected: " + e.getMessage());
        }
    }

    public void updateGoalIsChecked(int goalId, boolean newIsChecked) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.updateGoalIsChecked(goalId, newIsChecked);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento di isChecked: " + e.getMessage());
        }
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

    public List<Goal> getAllGoals() {
        loadingLiveData.setValue(true);
        List<Goal> goals = new ArrayList<>();
        try {
            goals = goalRepository.getAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione di tutti gli obiettivi: "+e.getMessage());
        }
        return goals;
    }

    public List<Goal> getSelectedGoals() {
        loadingLiveData.setValue(true);
        List<Goal> goals = new ArrayList<>();
        try {
            goals = goalRepository.getSelectedGoals();
            selectedGoalsLiveData.postValue(goalRepository.getSelectedGoals());
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            selectedGoalsLiveData.postValue(Collections.emptyList());
            errorLiveData.postValue
                    ("Errore nella restituzione dell'obiettivo selezionato: "+e.getMessage());
        }
        return goals;
    }

    public List<Goal> getCheckedGoals() {
        loadingLiveData.setValue(true);
        List<Goal> goals = new ArrayList<>();
        try {
            goals = goalRepository.getCheckedGoals();
            checkedGoalsLiveData.postValue(goalRepository.getCheckedGoals());
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            checkedGoalsLiveData.postValue(Collections.emptyList());
            errorLiveData.postValue
                    ("Errore nella restituzione dell'obiettivo spuntato: "+e.getMessage());
        }
        return goals;
    }

    public void toggleGoalSelection(Goal goal){
        boolean isSelected = goal.isGoal_isSelected();
        goal.setGoal_isSelected(!isSelected);
        updateGoalIsSelected(goal.getId(), !isSelected);
        List<Goal> goals = getSelectedGoals();
        selectedGoalsLiveData.postValue(goals);
        fetchAllGoals();
    }

    public void toggleGoalCheck(Goal goal){
        boolean isChecked = goal.isGoal_isChecked();
        goal.setGoal_isChecked(!isChecked);
        updateGoalIsChecked(goal.getId(), !isChecked);
        List<Goal> goals = getCheckedGoals();
        checkedGoalsLiveData.postValue(goals);
        fetchAllGoals();
    }

    public void deselectAllGoals() {
        List<Goal> goals = getAllGoals();
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
