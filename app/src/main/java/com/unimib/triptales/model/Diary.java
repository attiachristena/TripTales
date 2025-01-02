package com.unimib.triptales.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE))
public class Diary {
    @PrimaryKey
    public int id;

    public int userId; // ID dell'utente proprietario del diario

    @ColumnInfo(name = "diary_name")
    public String name;

    @ColumnInfo(name = "diary_start_date")
    public String startDate;

    @ColumnInfo(name = "diary_end_date")
    public String endDate;

    @ColumnInfo(name = "diary_photo_path")
    public String photoPath;

    @ColumnInfo(name = "diary_budget")
    public String budget;
}
