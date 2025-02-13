package com.unimib.triptales.repository.checkpointDiary;

import com.unimib.triptales.model.CheckpointDiary;

import java.util.List;

public interface ICheckpointDiaryRepository {
    List<CheckpointDiary> getCheckpointDiariesByDiaryId(String diaryId);
    List<CheckpointDiary> getAllCheckpointDiaries();
    long insertCheckpointDiary(CheckpointDiary checkpointDiary);
    void deleteCheckpointDiary(CheckpointDiary checkpointDiary);
    void updateCheckpointDiaryName(int checkpointId, String newName);
    void updateCheckpointDiaryDate(int checkpointId, String newDate);
    void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri);
}
