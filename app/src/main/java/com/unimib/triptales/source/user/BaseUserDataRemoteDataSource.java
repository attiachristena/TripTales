package com.unimib.triptales.source.user;

import com.unimib.triptales.model.User;
import com.unimib.triptales.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);

    // public abstract void getUserPreferences(String idToken);

    // public abstract void saveUserDiary(String diary, ....
}
