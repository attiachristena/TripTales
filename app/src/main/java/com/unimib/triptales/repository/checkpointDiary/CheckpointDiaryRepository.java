package com.unimib.triptales.repository.checkpointDiary;

import com.unimib.triptales.database.ImageCardItemDao;
import com.unimib.triptales.database.CheckpointDiaryDao;
import com.unimib.triptales.model.CheckpointDiary;
import com.unimib.triptales.source.checkpointDiary.BaseCheckpointDiaryLocalDataSource;

import java.util.List;

public class CheckpointDiaryRepository implements ICheckpointDiaryRepository {

    public BaseCheckpointDiaryLocalDataSource checkpointDiaryLocalDataSource;

    public CheckpointDiaryRepository(BaseCheckpointDiaryLocalDataSource checkpointDiaryLocalDataSource) {
        this.checkpointDiaryLocalDataSource = checkpointDiaryLocalDataSource;
    }

    public List<CheckpointDiary> getCheckpointDiariesByDiaryId(int diaryId) {
        return checkpointDiaryLocalDataSource.getCheckpointDiariesByDiaryId(diaryId);
    }

    public List<CheckpointDiary> getAllCheckpointDiaries() {
        return checkpointDiaryLocalDataSource.getAllCheckpointDiaries();
    }

    public long insertCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryLocalDataSource.insertCheckpointDiary(checkpointDiary);
        return 0;
    }

    public void updateCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryLocalDataSource.updateCheckpointDiary(checkpointDiary);
    }

    public void deleteCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryLocalDataSource.deleteCheckpointDiary(checkpointDiary);
    }

    public void deleteAllCheckpointDiaries() {
        checkpointDiaryLocalDataSource.deleteAllCheckpointDiaries();
    }

    public void deleteCheckpointDiaryById(List<Integer> ids) {
        checkpointDiaryLocalDataSource.deleteCheckpointDiaryById(ids);
    }

    public void updateCheckpointDiaryName(int checkpointId, String newName) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryName(checkpointId, newName);
    }

    public void updateCheckpointDiaryDate(int checkpointId, String newDate) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryDate(checkpointId, newDate);
    }

    public void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryImageUri(checkpointId, newImageUri);
    }

    public void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryLatitude(checkpointId, newLatitude);
    }

    public void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude) {
        checkpointDiaryLocalDataSource.updateCheckpointDiaryLongitude(checkpointId, newLongitude);
    }

    public void updateAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries) {
        checkpointDiaryLocalDataSource.updateAllCheckpointDiaries(checkpointDiaries);
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

}
