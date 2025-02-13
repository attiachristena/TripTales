package com.unimib.triptales.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccessLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userRemoteDataSource.setUserResponseCallback(this);
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
    public void logout() {
        userRemoteDataSource.logout();
    }

    @Override
    public MutableLiveData<Boolean> getLogoutSuccess() {
        return logoutSuccessLiveData;
    }

    @Override
    public MutableLiveData<Result> getUser() {
        userRemoteDataSource.getLoggedUser();
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        return userRemoteDataSource.resetPassword(email);
    }

    @Override
    public MutableLiveData<String> updatePassword(String email, String oldPassword, String newPassword) {
        userRemoteDataSource.updatePassword(email, oldPassword, newPassword);
        return errorLiveData;
    }

    @Override
    public MutableLiveData<String> updateProfile(String newName, String newSurname) {
        userRemoteDataSource.updateProfile(newName, newSurname);
        return errorLiveData;
    }

    @Override
    public MutableLiveData<String> getError() {
        return errorLiveData;
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
        logoutSuccessLiveData.postValue(false);
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    @Override
    public void onSuccessGetLoggedUser(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
        logoutSuccessLiveData.postValue(false);
    }

    @Override
    public void onFailureGetLoggedUser(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    @Override
    public void onSuccessLogout() {
        logoutSuccessLiveData.postValue(true);
    }

    @Override
    public void onSuccessFromUpdateData() {
        errorLiveData.postValue(null);
    }

    @Override
    public void onFailureFromUpdateData(String message) {
        errorLiveData.postValue(message);
    }
}
