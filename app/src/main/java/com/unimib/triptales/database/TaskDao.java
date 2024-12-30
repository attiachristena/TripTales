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
    void insert(Task task);

    @Insert
    void insertAll(List<Task> tasks);

    @Query("UPDATE Task SET task_name = :newName WHERE id = :taskId")
    void updateName(int taskId, String newName);

    @Delete
    void delete(Task task);

    @Delete
    void deleteAll(List<Task> tasks);

    //Recupero di un singolo elemento
    @Query("SELECT * FROM Task WHERE id = :taskId")
    Task getById(int taskId);

    //Recupero di una lista di elementi
    @Query("SELECT * FROM Task")
    List<Task> getAll();

    //Recupero delle spese di un determinato diario
    @Query("SELECT * FROM Task WHERE diaryId = :diaryId")
    List<Task> getAllByDiaryId(int diaryId);
}
