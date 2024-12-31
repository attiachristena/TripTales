package com.unimib.triptales.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.User;

import com.unimib.triptales.model.Result;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);

    MutableLiveData<Result> getGoogleUser(String idToken);

    MutableLiveData<Result> logout();

    User getLoggedUser();

    void signUp(String email, String password);

    void signIn(String email, String password);

    void signInWithGoogle(String token);
}
