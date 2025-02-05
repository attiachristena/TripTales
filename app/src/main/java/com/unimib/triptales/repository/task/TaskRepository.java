package com.unimib.triptales.repository.task;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.source.task.BaseTaskLocalDataSource;
import com.unimib.triptales.source.task.BaseTaskRemoteDataSource;

import java.util.List;

public class TaskRepository implements ITaskRepository, TaskResponseCallback{

    private final BaseTaskLocalDataSource taskLocalDataSource;
    private final BaseTaskRemoteDataSource taskRemoteDataSource;
    private final MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> selectedTasksLiveData = new MutableLiveData<>();

    public TaskRepository(BaseTaskLocalDataSource taskLocalDataSource, BaseTaskRemoteDataSource taskRemoteDataSource) {
        this.taskLocalDataSource = taskLocalDataSource;
        this.taskRemoteDataSource = taskRemoteDataSource;
        this.taskLocalDataSource.setTaskCallback(this);
        this.taskRemoteDataSource.setTaskCallback(this);
    }

    @Override
    public void insertTask(Task task) {
        taskLocalDataSource.insertTask(task);
        taskRemoteDataSource.insertTask(task);
    }

    @Override
    public void updateAllTasks(List<Task> tasks) {
        taskLocalDataSource.updateAllTasks(tasks);
        taskRemoteDataSource.updateAllTasks(tasks);
    }

    @Override
    public void updateTaskName(String taskId, String newName) {
        taskLocalDataSource.updateTaskName(taskId, newName);
        taskRemoteDataSource.updateTaskName(taskId, newName);
    }

    @Override
    public void updateTaskIsSelected(String taskId, boolean newIsSelected) {
        taskLocalDataSource.updateTaskIsSelected(taskId, newIsSelected);
        taskRemoteDataSource.updateTaskIsSelected(taskId, newIsSelected);
    }

    @Override
    public void updateTaskIsChecked(String taskId, boolean newIsChecked) {
        taskLocalDataSource.updateTaskIsChecked(taskId, newIsChecked);
        taskRemoteDataSource.updateTaskIsChecked(taskId, newIsChecked);
    }

    @Override
    public void deleteTask(Task task) {
        taskLocalDataSource.deleteTask(task);
        taskRemoteDataSource.deleteTask(task);
    }

    @Override
    public void deleteAllTasks(List<Task> tasks) {
        taskLocalDataSource.deleteAllTasks(tasks);
        taskRemoteDataSource.deleteAllTasks(tasks);
    }

    @Override
    public List<Task> getAllTasks() {
        taskLocalDataSource.getAllTasks();
        taskRemoteDataSource.getAllTasks();
        return tasksLiveData.getValue();
    }

    @Override
    public List<Task> getSelectedTasks() {
        taskLocalDataSource.getSelectedTasks();
        taskRemoteDataSource.getSelectedTasks();
        return selectedTasksLiveData.getValue();
    }


    @Override
    public void onSuccessFromRemote() {}

    @Override
    public void onSuccessFromRemote(List<Task> tasks) {}

    @Override
    public void onFailureFromRemote(Exception exception) {}

    @Override
    public void onSuccessFromLocal() {}

    @Override
    public void onSuccessFromLocal(List<Task> tasks) {
        tasksLiveData.setValue(tasks);
    }

    @Override
    public void onSuccessSelectionFromLocal(List<Task> tasks) {
        selectedTasksLiveData.setValue(tasks);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {}
}
