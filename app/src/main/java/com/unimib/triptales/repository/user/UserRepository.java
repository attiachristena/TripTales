package com.unimib.triptales.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;
import com.unimib.triptales.repository.diary.DiaryResponseCallback;
import com.unimib.triptales.source.diary.BaseDiaryLocalDataSource;
import com.unimib.triptales.source.diary.BaseDiaryRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserDataRemoteDataSource;

import java.util.List;

public class UserRepository implements IUserRepository, UserResponseCallback, DiaryResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userDiariesMutableLiveData;
    private final BaseDiaryLocalDataSource diaryLocalDataSource;
    private final BaseDiaryRemoteDataSource diaryRemoteDataSource;


    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.diaryLocalDataSource = diaryLocalDataSource;
        this.diaryRemoteDataSource = diaryRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userDiariesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.diaryRemoteDataSource.setDiaryResponseCallback(this);
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
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserDiaries(String userId) {
        diaryRemoteDataSource.getUserDiaries(userId);
        return userDiariesMutableLiveData;
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
    public MutableLiveData<Result> signUp(String name, String surname, String email, String password) {
        userRemoteDataSource.signUp(name, surname, email, password);
        return userDiariesMutableLiveData;
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email,password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void saveUserDiary(String userId, Diary diaryId) {
        userDataRemoteDataSource.saveUserDiary(userId, diaryId);
    }

    @Override
    public void deleteUserDiary(String userId, String diaryId) {
        userDataRemoteDataSource.deleteUserDiary(userId, diaryId);
    }

    //DA riguardare
    @Override
    public void onSuccessFromAuthentication(User user) {
        if(user!=null){
            Result.UserSuccess result = new Result.UserSuccess(user);
            userMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Diary> diaries) {
        Result.UserSuccess result = new Result.UserSuccess(diaries);
        userDiariesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessSaveDiary(Diary diary) {
        Result.DiarySuccess result = new Result.DiarySuccess(diary);
        userDiariesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeleteDiary(String diaryId) {
        Result.DiarySuccess result = new Result.DiarySuccess(diaryId);
        userDiariesMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureDiaryOperation(String message) {
        Result.Error result = new Result.Error(message);
        userDiariesMutableLiveData.postValue(result);
    }
}
