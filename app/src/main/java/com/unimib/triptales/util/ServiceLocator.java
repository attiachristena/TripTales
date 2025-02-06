package com.unimib.triptales.util;

import android.content.Context;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.repository.checkpoint.CheckpointRepository;
import com.unimib.triptales.repository.checkpoint.ICheckpointRepository;
import com.unimib.triptales.repository.diary.DiaryRepository;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.repository.expense.ExpenseRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.GoalRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.repository.task.TaskRepository;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.repository.user.UserRepository;
import com.unimib.triptales.source.checkpoint.BaseCheckpointLocalDataSource;
import com.unimib.triptales.source.checkpoint.BaseCheckpointRemoteDataSource;
import com.unimib.triptales.source.checkpoint.CheckpointLocalDataSource;
import com.unimib.triptales.source.checkpoint.CheckpointRemoteDataSource;
import com.unimib.triptales.source.diary.BaseDiaryLocalDataSource;
import com.unimib.triptales.source.diary.BaseDiaryRemoteDataSource;
import com.unimib.triptales.source.diary.DiaryLocalDataSource;
import com.unimib.triptales.source.diary.DiaryRemoteDataSource;
import com.unimib.triptales.source.expense.BaseExpenseLocalDataSource;
import com.unimib.triptales.source.expense.BaseExpenseRemoteDataSource;
import com.unimib.triptales.source.expense.ExpenseLocalDataSource;
import com.unimib.triptales.source.expense.ExpenseRemoteDataSource;
import com.unimib.triptales.source.goal.BaseGoalLocalDataSource;
import com.unimib.triptales.source.goal.BaseGoalRemoteDataSource;
import com.unimib.triptales.source.goal.GoalLocalDataSource;
import com.unimib.triptales.source.goal.GoalRemoteDataSource;
import com.unimib.triptales.source.task.BaseTaskLocalDataSource;
import com.unimib.triptales.source.task.BaseTaskRemoteDataSource;
import com.unimib.triptales.source.task.TaskLocalDataSource;
import com.unimib.triptales.source.task.TaskRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.triptales.source.user.UserAuthenticationFirebaseDataSource;

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

    public IUserRepository getUserRepository(){
        BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource =
                new UserAuthenticationFirebaseDataSource();
        return new UserRepository(userAuthRemoteDataSource);
    }

    public IExpenseRepository getExpenseRepository(Context context){
        BaseExpenseLocalDataSource expenseLocalDataSource =
                new ExpenseLocalDataSource(AppRoomDatabase.getDatabase(context).expenseDao(),
                        SharedPreferencesUtils.getDiaryId(context));
        BaseExpenseRemoteDataSource expenseRemoteDataSource =
                new ExpenseRemoteDataSource(SharedPreferencesUtils.getLoggedUserId(),
                        SharedPreferencesUtils.getDiaryId(context));
        return new ExpenseRepository(expenseLocalDataSource, expenseRemoteDataSource);
    }

    public IGoalRepository getGoalRepository(Context context){
        BaseGoalLocalDataSource goalLocalDataSource =
                new GoalLocalDataSource(AppRoomDatabase.getDatabase(context).goalDao());
        BaseGoalRemoteDataSource goalRemoteDataSource =
                new GoalRemoteDataSource(SharedPreferencesUtils.getLoggedUserId(),
                        SharedPreferencesUtils.getDiaryId(context));
        return new GoalRepository(goalLocalDataSource, goalRemoteDataSource);
    }

    public ITaskRepository getTaskRepository(Context context){
        BaseTaskLocalDataSource taskLocalDataSource =
                new TaskLocalDataSource(AppRoomDatabase.getDatabase(context).taskDao());
        BaseTaskRemoteDataSource taskRemoteDataSource =
                new TaskRemoteDataSource(SharedPreferencesUtils.getLoggedUserId(),
                        SharedPreferencesUtils.getDiaryId(context));
        return new TaskRepository(taskLocalDataSource, taskRemoteDataSource);
    }

    public ICheckpointRepository getCheckpointRepository(Context context){
        BaseCheckpointLocalDataSource checkpointLocalDataSource =
                new CheckpointLocalDataSource(AppRoomDatabase.getDatabase(context).checkpointDao());
        BaseCheckpointRemoteDataSource checkpointRemoteDataSource =
                new CheckpointRemoteDataSource();
        return new CheckpointRepository(checkpointLocalDataSource, checkpointRemoteDataSource);
    }

    public IDiaryRepository getDiaryRepository(Context context) {
        BaseDiaryLocalDataSource diaryLocalDataSource =
                new DiaryLocalDataSource(AppRoomDatabase.getDatabase(context).diaryDao());
        BaseDiaryRemoteDataSource diaryRemoteDataSource =
                new DiaryRemoteDataSource(SharedPreferencesUtils.getLoggedUserId());
        return new DiaryRepository(diaryLocalDataSource, diaryRemoteDataSource);
    }

}
