package com.unimib.triptales.source.user;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveDiary(String userId, Diary diary);

    public abstract void getUserDiaries(String userId);

    public void deleteUserDiary(String userId, String diaryId) {
    }
}
