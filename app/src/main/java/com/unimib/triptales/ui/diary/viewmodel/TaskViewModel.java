package com.unimib.triptales.ui.diary.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Task;
import com.unimib.triptales.repository.task.ITaskRepository;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends ViewModel {

    private final ITaskRepository taskRepository;

    private final MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

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

    public long insertTask(Task task) {
        loadingLiveData.setValue(true);
        long id = 0;
        try {
            id = taskRepository.insertTask(task);
            fetchAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nell'inserimento dell'attività: "+e.getMessage());
        }
        return id;
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

    public void deleteAllTasks(List<Task> tasks) {
        loadingLiveData.setValue(true);
        try {
            taskRepository.deleteAllTasks(tasks);
            fetchAllTasks();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione della lista di attività: "+e.getMessage());
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

}
