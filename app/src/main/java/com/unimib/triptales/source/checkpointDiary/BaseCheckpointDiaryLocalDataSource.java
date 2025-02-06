package com.unimib.triptales.source.checkpointDiary;

import com.unimib.triptales.model.CheckpointDiary;

import java.util.List;

public interface BaseCheckpointDiaryLocalDataSource {

    public interface Callback<T> {
        void onResult(T result);
    }

    List<CheckpointDiary> getCheckpointDiariesByDiaryId(int diaryId);
    List<CheckpointDiary> getAllCheckpointDiaries();
    void insertCheckpointDiary(CheckpointDiary checkpointDiary);
    void updateCheckpointDiary(CheckpointDiary checkpointDiary);
    void deleteCheckpointDiary(CheckpointDiary checkpointDiary);
    void deleteAllCheckpointDiaries();
    void deleteCheckpointDiaryById(List<Integer> ids);
    void updateCheckpointDiaryName(int checkpointId, String newName);
    void updateCheckpointDiaryDate(int checkpointId, String newDate);
    void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri);
    void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude);
    void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude);
    void updateAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries);

}
