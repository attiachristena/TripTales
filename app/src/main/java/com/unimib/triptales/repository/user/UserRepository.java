package com.unimib.triptales.repository.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserDataRemoteDataSource;

import java.util.List;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Result> diariesMutableLiveData = new MutableLiveData<>();

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;

        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String name, String surname, String email, String password, boolean isUserRegistered) {
        if(isUserRegistered)
            signIn(email, password);
        else {
            signUp(name, surname, email,password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> signUp(String name, String surname, String email, String password) {
        userRemoteDataSource.signUp(name, surname, email, password);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> signInWithGoogle(String idToken) {
        userRemoteDataSource.signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> signUpWithGoogle(String idToken) {
        userRemoteDataSource.signUpWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public LiveData<Result> checkEmailExists(String email) {
        return userRemoteDataSource.checkEmailExists(email); }

    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        return userRemoteDataSource.resetPassword(email);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    @Override
    public void onSuccessGetLoggedUser(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
    }

    @Override
    public void onFailureGetLoggedUser(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
    }


    @Override
    public void onFailureFromRemoteDatabase(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    @Override
    public void onSuccessSaveDiary(Diary diary) {
        diariesMutableLiveData.postValue(new Result.DiarySuccess(diary));
    }

    @Override
    public void onSuccessDeleteDiary(String diaryId) {
        diariesMutableLiveData.postValue(new Result.GenericSuccess());
    }

    @Override
    public void onSuccessGetDiaries(List<Diary> diaries) {
        diariesMutableLiveData.postValue(new Result.DiarySuccess(diaries));
    }

    @Override
    public void onFailureDiaryOperation(String message) {
        diariesMutableLiveData.postValue(new Result.Error(message));
    }


}
