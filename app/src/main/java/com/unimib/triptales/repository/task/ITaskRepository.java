package com.unimib.triptales.repository.task;

import com.unimib.triptales.model.Task;

import java.util.List;

public interface ITaskRepository {

    void insertTask(Task task);
    void updateAllTasks(List<Task> tasks);
    void updateTaskName(String taskId, String newName);
    void updateTaskIsSelected(String taskId, boolean newIsSelected);
    void updateTaskIsChecked(String taskId, boolean newIsChecked);
    void deleteAllTasks(List<Task> tasks);
    List<Task> getAllTasks();
    List<Task> getSelectedTasks();
}
