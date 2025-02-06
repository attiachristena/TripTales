package com.unimib.triptales.source.checkpoint;

import com.unimib.triptales.database.CheckpointDao;
import com.unimib.triptales.model.Checkpoint;

import java.util.Collections;
import java.util.List;

public class CheckpointLocalDataSource implements BaseCheckpointLocalDataSource {

    private final CheckpointDao checkpointDao;

    public CheckpointLocalDataSource(CheckpointDao checkpointDao) {
        this.checkpointDao = checkpointDao;
    }

    public List<Checkpoint> getCheckpointsByDiaryId(int diaryId) {
        return checkpointDao.getCheckpointsByDiaryId(diaryId);
    }
    public long insertCheckpoint(Checkpoint checkpoint) {
        return checkpointDao.insertCheckpoint(checkpoint);
    }

}
