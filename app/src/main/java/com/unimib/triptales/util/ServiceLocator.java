package com.unimib.triptales.util;

import android.content.Context;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.repository.diary.DiaryRepository;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.repository.checkpointDiary.CheckpointDiaryRepository;
import com.unimib.triptales.repository.checkpointDiary.ICheckpointDiaryRepository;
import com.unimib.triptales.source.checkpointDiary.CheckpointDiaryRemoteDataSource;
import com.unimib.triptales.source.checkpointDiary.BaseCheckpointDiaryRemoteDataSource;
import com.unimib.triptales.repository.expense.ExpenseRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.GoalRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
import com.unimib.triptales.repository.imageCardItem.ImageCardItemRepository;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.repository.task.TaskRepository;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.repository.user.UserRepository;
import com.unimib.triptales.source.checkpointDiary.BaseCheckpointDiaryRemoteDataSource;
import com.unimib.triptales.source.diary.BaseDiaryLocalDataSource;
import com.unimib.triptales.source.diary.BaseDiaryRemoteDataSource;
import com.unimib.triptales.source.diary.DiaryLocalDataSource;
import com.unimib.triptales.source.diary.DiaryRemoteDataSource;
import com.unimib.triptales.source.checkpointDiary.BaseCheckpointDiaryLocalDataSource;
import com.unimib.triptales.source.checkpointDiary.CheckpointDiaryLocalDataSource;
import com.unimib.triptales.source.expense.BaseExpenseLocalDataSource;
import com.unimib.triptales.source.expense.BaseExpenseRemoteDataSource;
import com.unimib.triptales.source.expense.ExpenseLocalDataSource;
import com.unimib.triptales.source.expense.ExpenseRemoteDataSource;
import com.unimib.triptales.source.goal.BaseGoalLocalDataSource;
import com.unimib.triptales.source.goal.BaseGoalRemoteDataSource;
import com.unimib.triptales.source.goal.GoalLocalDataSource;
import com.unimib.triptales.source.goal.GoalRemoteDataSource;
import com.unimib.triptales.source.imageCardItem.BaseImageCardItemLocalDataSource;
import com.unimib.triptales.source.imageCardItem.ImageCardItemLocalDataSource;
import com.unimib.triptales.source.task.BaseTaskLocalDataSource;
import com.unimib.triptales.source.task.BaseTaskRemoteDataSource;
import com.unimib.triptales.source.task.TaskLocalDataSource;
import com.unimib.triptales.source.task.TaskRemoteDataSource;
import com.unimib.triptales.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.triptales.source.user.UserAuthenticationFirebaseDataSource;

public class ServiceLocator {

    public static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    public static ServiceLocator getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null)
                    INSTANCE = new ServiceLocator();
            }
        }
        return INSTANCE;
    }

    public IUserRepository getUserRepository() {
        BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource =
                new UserAuthenticationFirebaseDataSource();
        return new UserRepository(userAuthRemoteDataSource);
    }

    public IExpenseRepository getExpenseRepository(Context context) {
        BaseExpenseLocalDataSource expenseLocalDataSource =
                new ExpenseLocalDataSource(AppRoomDatabase.getDatabase(context).expenseDao(),
                        SharedPreferencesUtils.getDiaryId(context));
        BaseExpenseRemoteDataSource expenseRemoteDataSource =
                new ExpenseRemoteDataSource(SharedPreferencesUtils.getLoggedUserId());
        return new ExpenseRepository(expenseLocalDataSource, expenseRemoteDataSource);
    }

    public IGoalRepository getGoalRepository(Context context) {
        BaseGoalLocalDataSource goalLocalDataSource =
                new GoalLocalDataSource(AppRoomDatabase.getDatabase(context).goalDao(),
                        SharedPreferencesUtils.getDiaryId(context));
        BaseGoalRemoteDataSource goalRemoteDataSource =
                new GoalRemoteDataSource(SharedPreferencesUtils.getLoggedUserId());
        return new GoalRepository(goalLocalDataSource, goalRemoteDataSource);
    }

    public ITaskRepository getTaskRepository(Context context) {
        BaseTaskLocalDataSource taskLocalDataSource =
                new TaskLocalDataSource(AppRoomDatabase.getDatabase(context).taskDao(),
                        SharedPreferencesUtils.getDiaryId(context));
        BaseTaskRemoteDataSource taskRemoteDataSource =
                new TaskRemoteDataSource(SharedPreferencesUtils.getLoggedUserId());
        return new TaskRepository(taskLocalDataSource, taskRemoteDataSource);
    }

    public ICheckpointDiaryRepository getCheckpointDiaryRepository(Context context) {
        BaseCheckpointDiaryLocalDataSource checkpointDiaryLocalDataSource =
                new CheckpointDiaryLocalDataSource(AppRoomDatabase.getDatabase(context).checkpointDiaryDao(),
                        SharedPreferencesUtils.getDiaryId(context));
        BaseCheckpointDiaryRemoteDataSource checkpointDiaryRemoteDataSource =
                new CheckpointDiaryRemoteDataSource(SharedPreferencesUtils.getLoggedUserId());
        return new CheckpointDiaryRepository(checkpointDiaryLocalDataSource, checkpointDiaryRemoteDataSource);
    }

    public IImageCardItemRepository getImageCardItemRepository(Context context) {
        BaseImageCardItemLocalDataSource imageCardItemLocalDataSource =
                new ImageCardItemLocalDataSource(AppRoomDatabase.getDatabase(context).imageCardItemDao());
        return new ImageCardItemRepository(imageCardItemLocalDataSource);

    }

    public IDiaryRepository getDiaryRepository(Context context) {
        BaseDiaryLocalDataSource diaryLocalDataSource =
                new DiaryLocalDataSource(AppRoomDatabase.getDatabase(context).diaryDao(),
                        SharedPreferencesUtils.getLoggedUserId());
        BaseDiaryRemoteDataSource diaryRemoteDataSource =
                new DiaryRemoteDataSource(SharedPreferencesUtils.getLoggedUserId());
        return new DiaryRepository(diaryLocalDataSource, diaryRemoteDataSource);
    }
}
