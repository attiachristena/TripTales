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
    void insert(Diary diary);

    @Insert
    void insertAll(List<Diary> diaries);

    @Update
    void update(Diary diary);

    @Delete
    void delete(Diary diary);

    @Delete
    void deleteAll(List<Diary> diaries);

    //Recupero di un singolo elemento
    @Query("SELECT * FROM Diary WHERE id = :diaryId")
    Diary getDiaryById(int diaryId);

    //Recupero di una lista di elementi
    @Query("SELECT * FROM Diary")
    List<Diary> getAllDiaries();

    //Recupero dei diari di un determinato user
    @Query("SELECT * FROM Diary WHERE userId = :userId")
    List<Diary> getAllDiariesByUserId(int userId);

    @Transaction
    @Query("SELECT * FROM Diary WHERE id = :diaryId")
    LiveData<CompleteDiary> getCompleteDiary(int diaryId);

}
