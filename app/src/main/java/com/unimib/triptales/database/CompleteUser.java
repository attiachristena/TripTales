package com.unimib.triptales.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.User;

import java.util.List;

public class CompleteUser {
    @Embedded
    public User user;

    //@Relation(parentColumn = "id", entityColumn = "userId")
    //public List<Diary> diaries;
}
