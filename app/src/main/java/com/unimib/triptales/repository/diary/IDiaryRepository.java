package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Expense;

import java.util.List;

public interface IDiaryRepository {

    long insertDiary(Diary diary);
    void updateDiary(Diary diary);
    void deleteDiary(Diary diary);
    void deleteAllDiary(List<Diary> diaries);
    List<Diary> getAllDiaries();
    List<Diary> getAllDiariesByUserId(int userId);
    List<String> getStartDates();
    List<String> getEndDates();

}
