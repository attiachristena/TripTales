package com.unimib.triptales.source.checkpointDiary;

import com.unimib.triptales.model.CheckpointDiary;
import com.unimib.triptales.repository.checkpointDiary.CheckpointDiaryResponseCallBack;

import java.util.List;

public abstract class BaseCheckpointDiaryRemoteDataSource {
    protected CheckpointDiaryResponseCallBack checkpointDiaryCallback;

    public void setCheckpointDiaryCallback(CheckpointDiaryResponseCallBack checkpointDiaryCallback){
        this.checkpointDiaryCallback = checkpointDiaryCallback;
    }

    public abstract void getAllCheckpointDiaries();
    public abstract void insertCheckpointDiary(CheckpointDiary checkpointDiary);
    public abstract void updateCheckpointDiary(CheckpointDiary checkpointDiary);
    public abstract void deleteCheckpointDiary(CheckpointDiary checkpointDiary);
    public abstract void deleteAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries);
    public abstract void updateCheckpointDiaryName(int checkpointId, String newName);
    public abstract void updateCheckpointDiaryDate(int checkpointId, String newDate);
    public abstract void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri);
    public abstract void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude);
    public abstract void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude);
}
