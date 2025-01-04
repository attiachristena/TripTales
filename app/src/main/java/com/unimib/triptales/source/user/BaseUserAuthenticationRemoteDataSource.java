package com.unimib.triptales.source.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;
import com.unimib.triptales.repository.user.UserResponseCallback;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();

    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);
    public abstract void signUp(String name, String surname, String email, String password);
    public abstract void signUpWithGoogle(String idToken);
    public abstract MutableLiveData<Result> resetPassword(String email);
}
