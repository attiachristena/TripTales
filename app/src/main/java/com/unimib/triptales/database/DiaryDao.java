package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.unimib.triptales.model.Diary;

import java.util.List;

@Dao
public interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Diary diary);

    @Update
    void update(Diary diary);

    @Delete
    void delete(Diary diary);

    @Delete
    void deleteAll(List<Diary> diaries);

    @Query("UPDATE Diary SET diary_name = :newName WHERE id = :diaryId")
    void updateName(String diaryId, String newName);

    @Query("UPDATE Diary SET diary_isSelected = :newIsSelected WHERE id = :diaryId")
    void updateIsSelected(String diaryId, boolean newIsSelected);

    @Query("UPDATE Diary SET diary_start_date = :newStartDate WHERE id = :diaryId")
    void updateStartDate(String diaryId, String newStartDate);

    @Query("UPDATE Diary SET diary_end_date = :newEndDate WHERE id = :diaryId")
    void updateEndDate(String diaryId, String newEndDate);

    @Query("UPDATE Diary SET diary_cover_image_uri = :newCoverImage WHERE id = :diaryId")
    void updateCoverImage(String diaryId, String newCoverImage);

    @Query("UPDATE Diary SET diary_country = :newCountry WHERE id = :diaryId")
    void updateCountry(String diaryId, String newCountry);

    @Query("UPDATE Diary SET diary_budget = :newBudget WHERE id = :diaryId")
    void updateBudget(String diaryId, String newBudget);

    @Query("SELECT * FROM Diary WHERE userId = :userId ORDER BY diary_timestamp DESC")
    List<Diary> getAllDiaries(String userId);

    @Query("SELECT * FROM Diary WHERE diary_isSelected = 1 AND userId = :userId")
    List<Diary> getSelectedDiaries(String userId);

    @Query("SELECT diary_country FROM Diary WHERE userId = :userId")
    List<String> getAllCountries(String userId);

}
