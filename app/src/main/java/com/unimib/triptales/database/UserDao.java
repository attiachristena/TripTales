package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.unimib.triptales.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Insert
    void insertAll(List<User> users);

    @Query("UPDATE User SET user_email = :newEmail WHERE id = :userId")
    void updateUserEmail(int userId, String newEmail);

    @Query("UPDATE User SET user_passowrd = :newPassword WHERE id = :userId")
    void updateUserPassword(int userId, String newPassword);

    @Delete
    void delete(User user);

    @Delete
    void deleteAll(List<User> users);

    //Recupero di un singolo elemento
    @Query("SELECT * FROM User WHERE id = :userId")
    User getUserById(int userId);

    //Recupero di una lista di elementi
    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE user_name LIKE :first AND " +
            "user_surname LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Transaction
    @Query("SELECT * FROM Diary WHERE id = :userId")
    LiveData<CompleteUser> getCompleteUser(int userId);

}
