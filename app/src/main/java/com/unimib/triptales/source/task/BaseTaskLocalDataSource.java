package com.unimib.triptales.source.task;

import com.unimib.triptales.model.Task;

import java.util.List;

public interface BaseTaskLocalDataSource {

    long insertTask(Task task);
    void updateTaskName(int taskId, String newName);
    void updateTaskIsSelected(int taskId, boolean newIsSelected);
    void updateTaskIsChecked(int taskId, boolean newIsChecked);
    void deleteTask(Task task);
    void deleteAllTasks(List<Task> tasks);
    List<Task> getAllTasks();
    List<Task> getSelectedTasks();
    //List<Task> getAllTasksByDiaryId(int diaryId);
}
