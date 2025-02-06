package com.unimib.triptales.source.diary;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.diary.DiaryResponseCallBack;

import java.util.List;

public abstract class BaseDiaryLocalDataSource {
    protected DiaryResponseCallBack diaryCallback;

    public void setDiaryCallback(DiaryResponseCallBack diaryCallback){
        this.diaryCallback = diaryCallback;
    }

    public abstract long insertDiary(Diary diary);
    public abstract void updateDiaryName(int diaryId, String newName);

    public abstract void updateDiaryIsSelected(int diaryId, boolean newIsSelected);
    public abstract void deleteDiary(Diary diary);
    public abstract void deleteAllDiaries(List<Diary> diaries);
    public abstract List<Diary> getAllDiaries();
    public abstract List<Diary> getSelectedDiaries();

    public abstract List<Diary> getAllDiariesByUserId(String userId);

    public abstract void updateDiary(Diary diary);

    public abstract void updateDiaryStartDate(int diaryId, String newStartDate);

    public abstract void updateDiaryEndDate(int diaryId, String newEndDate);

    public abstract void updateDiaryCoverImage(int diaryId, String newCoverImage);

    public abstract void updateDiaryBudget(int diaryId, String newBudget);

    public abstract void updateDiaryCountry(int diaryId, String newCountry);

    public abstract List<String> getAllCountriesByUserId(String userId);
    //public abstract List<Diary> getAllDiariesByUserId(int Id);

}
