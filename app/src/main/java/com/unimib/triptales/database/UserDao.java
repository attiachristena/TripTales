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

    @Delete
    void delete(User user);

    @Delete
    void deleteAll(List<User> users);


    //Recupero di una lista di elementi
    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM user")
    List<User> getAll();


    @Query("SELECT * FROM user WHERE user_name LIKE :first AND " +
            "user_surname LIKE :last LIMIT 1")
    User findByName(String first, String last);


}
