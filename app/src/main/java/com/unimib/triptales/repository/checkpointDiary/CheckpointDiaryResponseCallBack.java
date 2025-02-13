package com.unimib.triptales.repository.checkpointDiary;

import com.unimib.triptales.model.CheckpointDiary;

import java.util.List;

public interface CheckpointDiaryResponseCallBack {
    void onSuccessFromRemote(List<CheckpointDiary> checkpointDiaries);
    void onFailureFromRemote(Exception exception);
    void onSuccessDeleteFromRemote();

    void onSuccessFromLocal(List<CheckpointDiary> checkpointDiaries);
    void onFailureFromLocal(Exception exception);
    void onSuccessDeleteFromLocal();
    void onSuccessSelectionFromLocal(List<CheckpointDiary> checkpointDiaries);

}
