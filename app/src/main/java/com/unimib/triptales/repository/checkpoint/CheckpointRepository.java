package com.unimib.triptales.repository.checkpoint;

import com.unimib.triptales.model.Checkpoint;
import com.unimib.triptales.source.checkpoint.BaseCheckpointLocalDataSource;
import com.unimib.triptales.source.checkpoint.BaseCheckpointRemoteDataSource;

import java.util.List;

public class CheckpointRepository implements ICheckpointRepository{

    private final BaseCheckpointLocalDataSource checkpointLocalDataSource;
    // private final BaseCheckpointRemoteDataSource checkpointRemoteDataSource;

    public CheckpointRepository(BaseCheckpointLocalDataSource checkpointLocalDataSource) {
        this.checkpointLocalDataSource = checkpointLocalDataSource;
        //this.checkpointRemoteDataSource = checkpointRemoteDataSource;
    }

    public long insertCheckpoint(Checkpoint checkpoint) {
        return checkpointLocalDataSource.insertCheckpoint(checkpoint);
    }

    public void updateCheckpoint(Checkpoint checkpoint) {
        checkpointLocalDataSource.updateCheckpoint(checkpoint);
    }

    public void updateAllCheckpoints(List<Checkpoint> checkpoints) {
        checkpointLocalDataSource.updateAllCheckpoints(checkpoints);
    }

    public void updateCheckpointName(int checkpointId, String newName) {
        checkpointLocalDataSource.updateCheckpointName(checkpointId, newName);
    }

    public void updateCheckpointDate(int checkpointId, String newDate) {
        checkpointLocalDataSource.updateCheckpointDate(checkpointId, newDate);
    }

    public void updateCheckpointImageUri(int checkpointId, String newImageUri) {
        checkpointLocalDataSource.updateCheckpointImageUri(checkpointId, newImageUri);
    }

    public void updateCheckpointLatitude(int checkpointId, double newLatitude) {
        checkpointLocalDataSource.updateCheckpointLatitude(checkpointId, newLatitude);
    }

    public void updateCheckpointLongitude(int checkpointId, double newLongitude) {
        checkpointLocalDataSource.updateCheckpointLongitude(checkpointId, newLongitude);
    }


    public void deleteCheckpoint(Checkpoint checkpoint) {
        checkpointLocalDataSource.deleteCheckpoint(checkpoint);
    }

    public void deleteAllCheckpoints(List<Checkpoint> checkpoints) {
        checkpointLocalDataSource.deleteAllCheckpoints(checkpoints);
    }

    public List<Checkpoint> getAllCheckpoints() {
        return checkpointLocalDataSource.getAllCheckpoints();
    }

    public List<Checkpoint> searchCheckpointsByName(String query) {
        return checkpointLocalDataSource.searchCheckpointsByName(query);
    }

    public List<Checkpoint> getCheckpointsByDiaryId(int diaryId) {
        return checkpointLocalDataSource.getCheckpointsByDiaryId(diaryId);
    }



    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }


}
