package com.unimib.triptales.repository.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.User;
import com.unimib.triptales.model.Result;

public interface IUserRepository {

    MutableLiveData<Result> signUp(String name, String surname, String email, String password);
    MutableLiveData<Result> signIn(String email, String password);
    MutableLiveData<Result> signInWithGoogle(String token);
    MutableLiveData<Result> signUpWithGoogle(String idToken);
    MutableLiveData<Result> logout();

    MutableLiveData<Result> getUser(String name, String surname, String email, String password, boolean isUserRegistered);
    User getLoggedUser();

    LiveData<Result> checkEmailExists(String email);
    MutableLiveData<Result> resetPassword(String email);
}
