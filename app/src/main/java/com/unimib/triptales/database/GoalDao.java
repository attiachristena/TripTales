package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.unimib.triptales.model.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    long insert(Goal goal);

    @Update
    void update(Goal goal);

    @Query("UPDATE Goal SET goal_name = :newName WHERE id = :goalId")
    void updateName(int goalId, String newName);

    @Query("UPDATE Goal SET goal_description = :newDescription WHERE id = :goalId")
    void updateDescription(int goalId, String newDescription);

    @Query("UPDATE Goal SET goal_isSelected = :newIsSelected WHERE id = :goalId")
    void updateIsSelected(int goalId, boolean newIsSelected);

    @Query("UPDATE Goal SET goal_isChecked = :newIsChecked WHERE id = :goalId")
    void updateIsChecked(int goalId, boolean newIsChecked);

    @Delete
    void delete(Goal goal);

    @Delete
    void deleteAll(List<Goal> goals);

    @Query("SELECT * FROM Goal")
    List<Goal> getAll();

    //Recupero delle spese di un determinato diario
    /*@Query("SELECT * FROM Goal WHERE diaryId = :diaryId")
    List<Goal> getAllByDiaryId(int diaryId);*/

    @Query("SELECT * FROM Goal WHERE goal_isSelected = 1")
    List<Goal> getSelectedGoals();

    @Query("SELECT * FROM Goal WHERE goal_isChecked = 1")
    List<Goal> getCheckedGoals();
}
