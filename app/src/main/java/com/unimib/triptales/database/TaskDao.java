package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.unimib.triptales.model.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Query("UPDATE Task SET task_name = :newName WHERE id = :taskId")
    void updateName(int taskId, String newName);

    @Query("UPDATE Task SET task_isSelected = :newIsSelected WHERE id = :taskId")
    void updateIsSelected(int taskId, boolean newIsSelected);

    @Query("UPDATE Task SET task_isChecked = :newIsChecked WHERE id = :taskId")
    void updateIsChecked(int taskId, boolean newIsChecked);

    @Delete
    void delete(Task task);

    @Delete
    void deleteAll(List<Task> tasks);

    @Query("SELECT * FROM Task")
    List<Task> getAll();

    @Query("SELECT * FROM Task WHERE task_isSelected = 1")
    List<Task> getSelectedTasks();

    //Recupero delle spese di un determinato diario
    /*@Query("SELECT * FROM Task WHERE diaryId = :diaryId")
    List<Task> getAllByDiaryId(int diaryId);*/
}
