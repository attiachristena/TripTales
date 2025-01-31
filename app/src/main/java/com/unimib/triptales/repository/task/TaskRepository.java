package com.unimib.triptales.repository.task;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.source.task.BaseTaskLocalDataSource;
import com.unimib.triptales.source.task.BaseTaskRemoteDataSource;

import java.util.List;

public class TaskRepository implements ITaskRepository, TaskResponseCallback{

    private final BaseTaskLocalDataSource taskLocalDataSource;
    private final BaseTaskRemoteDataSource taskRemoteDataSource;

    public TaskRepository(BaseTaskLocalDataSource taskLocalDataSource, BaseTaskRemoteDataSource taskRemoteDataSource) {
        this.taskLocalDataSource = taskLocalDataSource;
        this.taskRemoteDataSource = taskRemoteDataSource;
    }

    @Override
    public long insertTask(Task task) {
        return taskLocalDataSource.insertTask(task);
    }

    @Override
    public void updateAllTasks(List<Task> tasks) {
        taskLocalDataSource.updateAllTasks(tasks);
    }

    @Override
    public void updateTaskName(int taskId, String newName) {
        taskLocalDataSource.updateTaskName(taskId, newName);
    }

    @Override
    public void updateTaskIsSelected(int taskId, boolean newIsSelected) {
        taskLocalDataSource.updateTaskIsSelected(taskId, newIsSelected);
    }

    @Override
    public void updateTaskIsChecked(int taskId, boolean newIsChecked) {
        taskLocalDataSource.updateTaskIsChecked(taskId, newIsChecked);
    }

    @Override
    public void deleteTask(Task task) {
        taskLocalDataSource.deleteTask(task);
    }

    @Override
    public void deleteAllTasks(List<Task> tasks) {
        taskLocalDataSource.deleteAllTasks(tasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskLocalDataSource.getAllTasks();
    }

    @Override
    public List<Task> getSelectedTasks() {
        return taskLocalDataSource.getSelectedTasks();
    }

    @Override
    public void onSuccessFromLocal(List<Expense> expenses) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }
}
