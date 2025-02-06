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

    public long insertCheckpoint(Checkpoint checkpoint) {
        return checkpointDao.insertCheckpoint(checkpoint);
    }

    public void updateCheckpoint(Checkpoint checkpoint) {
        checkpointDao.updateCheckpoint(checkpoint);
    }

    public void updateCheckpointName(int checkpointId, String newName) {
        checkpointDao.updateCheckpointName(checkpointId, newName);
    }

    public void updateCheckpointDate(int checkpointId, String newDate) {
        checkpointDao.updateCheckpointDate(checkpointId, newDate);
    }

    public void updateCheckpointImageUri(int checkpointId, String newImageUri) {
        checkpointDao.updateCheckpointImageUri(checkpointId, newImageUri);
    }

    public void updateCheckpointLatitude(int checkpointId, double newLatitude) {
        checkpointDao.updateCheckpointLatitude(checkpointId, newLatitude);
    }

    public void updateCheckpointLongitude(int checkpointId, double newLongitude) {
        checkpointDao.updateCheckpointLongitude(checkpointId, newLongitude);
    }


    public void updateAllCheckpoints(List<Checkpoint> checkpoints) {
        checkpointDao.updateAllCheckpoints(checkpoints);
    }

    public void deleteCheckpoint(Checkpoint checkpoint) {
        checkpointDao.deleteCheckpoint(checkpoint);
    }

    @Override
    public void deleteAllCheckpoints(List<Checkpoint> checkpoints) {
        checkpointDao.deleteAllCheckpoints();
    }


    public List<Checkpoint> getAllCheckpoints() {
        return checkpointDao.getAllCheckpoints();
    }


    public Checkpoint getCheckpointById(int id) {
        return checkpointDao.getCheckpointById(id);
    }

    public void deleteCheckpointById(List<Integer> ids) {
        checkpointDao.deleteCheckpointById(ids);
    }

    public List<Checkpoint> searchCheckpointsByName(String query) {
        return checkpointDao.searchCheckpointsByName(query);
    }

    public List<Checkpoint> getCheckpointsByDiaryId(int diaryId) {
        return checkpointDao.getCheckpointsByDiaryId(diaryId);
    }

}
