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

    @Query("SELECT * FROM Checkpoint WHERE diary_id = :diaryId")
    List<Checkpoint> getCheckpointsByDiaryId(int diaryId);
}
