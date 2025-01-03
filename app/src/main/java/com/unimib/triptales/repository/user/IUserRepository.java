package com.unimib.triptales.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.User;
import com.unimib.triptales.model.Result;

public interface IUserRepository {

    MutableLiveData<Result> signUp(String name, String surname, String email, String password);
    MutableLiveData<Result> signIn(String email, String password);
    MutableLiveData<Result> signInWithGoogle(String token);
    MutableLiveData<Result> logout();

    MutableLiveData<Result> getUser(String name, String surname, String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);

    User getLoggedUser();

    MutableLiveData<Result> getUserDiaries(String userId);
    MutableLiveData<Result> saveUserDiary(String userId, String diaryId);
    MutableLiveData<Result> deleteUserDiary(String userId, String diaryId);
}
