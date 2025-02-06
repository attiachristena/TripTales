package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Expense;

import java.util.List;

public interface DiaryResponseCallBack {
    void onSuccessFromLocal(List<Diary> diaries);
    void onFailureFromLocal(Exception exception);
    void onSuccessFromLocal();
    void onSuccessSelectionFromLocal(List<Diary> diaries);

    void onSuccessFromRemote();
    void onSuccessFromRemote(List<Diary> diaries);
    void onFailureFromRemote(Exception exception);
    void onSuccessSelectionFromRemote(List<Diary> diaries);




}
