package com.unimib.triptales.repository.checkpointDiary;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.model.CheckpointDiary;
import com.unimib.triptales.source.checkpointDiary.BaseCheckpointDiaryLocalDataSource;
import com.unimib.triptales.source.checkpointDiary.BaseCheckpointDiaryRemoteDataSource;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CheckpointDiaryRepository implements ICheckpointDiaryRepository, CheckpointDiaryResponseCallBack {

    private final BaseCheckpointDiaryLocalDataSource checkpointDiaryLocalDataSource;
    private final BaseCheckpointDiaryRemoteDataSource checkpointDiaryRemoteDataSource;
    private final MutableLiveData<List<CheckpointDiary>> checkpointDiariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CheckpointDiary>> selectedCheckpointDiariesLiveData = new MutableLiveData<>();
    private boolean remoteDelete = false;
    private boolean localDelete = false;
    private boolean isRemoteOperation = false;


    public CheckpointDiaryRepository(
            BaseCheckpointDiaryLocalDataSource checkpointDiaryLocalDataSource,
            BaseCheckpointDiaryRemoteDataSource checkpointDiaryRemoteDataSource) {
        this.checkpointDiaryLocalDataSource = checkpointDiaryLocalDataSource;
        this.checkpointDiaryRemoteDataSource = checkpointDiaryRemoteDataSource;
        this.checkpointDiaryLocalDataSource.setCheckpointDiaryCallback(this);
        this.checkpointDiaryRemoteDataSource.setCheckpointDiaryCallback(this);
    }

    @Override
    public List<CheckpointDiary> getCheckpointDiariesByDiaryId(String diaryId) {
        List<CheckpointDiary> checkpoints = checkpointDiaryLocalDataSource.getCheckpointDiariesByDiaryId(diaryId);
        checkpointDiariesLiveData.postValue(checkpoints);
        return checkpoints;
    }

    @Override
    public List<CheckpointDiary> getAllCheckpointDiaries() {
        checkpointDiaryLocalDataSource.getAllCheckpointDiaries();
        checkpointDiaryRemoteDataSource.getAllCheckpointDiaries();
        return checkpointDiariesLiveData.getValue();
    }

    @Override
    public long insertCheckpointDiary(CheckpointDiary checkpointDiary) {
        long id = checkpointDiaryLocalDataSource.insertCheckpointDiary(checkpointDiary);
        checkpointDiary.setId((int) id);

        isRemoteOperation = true;
        checkpointDiaryRemoteDataSource.insertCheckpointDiary(checkpointDiary);

        return id;
    }

    @Override
    public void updateCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryLocalDataSource.updateCheckpointDiary(checkpointDiary);
        checkpointDiaryRemoteDataSource.updateCheckpointDiary(checkpointDiary);
    }

    @Override
    public void deleteCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryLocalDataSource.deleteCheckpointDiary(checkpointDiary);
        checkpointDiaryRemoteDataSource.deleteCheckpointDiary(checkpointDiary);
    }

    @Override
    public void deleteAllCheckpointDiaries() {
        checkpointDiaryLocalDataSource.deleteAllCheckpointDiaries();
    }

    @Override
    public void deleteCheckpointDiaryById(List<Integer> ids) {
        checkpointDiaryLocalDataSource.deleteCheckpointDiaryById(ids);
    }

    @Override
    public void updateCheckpointDiaryName(int checkpointId, String newName) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryName(checkpointId, newName);
        checkpointDiaryRemoteDataSource.updateCheckpointDiaryName(checkpointId, newName);
    }

    @Override
    public void updateCheckpointDiaryDate(int checkpointId, String newDate) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryDate(checkpointId, newDate);
        checkpointDiaryRemoteDataSource.updateCheckpointDiaryDate(checkpointId, newDate);
    }

    @Override
    public void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryImageUri(checkpointId, newImageUri);
        checkpointDiaryRemoteDataSource.updateCheckpointDiaryImageUri(checkpointId, newImageUri);
    }

    @Override
    public void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryLatitude(checkpointId, newLatitude);
        checkpointDiaryRemoteDataSource.updateCheckpointDiaryLatitude(checkpointId, newLatitude);
    }

    @Override
    public void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryLongitude(checkpointId, newLongitude);
        checkpointDiaryRemoteDataSource.updateCheckpointDiaryLongitude(checkpointId, newLongitude);
    }

    @Override
    public void updateAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries) {
        checkpointDiaryLocalDataSource.updateAllCheckpointDiaries(checkpointDiaries);
    }

    @Override
    public void onSuccessDeleteFromRemote() {
        remoteDelete = true;
    }

    @Override
    public void onSuccessFromRemote(List<CheckpointDiary> checkpoints) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(remoteDelete || !localDelete){
                for (CheckpointDiary checkpoint : checkpoints) {
                    checkpointDiaryLocalDataSource.insertCheckpointDiary(checkpoint);
                }
                checkpointDiaryLocalDataSource.getAllCheckpointDiaries();
                remoteDelete = false;
                localDelete = false;
            }
        });
    }


    @Override
    public void onFailureFromRemote(Exception exception) {
    }

    @Override
    public void onSuccessDeleteFromLocal() {
        localDelete = true;
    }

    @Override
    public void onSuccessFromLocal(List<CheckpointDiary> checkpoints) {
        checkpointDiariesLiveData.setValue(checkpoints);
        for(CheckpointDiary checkpoint : checkpoints){
            checkpointDiaryRemoteDataSource.insertCheckpointDiary(checkpoint);
        }
    }

    @Override
    public void onSuccessSelectionFromLocal(List<CheckpointDiary> checkpoints) {
        selectedCheckpointDiariesLiveData.postValue(checkpoints);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
    }
}