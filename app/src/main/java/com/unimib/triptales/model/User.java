package com.unimib.triptales.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity
public class User {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "user_name")
    public String name;

    @ColumnInfo(name = "user_surname")
    public String surname;

    @ColumnInfo(name = "user_email")
    public String email;

    @ColumnInfo(name = "user_passowrd")
    public String password;
}
