package com.unimib.triptales.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity/*(foreignKeys = @ForeignKey(entity = Diary.class, parentColumns = "id", childColumns = "diaryId", onDelete = CASCADE))*/
public class Task {
    @PrimaryKey
    public int id; // ID univoco per la spesa

    //public int diaryId; // ID del diario

    @ColumnInfo(name = "task_name")
    public String name;

}
