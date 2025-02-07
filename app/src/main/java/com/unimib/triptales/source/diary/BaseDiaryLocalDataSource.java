package com.unimib.triptales.source.diary;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.diary.DiaryResponseCallBack;

import java.util.List;

public abstract class BaseDiaryLocalDataSource {
    protected DiaryResponseCallBack diaryCallback;

    public void setDiaryCallback(DiaryResponseCallBack diaryCallback){
        this.diaryCallback = diaryCallback;
    }

    public abstract void insertDiary(Diary diary);
    public abstract void updateDiary(Diary diary);
    public abstract void updateDiaryName(String diaryId, String newName);
    public abstract void updateDiaryStartDate(String diaryId, String newStartDate);
    public abstract void updateDiaryEndDate(String diaryId, String newEndDate);
    public abstract void updateDiaryCoverImage(String diaryId, String newCoverImage);
    public abstract void updateDiaryBudget(String diaryId, String newBudget);
    public abstract void updateDiaryCountry(String diaryId, String newCountry);
    public abstract void updateDiaryIsSelected(String diaryId, boolean newIsSelected);
    public abstract void deleteDiary(Diary diary);
    public abstract void deleteAllDiaries(List<Diary> diaries);
    public abstract void getAllDiaries();
    public abstract void getSelectedDiaries();
    public abstract void getAllCountries(String userId);
    public abstract void getBudget(String diaryId);
}
