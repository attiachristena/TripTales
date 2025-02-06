package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;

import java.util.List;

public interface DiaryResponseCallBack {
    void onSuccessFromLocal(List<Diary> diaries);
    void onFailureFromLocal(Exception exception);
    void onSuccessDeleteFromLocal();
    void onSuccessCountriesFromLocal(List<String> countries);
    void onSuccessSelectionFromLocal(List<Diary> diaries);

    void onSuccessDeleteFromRemote();
    void onSuccessFromRemote(List<Diary> diaries);
    void onFailureFromRemote(Exception exception);
}
