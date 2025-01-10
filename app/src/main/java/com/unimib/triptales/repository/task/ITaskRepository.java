package com.unimib.triptales.repository.task;

import com.unimib.triptales.model.Task;

import java.util.List;

public interface ITaskRepository {

    long insertTask(Task task);
    void updateAllTasks(List<Task> tasks);
    void updateTaskName(int taskId, String newName);
    void updateTaskIsSelected(int taskId, boolean newIsSelected);
    void updateTaskIsChecked(int taskId, boolean newIsChecked);
    void deleteTask(Task task);
    void deleteAllTasks(List<Task> tasks);
    List<Task> getAllTasks();
    List<Task> getSelectedTasks();
    //List<Task> getAllTasksByDiaryId(int diaryId);
}
