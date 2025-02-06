package com.unimib.triptales.repository.checkpoint;

import com.unimib.triptales.model.Checkpoint;

import java.util.List;

public interface ICheckpointRepository {

    long insertCheckpoint(Checkpoint checkpoint);
    void updateCheckpoint(Checkpoint checkpoint);
    void updateAllCheckpoints(List<Checkpoint> checkpoints);
    void updateCheckpointName(int checkpointId, String newName);
    void updateCheckpointDate(int checkpointId, String newDate);
    void updateCheckpointImageUri(int checkpointId, String newImageUri);
    void updateCheckpointLatitude(int checkpointId, double newLatitude);
    void updateCheckpointLongitude(int checkpointId, double newLongitude);
    void deleteCheckpoint(Checkpoint checkpoint);
    void deleteAllCheckpoints(List<Checkpoint> checkpoints);
    List<Checkpoint> getAllCheckpoints();
    List<Checkpoint> searchCheckpointsByName(String query);
    List<Checkpoint> getCheckpointsByDiaryId(int diaryId);
}
