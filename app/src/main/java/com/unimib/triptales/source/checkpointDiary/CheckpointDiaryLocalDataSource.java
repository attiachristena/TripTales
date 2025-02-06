package com.unimib.triptales.source.checkpointDiary;

import com.unimib.triptales.database.CheckpointDiaryDao;
import com.unimib.triptales.model.CheckpointDiary;

import java.util.List;

public class CheckpointDiaryLocalDataSource implements BaseCheckpointDiaryLocalDataSource {
    private final CheckpointDiaryDao checkpointDiaryDao;

    public CheckpointDiaryLocalDataSource(CheckpointDiaryDao checkpointDiaryDao) {
        this.checkpointDiaryDao = checkpointDiaryDao;
    }

    @Override
    public List<CheckpointDiary> getCheckpointDiariesByDiaryId(int diaryId) {
        return checkpointDiaryDao.getCheckpointDiaryById(diaryId);
    }

    @Override
    public List<CheckpointDiary> getAllCheckpointDiaries() {
        return checkpointDiaryDao.getAllCheckpointDiaries();
    }

    @Override
    public void insertCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryDao.insertCheckpointDiary(checkpointDiary);
    }

    @Override
    public void updateCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryDao.updateCheckpointDiary(checkpointDiary);
    }

    @Override
    public void deleteCheckpointDiary(CheckpointDiary checkpointDiary) {
        checkpointDiaryDao.deleteCheckpointDiary(checkpointDiary);
    }

    @Override
    public void deleteAllCheckpointDiaries() {
        checkpointDiaryDao.deleteAllCheckpointDiaries();
    }

    @Override
    public void deleteCheckpointDiaryById(List<Integer> ids) {
        checkpointDiaryDao.deleteCheckpointDiaryById(ids);
    }

    @Override
    public void updateCheckpointDiaryName(int checkpointId, String newName) {
        checkpointDiaryDao.updateCheckpointDiaryName(checkpointId, newName);
    }

    @Override
    public void updateCheckpointDiaryDate(int checkpointId, String newDate) {
        checkpointDiaryDao.updateCheckpointDiaryDate(checkpointId, newDate);
    }

    @Override
    public void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri) {
        checkpointDiaryDao.updateCheckpointDiaryImageUri(checkpointId, newImageUri);
    }

    @Override
    public void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude) {
        checkpointDiaryDao.updateCheckpointDiaryLatitude(checkpointId, newLatitude);
    }

    @Override
    public void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude) {
        checkpointDiaryDao.updateCheckpointDiaryLongitude(checkpointId, newLongitude);
    }

    @Override
    public void updateAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries) {
        checkpointDiaryDao.updateAllCheckpointsDiaries(checkpointDiaries);
    }

}
