package com.unimib.triptales.source.task;

import com.unimib.triptales.database.TaskDao;
import com.unimib.triptales.model.Task;

import java.util.List;

public class TaskLocalDataSource extends BaseTaskLocalDataSource{

    private final TaskDao taskDao;

    public TaskLocalDataSource(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void insertTask(Task task) {
        try{
            taskDao.insert(task);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateAllTasks(List<Task> tasks) {
        try{
            taskDao.updateAll(tasks);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateTaskName(String taskId, String newName) {
        try{
            taskDao.updateName(taskId, newName);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateTaskIsSelected(String taskId, boolean newIsSelected) {
        try{
            taskDao.updateIsSelected(taskId, newIsSelected);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateTaskIsChecked(String taskId, boolean newIsChecked) {
        try{
            taskDao.updateIsChecked(taskId, newIsChecked);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteTask(Task task) {
        try{
            taskDao.delete(task);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteAllTasks(List<Task> tasks) {
        try{
            taskDao.deleteAll(tasks);
            taskCallback.onSuccessFromLocal();
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getAllTasks() {
        try{
            taskCallback.onSuccessFromLocal(taskDao.getAll());
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getSelectedTasks() {
        try{
            taskCallback.onSuccessSelectionFromLocal(taskDao.getSelectedTasks());
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

}
