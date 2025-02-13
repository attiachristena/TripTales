package com.unimib.triptales.ui.login.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Result;
import com.unimib.triptales.repository.user.IUserRepository;


public class UserViewModel extends ViewModel{

    private final IUserRepository userRepository;
    private final MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository){
        this.userRepository = userRepository;
        authenticationError = false;
        this.userMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        fetchUserData(email, password, isUserRegistered);
        return userMutableLiveData;
    }

    public void fetchUserData(String email, String password, boolean isUserRegistered){
        userRepository.getUser(null, null, email, password, isUserRegistered).observeForever(userMutableLiveData::postValue);
    }

    public MutableLiveData<Result> signupUser(String name, String surname, String email, String password){
        userRepository.signUp(name, surname, email, password).observeForever(userMutableLiveData::postValue);
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token){
        return userRepository.signInWithGoogle(token);
    }

    public MutableLiveData<Result> signUpWithGoogle(String idToken) {
        return userRepository.signUpWithGoogle(idToken);
    }

    public MutableLiveData<Result> logout(){
        userRepository.logout().observeForever(userMutableLiveData::postValue);
        return userMutableLiveData;
    }


    public void setAuthenticationError(boolean authenticationError){
        this.authenticationError = authenticationError;
    }

    public MutableLiveData<Result> resetPassword(String email){
        return userRepository.resetPassword(email);
    }
}
