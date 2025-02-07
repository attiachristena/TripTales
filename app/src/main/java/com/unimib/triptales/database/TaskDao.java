package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.triptales.model.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void updateAll(List<Task> tasks);

    @Query("UPDATE Task SET task_name = :newName WHERE id = :taskId")
    void updateName(String taskId, String newName);

    @Query("UPDATE Task SET task_isSelected = :newIsSelected WHERE id = :taskId")
    void updateIsSelected(String taskId, boolean newIsSelected);

    @Query("UPDATE Task SET task_isChecked = :newIsChecked WHERE id = :taskId")
    void updateIsChecked(String taskId, boolean newIsChecked);

    @Delete
    void delete(Task task);

    @Delete
    void deleteAll(List<Task> tasks);

    @Query("SELECT * FROM Task WHERE diaryId = :diaryId")
    List<Task> getAll(int diaryId);

    @Query("SELECT * FROM Task WHERE task_isSelected = 1 AND diaryId = :diaryId")
    List<Task> getSelectedTasks(int diaryId);
}
