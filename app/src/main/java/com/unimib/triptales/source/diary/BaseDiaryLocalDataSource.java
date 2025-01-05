package com.unimib.triptales.source.diary;

import com.unimib.triptales.database.CompleteDiary;
import com.unimib.triptales.model.Diary;
import java.util.List;

public interface BaseDiaryLocalDataSource {

    // da finire!
    long insertDiary(Diary diary);
    void updateDiary(Diary diary);
    void deleteDiary(Diary diary);
    void deleteAllDiary(List<Diary> diaries);
    List<Diary> getAllDiaries();
    List<Diary> getAllDiariesByUserId(int userId);
    List<String> getStartDates();
    List<String> getEndDates();

}
