package com.unimib.triptales.ui.login.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;
import com.unimib.triptales.repository.user.IUserRepository;

public class UserViewModel extends ViewModel{

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository){
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        if(userMutableLiveData == null){
            fetchUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public void fetchUserData(String email, String password, boolean isUserRegistered){
        userMutableLiveData = userRepository.getUser(null, null, email, password, isUserRegistered);
    }

    public MutableLiveData<Result> signupUser(String name, String surname, String email, String password){
        if(userMutableLiveData == null) {
            userMutableLiveData = userRepository.signUp(name, surname, email, password);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token){
        return userRepository.signInWithGoogle(token);
    }

    public MutableLiveData<Result> signUpWithGoogle(String idToken) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.signUpWithGoogle(idToken);
        }
        return userMutableLiveData;
    }

    public User getLoggedUser(){
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout(){
        if(userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        return userMutableLiveData;
    }

    public boolean isAuthenticationError(){
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError){
        this.authenticationError = authenticationError;
    }

    public MutableLiveData<Result> resetPassword(String email){
        return userRepository.resetPassword(email);
    }
}
