package com.unimib.triptales.source.checkpointDiary;

import com.unimib.triptales.model.CheckpointDiary;
import com.unimib.triptales.repository.checkpointDiary.CheckpointDiaryResponseCallBack;

import java.util.List;

public abstract class BaseCheckpointDiaryLocalDataSource {
    protected CheckpointDiaryResponseCallBack checkpointDiaryCallback;

    public void setCheckpointDiaryCallback(CheckpointDiaryResponseCallBack checkpointDiaryCallback){
        this.checkpointDiaryCallback = checkpointDiaryCallback;
    }

    public abstract List<CheckpointDiary> getCheckpointDiariesByDiaryId(String diaryId);
    public abstract List<CheckpointDiary> getAllCheckpointDiaries();
    public abstract long insertCheckpointDiary(CheckpointDiary checkpointDiary);
    public abstract void updateCheckpointDiary(CheckpointDiary checkpointDiary);
    public abstract void deleteCheckpointDiary(CheckpointDiary checkpointDiary);
    public abstract void deleteAllCheckpointDiaries();
    public abstract void deleteCheckpointDiaryById(List<Integer> ids);
    public abstract void updateCheckpointDiaryName(int checkpointId, String newName);
    public abstract void updateCheckpointDiaryDate(int checkpointId, String newDate);
    public abstract void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri);
    public abstract void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude);
    public abstract void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude);
    public abstract void updateAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries);

}
