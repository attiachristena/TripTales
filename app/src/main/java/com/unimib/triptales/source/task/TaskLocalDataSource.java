package com.unimib.triptales.source.task;

import com.unimib.triptales.database.TaskDao;
import com.unimib.triptales.model.Task;

import java.util.List;

public class TaskLocalDataSource implements BaseTaskLocalDataSource{

    private final TaskDao taskDao;

    public TaskLocalDataSource(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public long insertTask(Task task) {
        return taskDao.insert(task);
    }

    @Override
    public void updateTaskName(int taskId, String newName) {
        taskDao.updateName(taskId, newName);
    }

    @Override
    public void updateTaskIsSelected(int taskId, boolean newIsSelected) {
        taskDao.updateIsSelected(taskId, newIsSelected);
    }

    @Override
    public void updateTaskIsChecked(int taskId, boolean newIsChecked) {
        taskDao.updateIsChecked(taskId, newIsChecked);
    }

    @Override
    public void deleteTask(Task task) {
        taskDao.delete(task);
    }

    @Override
    public void deleteAllTasks(List<Task> tasks) {
        taskDao.deleteAll(tasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskDao.getAll();
    }

    @Override
    public List<Task> getSelectedTasks() {
        return taskDao.getSelectedTasks();
    }
}
