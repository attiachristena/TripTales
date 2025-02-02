package com.unimib.triptales.ui.diary.viewmodel;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Goal;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.repository.task.ITaskRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskViewModel extends ViewModel {

    private final ITaskRepository taskRepository;

    private final MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> selectedTasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> checkedTasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> taskOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<String> taskEvent = new MutableLiveData<>();

    public TaskViewModel(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public MutableLiveData<List<Task>> getTasksLiveData() {
        return tasksLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public MutableLiveData<List<Task>> getSelectedTasksLiveData() { return selectedTasksLiveData; }

    public MutableLiveData<List<Task>> getCheckedTasksLiveData() { return checkedTasksLiveData; }

    public MutableLiveData<Boolean> getTaskOverlayVisibility() { return taskOverlayVisibility; }

    public MutableLiveData<String> getTaskEvent() { return taskEvent; }

    public void setTaskOverlayVisibility(boolean visible) {
        taskOverlayVisibility.postValue(visible);
    }

    public boolean validateInputTask(String name){
        boolean correct = true;
        if (name.isEmpty()) {
            errorLiveData.setValue("Inserisci il nome dell'attività");
        } else {
            errorLiveData.setValue(null);
        }

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
    }

    public void fetchAllTasks() {
        loadingLiveData.setValue(true);
        try {
            List<Task> tasks = taskRepository.getAllTasks();
            tasksLiveData.postValue(tasks);
            loadingLiveData.postValue(false);
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public void insertTask(String name) {
        Task task = new Task(name, false, false);
        taskRepository.insertTask(task);
        fetchAllTasks();
        taskEvent.setValue(ADDED);
    }

    public void updateTask(Task task, String name){
        if(!task.getName().equals(name)){
            updateTaskName(task.getId(), name);
            task.setName(name);
        }
        taskEvent.setValue(UPDATED);
    }

    public void updateTaskName(int taskId, String newName) {
        loadingLiveData.setValue(true);
        try {
            taskRepository.updateTaskName(taskId, newName);
            fetchAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento del nome dell'attività: " + e.getMessage());
        }
    }

    public void updateTaskIsSelected(int taskId, boolean newIsSelected) {
        loadingLiveData.setValue(true);
        try {
            taskRepository.updateTaskIsSelected(taskId, newIsSelected);
            fetchAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento di isSelected: " + e.getMessage());
        }
    }

    public void updateTaskIsChecked(int taskId, boolean newIsChecked) {
        loadingLiveData.setValue(true);
        try {
            taskRepository.updateTaskIsChecked(taskId, newIsChecked);
            fetchAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento di isChecked: " + e.getMessage());
        }
    }

    public void deleteTask(Task task) {
        loadingLiveData.setValue(true);
        try {
            taskRepository.deleteTask(task);
            fetchAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione dell'attività: "+e.getMessage());
        }
    }

    public void deleteSelectedTasks() {
        List<Task> selectedTasks = getSelectedTasksLiveData().getValue();
        if(selectedTasks != null && !selectedTasks.isEmpty()) {
            taskRepository.deleteAllTasks(selectedTasks);
            selectedTasksLiveData.postValue(Collections.emptyList());
            fetchAllTasks();
            taskEvent.setValue(DELETED);
        } else {
            taskEvent.setValue(INVALID_DELETE);
        }
    }

    public List<Task> getAllTasks() {
        loadingLiveData.setValue(true);
        List<Task> tasks = new ArrayList<>();
        try {
            tasks = taskRepository.getAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione di tutte le attività: "+e.getMessage());
        }
        return tasks;
    }

    public List<Task> getSelectedTasks() {
        loadingLiveData.setValue(true);
        List<Task> tasks = new ArrayList<>();
        try {
            tasks = taskRepository.getSelectedTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione dell'attività selezionata: "+e.getMessage());
        }
        return tasks;
    }

    public void toggleTaskSelection(Task task){
        boolean isSelected = task.isSelected();
        task.setSelected(!isSelected);
        updateTaskIsSelected(task.getId(), !isSelected);
        List<Task> tasks = getSelectedTasks();
        selectedTasksLiveData.postValue(tasks);
        fetchAllTasks();
    }

    public void toggleTaskCheck(Task task){
        boolean isChecked = task.isChecked();
        task.setChecked(!isChecked);
        updateTaskIsChecked(task.getId(), !isChecked);
        fetchAllTasks();
    }

    public void deselectAllTasks() {
        List<Task> tasks = getAllTasks();
        if(tasks != null) {
            for (Task t : tasks) {
                t.setSelected(false);
                updateTaskIsSelected(t.getId(), false);
            }
            tasksLiveData.setValue(tasks);
            selectedTasksLiveData.postValue(Collections.emptyList());
            taskRepository.updateAllTasks(tasks);
        }
    }

}
