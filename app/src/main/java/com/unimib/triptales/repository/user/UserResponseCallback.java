package com.unimib.triptales.repository.user;

import com.unimib.triptales.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);

    void onFailureFromAuthentication(String message);

    void onSuccessLogout();

    void onSuccessFromRemoteDatabase(User user);

    void onFailureFromRemoteDatabase(String message);
}
