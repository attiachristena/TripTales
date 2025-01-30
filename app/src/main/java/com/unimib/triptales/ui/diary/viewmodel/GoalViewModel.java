package com.unimib.triptales.ui.diary.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Goal;
import com.unimib.triptales.repository.goal.IGoalRepository;

import java.util.ArrayList;
import java.util.List;

public class GoalViewModel extends ViewModel {

    private final IGoalRepository goalRepository;

    private final MutableLiveData<List<Goal>> goalsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    public GoalViewModel(IGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public MutableLiveData<List<Goal>> getGoalsLiveData() {
        return goalsLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
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

    public long insertGoal(Goal goal) {
        loadingLiveData.setValue(true);
        long id = 0;
        try {
            id = goalRepository.insertGoal(goal);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nell'inserimento dell'obiettivo: "+e.getMessage());
        }
        return id;
    }

    public void updateGoal(Goal goal) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.updateGoal(goal);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nell'aggiornamento dell'obiettivo: "+e.getMessage());
        }
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

    public void deleteGoal(Goal goal) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.deleteGoal(goal);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione dell'obiettivo: "+e.getMessage());
        }
    }

    public void deleteAllGoals(List<Goal> goals) {
        loadingLiveData.setValue(true);
        try {
            goalRepository.deleteAllGoals(goals);
            fetchAllGoals();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione della lista di obiettivi: "+e.getMessage());
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
        } catch (Exception e) {
            loadingLiveData.postValue(false);
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
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione dell'obiettivo spuntato: "+e.getMessage());
        }
        return goals;
    }

}
