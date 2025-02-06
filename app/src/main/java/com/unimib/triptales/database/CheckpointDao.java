package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.triptales.model.Checkpoint;

import java.util.List;

@Dao
public interface CheckpointDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCheckpoint(Checkpoint checkpoint);

    @Update
    void updateCheckpoint(Checkpoint checkpoint);

    @Update
    void updateAllCheckpoints(List<Checkpoint> checkpoints);

    @Query("UPDATE Checkpoint SET nome = :newName WHERE id = :checkpointId")
    void updateCheckpointName(int checkpointId, String newName);

    @Query("UPDATE Checkpoint SET data = :newDate WHERE id = :checkpointId")
    void updateCheckpointDate(int checkpointId, String newDate);

    @Query("UPDATE Checkpoint SET immagine_uri = :newImageUri WHERE id = :checkpointId")
    void updateCheckpointImageUri(int checkpointId, String newImageUri);

    @Query("UPDATE Checkpoint SET latitude = :newLatitude WHERE id = :checkpointId")
    void updateCheckpointLatitude(int checkpointId, double newLatitude);

    @Query("UPDATE Checkpoint SET longitude = :newLongitude WHERE id = :checkpointId")
    void updateCheckpointLongitude(int checkpointId, double newLongitude);


    @Query("SELECT * FROM Checkpoint ORDER BY data DESC")
    List<Checkpoint> getAllCheckpoints();

    @Query("SELECT * FROM Checkpoint WHERE id = :id")
    Checkpoint getCheckpointById(int id);

    @Delete
    void deleteCheckpoint(Checkpoint checkpoint);

    @Query("DELETE FROM Checkpoint WHERE id IN (:ids)")
    void deleteCheckpointById(List<Integer> ids);

    @Query("DELETE FROM Checkpoint")
    void deleteAllCheckpoints();

    @Query("SELECT nome FROM Checkpoint WHERE id = :id")
    String getName(int id);

    @Query("SELECT * FROM Checkpoint WHERE nome LIKE '%' || :searchTerm || '%'")
    List<Checkpoint> searchCheckpointsByName(String searchTerm);

    @Query("SELECT * FROM Checkpoint WHERE diary_id = :diaryId")
    List<Checkpoint> getCheckpointsByDiaryId(int diaryId);

}
