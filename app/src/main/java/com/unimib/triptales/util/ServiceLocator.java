package com.unimib.triptales.util;

import android.app.Application;
import android.app.Service;

import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.repository.user.UserRepository;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserDataRemoteDataSource;
import com.unimib.triptales.source.user.UserAuthenticationFirebaseDataSource;
import com.unimib.triptales.source.user.UserFirebaseDataSource;

public class ServiceLocator {

    public static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getINSTANCE() {
        if (INSTANCE == null){
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null)
                    INSTANCE = new ServiceLocator();
            }
        }
        return INSTANCE;
    }

    public IUserRepository getUserRepository(Application application){
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);
        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource = new UserAuthenticationFirebaseDataSource();
        BaseUserDataRemoteDataSource userDataRemoteDataSource = new UserFirebaseDataSource(sharedPreferencesUtil);
        return new UserRepository(userRemoteAuthenticationDataSource, userDataRemoteDataSource);
    }
}
