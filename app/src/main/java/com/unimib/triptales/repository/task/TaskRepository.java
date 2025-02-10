package com.unimib.triptales.repository.task;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.source.task.BaseTaskLocalDataSource;
import com.unimib.triptales.source.task.BaseTaskRemoteDataSource;

import java.util.List;

public class TaskRepository implements ITaskRepository, TaskResponseCallback{

    private final BaseTaskLocalDataSource taskLocalDataSource;
    private final BaseTaskRemoteDataSource taskRemoteDataSource;
    private final MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> selectedTasksLiveData = new MutableLiveData<>();
    private boolean remoteDelete = false;
    private boolean localDelete = false;

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
        return selectedTasksLiveData.getValue();
    }

    @Override
    public void onSuccessDeleteFromRemote() {
        remoteDelete = true;
    }

    @Override
    public void onSuccessFromRemote(List<Task> tasks) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(remoteDelete || !localDelete){
                for (Task task : tasks) {
                    taskLocalDataSource.insertTask(task);
                }
                taskLocalDataSource.getAllTasks();
                remoteDelete = false;
                localDelete = false;
            }
        });
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Log.e("FirebaseError", "Errore nel recupero remoto: " + exception.getMessage());
    }

    @Override
    public void onSuccessDeleteFromLocal() {
        localDelete = true;
    }

    @Override
    public void onSuccessFromLocal(List<Task> tasks) {
        tasksLiveData.setValue(tasks);
        for(Task task : tasks){
            taskRemoteDataSource.insertTask(task);
        }
    }

    @Override
    public void onSuccessSelectionFromLocal(List<Task> tasks) {
        selectedTasksLiveData.setValue(tasks);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Log.e("FirebaseError", "Errore nel recupero remoto: " + exception.getMessage());
    }
}
