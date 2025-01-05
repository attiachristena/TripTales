package com.unimib.triptales.source.diary;

import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.model.Diary;
import java.util.List;

public class DiaryLocalDataSource implements BaseDiaryLocalDataSource{

    private final DiaryDao diaryDao;

    public DiaryLocalDataSource(DiaryDao diaryDao) {
        this.diaryDao = diaryDao;
    }

    //operations on DiaryDao
    @Override
    public long insertDiary(Diary diary) {
        return diaryDao.insert(diary);
    }

    @Override
    public void updateDiary(Diary diary) {
        diaryDao.update(diary);
    }

    @Override
    public void deleteDiary(Diary diary) {
        diaryDao.delete(diary);
    }

    @Override
    public void deleteAllDiary(List<Diary> diaries) {
        diaryDao.deleteAll(diaries);
    }

    @Override
    public List<Diary> getAllDiaries() {
        return diaryDao.getAllDiaries();
    }

    @Override
    public List<Diary> getAllDiariesByUserId(int userId) {
        return diaryDao.getAllDiariesByUserId(userId);
    }

    @Override
    public List<String> getStartDates() {
        return diaryDao.getStartDates();
    }

    @Override
    public List<String> getEndDates() {
        return diaryDao.getEndDates();
    }

}
