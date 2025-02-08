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
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Update
    void updateAll(List<Goal> goals);

    @Query("UPDATE Goal SET goal_name = :newName WHERE id = :goalId")
    void updateName(String goalId, String newName);

    @Query("UPDATE Goal SET goal_description = :newDescription WHERE id = :goalId")
    void updateDescription(String goalId, String newDescription);

    @Query("UPDATE Goal SET goal_isSelected = :newIsSelected WHERE id = :goalId")
    void updateIsSelected(String goalId, boolean newIsSelected);

    @Query("UPDATE Goal SET goal_isChecked = :newIsChecked WHERE id = :goalId")
    void updateIsChecked(String goalId, boolean newIsChecked);

    @Delete
    void delete(Goal goal);

    @Delete
    void deleteAll(List<Goal> goals);

    @Query("SELECT * FROM Goal WHERE diaryId = :diaryId ORDER BY goal_timestamp DESC")
    List<Goal> getAll(String diaryId);

    @Query("SELECT * FROM Goal WHERE goal_isSelected = 1 AND diaryId = :diaryId")
    List<Goal> getSelectedGoals(String diaryId);

    @Query("SELECT * FROM Goal WHERE goal_isChecked = 1 AND diaryId = :diaryId")
    List<Goal> getCheckedGoals(String diaryId);
}
