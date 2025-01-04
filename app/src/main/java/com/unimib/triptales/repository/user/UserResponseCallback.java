package com.unimib.triptales.repository.user;

import com.unimib.triptales.model.User;
import com.unimib.triptales.model.Diary;

import java.util.List;


public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessGetLoggedUser(User user);
    void onFailureGetLoggedUser(String message);
    void onSuccessLogout();
    void onSuccessFromRemoteDatabase(User user);
    void onFailureFromRemoteDatabase(String message);

    void onSuccessSaveDiary(Diary diary);
    void onSuccessDeleteDiary(String diaryId);
    void onSuccessGetDiaries(List<Diary> diaries);
    void onFailureDiaryOperation(String message);
}
