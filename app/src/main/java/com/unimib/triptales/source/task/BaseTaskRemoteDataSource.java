package com.unimib.triptales.source.task;

import com.unimib.triptales.model.Task;
import com.unimib.triptales.repository.task.TaskResponseCallback;

import java.util.List;

public abstract class BaseTaskRemoteDataSource {
    protected TaskResponseCallback taskCallback;

    public void setTaskCallback(TaskResponseCallback taskCallback){
        this.taskCallback = taskCallback;
    }

    public abstract void insertTask(Task task);
    public abstract void updateAllTasks(List<Task> tasks);
    public abstract void updateTaskName(String taskId, String newName);
    public abstract void updateTaskIsSelected(String taskId, boolean newIsSelected);
    public abstract void updateTaskIsChecked(String taskId, boolean newIsChecked);
    public abstract void deleteTask(Task task);
    public abstract void deleteAllTasks(List<Task> tasks);
    public abstract void getAllTasks();
}
