package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.unimib.triptales.model.Diary;

import java.util.HashMap;
import java.util.List;

@Dao
public interface DiaryDao {
    @Insert
    long insert(Diary diary);

    @Update
    void update(Diary diary);

    @Delete
    void delete(Diary diary);

    @Delete
    void deleteAll(List<Diary> diaries);

    @Query("UPDATE Diary SET diary_name = :newName WHERE id = :diaryId")
    void updateName(int diaryId, String newName);

    @Query("UPDATE Diary SET isSelected = :newIsSelected WHERE id = :diaryId")
    void updateIsSelected(int diaryId, boolean newIsSelected);

    @Query("SELECT * FROM Diary")
    List<Diary> getAllDiaries();

    @Query("SELECT * FROM Diary WHERE isSelected = 1")
    List<Diary> getSelectedDiaries();

    @Query("SELECT * FROM Diary WHERE userId = :userId")
    List<Diary> getAllDiariesByUserId(String userId);

    @Query("SELECT diary_country FROM Diary WHERE userId = :userId")
    List<String> getAllCountriesByUserId(String userId);

    @Transaction
    @Query("SELECT * FROM Diary WHERE id = :diaryId")
    LiveData<CompleteDiary> getCompleteDiary(int diaryId);

    @Query("UPDATE Diary SET diary_budget = :newBudget WHERE id = :diaryId")
    void updateBudget(int diaryId, String newBudget);

}
