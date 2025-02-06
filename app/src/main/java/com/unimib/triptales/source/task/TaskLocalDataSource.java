package com.unimib.triptales.source.task;

import com.unimib.triptales.database.TaskDao;
import com.unimib.triptales.model.Task;

import java.util.List;

public class TaskLocalDataSource extends BaseTaskLocalDataSource{

    private final TaskDao taskDao;
    private final int diaryId;

    public TaskLocalDataSource(TaskDao taskDao, String diaryId) {
        this.taskDao = taskDao;
        this.diaryId = Integer.parseInt(diaryId);
    }

    @Override
    public void insertTask(Task task) {
        try{
            taskDao.insert(task);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateAllTasks(List<Task> tasks) {
        try{
            taskDao.updateAll(tasks);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateTaskName(String taskId, String newName) {
        try{
            taskDao.updateName(taskId, newName);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateTaskIsSelected(String taskId, boolean newIsSelected) {
        try{
            taskDao.updateIsSelected(taskId, newIsSelected);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateTaskIsChecked(String taskId, boolean newIsChecked) {
        try{
            taskDao.updateIsChecked(taskId, newIsChecked);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteTask(Task task) {
        try{
            taskDao.delete(task);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteAllTasks(List<Task> tasks) {
        try{
            taskDao.deleteAll(tasks);
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getAllTasks() {
        try{
            taskCallback.onSuccessFromLocal(taskDao.getAll(diaryId));
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getSelectedTasks() {
        try{
            taskCallback.onSuccessSelectionFromLocal(taskDao.getSelectedTasks(diaryId));
        } catch (Exception e){
            taskCallback.onFailureFromLocal(e);
        }
    }

}
