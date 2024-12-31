package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.unimib.triptales.model.Stage;

import java.util.List;

@Dao
public interface StageDao {
    @Insert
    void insert(Stage stage);

    @Insert
    void insertAll(List<Stage> stages);

    @Query("UPDATE Stage SET stage_name = :newName WHERE id = :stageId")
    void updateName(int stageId, String newName);

    @Query("UPDATE Stage SET stage_photo_path = :newPhotoPath WHERE id = :stageId")
    void updateDescription(int stageId, String newPhotoPath);

    @Delete
    void delete(Stage stage);

    @Delete
    void deleteAll(List<Stage> stages);

    //Recupero di un singolo elemento
    @Query("SELECT * FROM Stage WHERE id = :stageId")
    Stage getById(int stageId);

    //Recupero di una lista di elementi
    @Query("SELECT * FROM Stage")
    List<Stage> getAll();

    //Recupero delle spese di un determinato diario
    @Query("SELECT * FROM Stage WHERE diaryId = :diaryId")
    List<Stage> getAllByDiaryId(int diaryId);
}
