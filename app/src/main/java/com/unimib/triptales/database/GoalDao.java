package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.unimib.triptales.model.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(Goal goal);

    @Insert
    void insertAll(List<Goal> goals);

    @Query("UPDATE Goal SET goal_name = :newName WHERE id = :goalId")
    void updateName(int goalId, String newName);

    @Query("UPDATE Goal SET goal_description = :newDescription WHERE id = :goalId")
    void updateDescription(int goalId, String newDescription);

    @Delete
    void delete(Goal goal);

    @Delete
    void deleteAll(List<Goal> goals);

    //Recupero di un singolo elemento
    @Query("SELECT * FROM Goal WHERE id = :goalId")
    Goal getById(int goalId);

    //Recupero di una lista di elementi
    @Query("SELECT * FROM Goal")
    List<Goal> getAll();

    //Recupero delle spese di un determinato diario
    @Query("SELECT * FROM Goal WHERE diaryId = :diaryId")
    List<Goal> getAllByDiaryId(int diaryId);
}
