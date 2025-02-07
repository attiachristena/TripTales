package com.unimib.triptales.database;

import androidx.room.OnConflictStrategy;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.unimib.triptales.model.CheckpointDiary;

@Dao
public interface CheckpointDiaryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCheckpointDiary(CheckpointDiary checkpointDiary);

    @Update
    void updateCheckpointDiary(CheckpointDiary checkpointDiary);

    @Update
    void updateAllCheckpointsDiaries(List<CheckpointDiary> checkpointDiaries);

    @Query("UPDATE CheckpointDiary SET nome = :newName WHERE id = :checkpointId")
    void updateCheckpointDiaryName(int checkpointId, String newName);

    @Query("UPDATE CheckpointDiary SET data = :newDate WHERE id = :checkpointId")
    void updateCheckpointDiaryDate(int checkpointId, String newDate);

    @Query("UPDATE CheckpointDiary SET immagine_uri = :newImageUri WHERE id = :checkpointId")
    void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri);

    @Query("UPDATE CheckpointDiary SET latitude = :newLatitude WHERE id = :checkpointId")
    void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude);

    @Query("UPDATE CheckpointDiary SET longitude = :newLongitude WHERE id = :checkpointId")
    void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude);

    @Query("SELECT * FROM CheckpointDiary ORDER BY data DESC")
    List<CheckpointDiary> getAllCheckpointDiaries();

    @Delete
    void deleteCheckpointDiary(CheckpointDiary checkpointDiary);

    @Query("DELETE FROM Checkpoint WHERE id IN (:ids)")
    void deleteCheckpointDiaryById(List<Integer> ids);

    @Query("DELETE FROM Checkpoint")
    void deleteAllCheckpointDiaries();

    @Query("SELECT nome FROM CheckpointDiary WHERE id = :id")
    String getName(int id);

    @Query("SELECT * FROM CheckpointDiary WHERE diary_id = :diaryId")
    List<CheckpointDiary> getCheckpointDiaryById(int diaryId);

    @Query("SELECT * FROM CheckpointDiary WHERE diary_id = :diaryId")
    LiveData<List<CheckpointDiary>> getCheckpointDiaryByCheckpointId(int diaryId);
}
