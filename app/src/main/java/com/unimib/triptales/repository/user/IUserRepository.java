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
    void logout();
    MutableLiveData<Boolean> getLogoutSuccess();

    MutableLiveData<Result> getUser(String name, String surname, String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getUser();
    User getLoggedUser();

    MutableLiveData<Result> resetPassword(String email);
    MutableLiveData<String> updatePassword(String email, String oldPassword, String newPassword);
    MutableLiveData<String> updateProfile(String newName, String newSurname);
    MutableLiveData<String> getError();

}
