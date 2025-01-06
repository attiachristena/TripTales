package com.unimib.triptales.util;

import android.app.Application;

import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.repository.user.UserRepository;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserDataRemoteDataSource;
import com.unimib.triptales.source.user.UserAuthenticationFirebaseDataSource;
import com.unimib.triptales.source.user.UserFirebaseDataSource;
/*Per i diari
import com.unimib.triptales.source.diary.BaseDiaryLocalDataSource;
import com.unimib.triptales.source.diary.BaseDiaryRemoteDataSource;
import com.unimib.triptales.source.diary.DiaryLocalDataSource;
import com.unimib.triptales.source.diary.DiaryFirebaseDataSource;
import com.unimib.triptales.database.DiaryRoomDatabase;
import com.unimib.triptales.repository.diary.DiaryRepository;
import com.unimib.triptales.source.diary.DiaryRemoteDataSource;
*/


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

    /*
    public DiaryRoomDatabase getDiaryDao(){
        return DiaryRoomDatabase.getDatabase();
    }

    public DiaryRepository getDiaryRepository(Application application) {
        BaseDiaryRemoteDataSource diaryRemoteDataSource = new DiaryFirebaseDataSource();
        BaseDiaryLocalDataSource diaryLocalDataSource = new DiaryLocalDataSource(getDiaryDao());

        return new DiaryRepository(diaryRemoteDataSource, diaryLocalDataSource);
    }

     */

    public IUserRepository getUserRepository(){
        BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource =
                new UserAuthenticationFirebaseDataSource();
        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserFirebaseDataSource();
        return new UserRepository(userAuthRemoteDataSource, userDataRemoteDataSource);
    }
}
