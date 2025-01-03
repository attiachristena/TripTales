package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;

import java.util.List;

public interface DiaryResponseCallback {
    void onSuccessFromRemoteDiary(Diary diary);
    void onSuccessFromRemoteDiaries(List<Diary> diariesList);
    void onFailureFromRemoteDiary(String message);
    void onSuccessFromSavingRemoteDiary(Diary diary);
    void onFailureFromSavingRemoteDiary(String message);
}
