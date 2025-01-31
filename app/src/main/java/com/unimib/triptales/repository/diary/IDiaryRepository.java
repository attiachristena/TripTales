package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;

import java.util.List;


public interface IDiaryRepository {
    long insertDiary(Diary diary);

    void updateDiaryName(int diaryId, String newName);

    void updateDiaryIsSelected(int diaryId, boolean isSelected);

    void deleteDiary(Diary diary);

    void deleteAllDiaries(List<Diary> diaries);

    void getAllDiaries(DiaryResponseCallBack callBack);

    void getSelectedDiaries(DiaryResponseCallBack callBack);
}
