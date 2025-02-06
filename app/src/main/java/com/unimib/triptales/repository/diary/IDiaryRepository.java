package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;

import java.util.List;
import androidx.lifecycle.LiveData;


public interface IDiaryRepository {
        void insertDiary(Diary diary);
        void updateDiary(Diary diary);
        void updateAllDiaries(List<Diary> diaries);
        void updateDiaryName(String diaryId, String newName);
        void updateDiaryStartDate(String diaryId, String newStartDate);
        void updateDiaryEndDate(String diaryId, String newEndDate);
        void updateDiaryCoverImage(String diaryId, String newCoverImage);
        void updateDiaryBudget(String diaryId, String newBudget);
        void updateDiaryCountry(String diaryId, String newCountry);
        void updateDiaryIsSelected(String diaryId, boolean newIsSelected);
        void deleteDiary(Diary diary);
        void deleteAllDiaries(List<Diary> diaries);
        List<Diary> getAllDiaries();
        List<Diary> getSelectedDiaries();
        List<String> getAllCountries(String userId);
        LiveData<Boolean> getLoading();
}
