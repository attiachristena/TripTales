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

    public List<Checkpoint> getCheckpointsByDiaryId(int diaryId) {
        return checkpointLocalDataSource.getCheckpointsByDiaryId(diaryId);
    }

    public long insertCheckpoint(Checkpoint checkpoint) {
        return checkpointLocalDataSource.insertCheckpoint(checkpoint);
    }
    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }


}
