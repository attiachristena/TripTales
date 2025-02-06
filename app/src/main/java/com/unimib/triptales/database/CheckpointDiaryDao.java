package com.unimib.triptales.database;

import androidx.room.OnConflictStrategy;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.unimib.triptales.model.CardItem;
import com.unimib.triptales.model.CheckpointDiary;

@Dao
public interface CheckpointDiaryDao {


    @Query("SELECT * FROM CheckpointDiary WHERE id = :id")
    LiveData<CheckpointDiary> getCheckpointDiaryById(int id);

    @Query("SELECT * FROM CheckpointDiary WHERE checkpoint_id = :checkpointId")
    LiveData<List<CheckpointDiary>> getCheckpointDiaryByCheckpointId(int checkpointId);
}
