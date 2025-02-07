package com.unimib.triptales.repository.checkpoint;

import com.unimib.triptales.model.Checkpoint;

import java.util.List;

public interface ICheckpointRepository {
    List<Checkpoint> getCheckpointsByDiaryId(int diaryId);

    long insertCheckpoint(Checkpoint checkpoint);

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
