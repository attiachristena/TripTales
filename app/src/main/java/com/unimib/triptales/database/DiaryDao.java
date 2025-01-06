package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.unimib.triptales.model.Diary;

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

    @Query("SELECT * FROM Diary")
    List<Diary> getAllDiaries();

    @Query("SELECT * FROM Diary WHERE userId = :userId")
    List<Diary> getAllDiariesByUserId(int userId);

    @Query("SELECT diary_start_date FROM Diary")
    List<String> getStartDates();

    @Query("SELECT diary_end_date FROM Diary")
    List<String> getEndDates();

    @Transaction
    @Query("SELECT * FROM Diary WHERE id = :diaryId")
    LiveData<CompleteDiary> getCompleteDiary(int diaryId);

    @Query("UPDATE Diary SET diary_budget = :newBudget WHERE id = :diaryId")
    void updateBudget(int diaryId, String newBudget);

    /*//Recupero dei diari di un determinato utente
    @Query("SELECT * FROM Diary WHERE userId = :userId")
    List<Diary> getAllByUserId(int userId);*/

}
