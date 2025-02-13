package com.unimib.triptales.ui.diary.viewmodel;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.R;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.Collections;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final ITaskRepository taskRepository;

    private final MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> selectedTasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> taskOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<String> taskEvent = new MutableLiveData<>();

    public TaskViewModel(ITaskRepository taskRepository, @NonNull Application application) {
        super(application);
        this.taskRepository = taskRepository;
    }

    public MutableLiveData<List<Task>> getTasksLiveData() {
        return tasksLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<List<Task>> getSelectedTasksLiveData() { return selectedTasksLiveData; }

    public MutableLiveData<Boolean> getTaskOverlayVisibility() { return taskOverlayVisibility; }

    public MutableLiveData<Boolean> getDeleteOverlayVisibility() { return deleteOverlayVisibility; }

    public MutableLiveData<String> getTaskEvent() { return taskEvent; }

    public void setTaskOverlayVisibility(boolean visible) {
        taskOverlayVisibility.postValue(visible);
    }

    public void setDeleteOverlayVisibility(boolean visible) {
        deleteOverlayVisibility.postValue(visible);
    }

    public boolean validateInputTask(String name){
        boolean correct = true;
        if (name.isEmpty()) {
            errorLiveData.setValue(getApplication().getString(R.string.errorTaskName));
        } else {
            errorLiveData.setValue(null);
        }

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
    }

    public void fetchAllTasks() {
        tasksLiveData.setValue(taskRepository.getAllTasks());
    }

    public void insertTask(String name, Context context) {
        String diaryId = SharedPreferencesUtils.getDiaryId(context);
        Task task = new Task(name, false, false, diaryId,
                System.currentTimeMillis());
        taskRepository.insertTask(task);
        fetchAllTasks();
        taskEvent.setValue(ADDED);
    }

    public void updateTask(Task task, String name){
        if(!task.getName().equals(name)){
            updateTaskName(task.getId(), name);
            task.setName(name);
        }
        fetchAllTasks();
        taskEvent.setValue(UPDATED);
    }

    public void updateTaskName(String taskId, String newName) {
        taskRepository.updateTaskName(taskId, newName);
        fetchAllTasks();
    }

    public void updateTaskIsSelected(String taskId, boolean newIsSelected) {
        taskRepository.updateTaskIsSelected(taskId, newIsSelected);
        fetchAllTasks();
    }

    public void updateTaskIsChecked(String taskId, boolean newIsChecked) {
        taskRepository.updateTaskIsChecked(taskId, newIsChecked);
        fetchAllTasks();
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

    public void toggleTaskSelection(Task task){
        boolean isSelected = task.isTask_isSelected();
        task.setTask_isSelected(!isSelected);
        updateTaskIsSelected(task.getId(), !isSelected);
        selectedTasksLiveData.setValue(taskRepository.getSelectedTasks());
        fetchAllTasks();
    }

    public void toggleTaskCheck(Task task){
        boolean isChecked = task.isTask_isChecked();
        task.setTask_isChecked(!isChecked);
        updateTaskIsChecked(task.getId(), !isChecked);
        fetchAllTasks();
    }

    public void deselectAllTasks() {
        fetchAllTasks();
        List<Task> tasks = tasksLiveData.getValue();
        if(tasks != null) {
            for (Task t : tasks) {
                t.setTask_isSelected(false);
                updateTaskIsSelected(t.getId(), false);
            }
            tasksLiveData.setValue(tasks);
            selectedTasksLiveData.postValue(Collections.emptyList());
            taskRepository.updateAllTasks(tasks);
        }
    }

}
