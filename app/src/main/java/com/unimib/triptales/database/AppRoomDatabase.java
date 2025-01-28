package com.unimib.triptales.database;

import static com.unimib.triptales.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.unimib.triptales.model.Checkpoint;
import com.unimib.triptales.model.CountryPolygon;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.model.User;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.UriConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Diary.class, Checkpoint.class, Goal.class, Task.class, Expense.class, CountryPolygon.class}, version = DATABASE_VERSION)
@TypeConverters({UriConverter.class})
public abstract class AppRoomDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract DiaryDao diaryDao();
    public abstract CheckpointDao checkpointDao();
    public abstract GoalDao goalDao();
    public abstract TaskDao taskDao();
    public abstract ExpenseDao expenseDao();
    public abstract CountryPolygonDao countryPolygonDao();

    public static volatile AppRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, Constants.APP_DATABASE).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

}
