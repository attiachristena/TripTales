package com.unimib.triptales.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Diary.class, parentColumns = "id", childColumns = "diaryId", onDelete = CASCADE))
public class Stage {
    @PrimaryKey
    public int id;

    public int diaryId;

    @ColumnInfo(name = "stage_name")
    public int name;

    @ColumnInfo(name = "stage_photo_path")
    public String photoPath;
}
