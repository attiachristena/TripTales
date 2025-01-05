package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.source.diary.BaseDiaryLocalDataSource;
import com.unimib.triptales.source.diary.BaseDiaryRemoteDataSource;

import java.util.List;

public class DiaryRepository implements IDiaryRepository{
    private final BaseDiaryLocalDataSource diaryLocalDataSource;
    private final BaseDiaryRemoteDataSource diaryRemoteDataSource;

    public DiaryRepository(BaseDiaryLocalDataSource diaryLocalDataSource, BaseDiaryRemoteDataSource diaryRemoteDataSource) {
        this.diaryLocalDataSource = diaryLocalDataSource;
        this.diaryRemoteDataSource = diaryRemoteDataSource;
    }

    //operations on DiaryDao
    public long insertDiary(Diary diary) {
        return diaryLocalDataSource.insertDiary(diary);
    }

    public void updateDiary(Diary diary) {
        diaryLocalDataSource.updateDiary(diary);
    }

    public void deleteDiary(Diary diary) {
        diaryLocalDataSource.deleteDiary(diary);
    }

    public void deleteAllDiary(List<Diary> diaries) {
        diaryLocalDataSource.deleteAllDiary(diaries);
    }

    public List<Diary> getAllDiaries() {
        return diaryLocalDataSource.getAllDiaries();
    }

    public List<Diary> getAllDiariesByUserId(int userId) {
        return diaryLocalDataSource.getAllDiariesByUserId(userId);
    }

    public List<String> getStartDates() {
        return diaryLocalDataSource.getStartDates();
    }

    public List<String> getEndDates() {
        return diaryLocalDataSource.getEndDates();
    }
}
