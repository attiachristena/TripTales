package com.unimib.triptales.source.diary;

import com.unimib.triptales.model.Diary;

import java.util.List;

public interface BaseDiaryLocalDataSource {
    long insertDiary(Diary diary);
    void updateDiaryName(int diaryId, String newName);

    void updateDiaryIsSelected(int diaryId, boolean newIsSelected);
    void deleteDiary(Diary diary);
    void deleteAllDiaries(List<Diary> diaries);
    List<Diary> getAllDiaries();
    List<Diary> getSelectedDiaries();

    List<Diary> getAllDiariesByUserId(String userId);
    //List<Diary> getAllDiariesByUserId(int Id);

}
