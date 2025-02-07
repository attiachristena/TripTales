package com.unimib.triptales.source.checkpoint;

import com.unimib.triptales.model.Checkpoint;

import java.util.List;

public interface BaseCheckpointLocalDataSource {

    List<Checkpoint> getCheckpointsByDiaryId(int diaryId);
    long insertCheckpoint(Checkpoint checkpoint);

    public interface Callback<T> {
        void onResult(T result);
    }
}
