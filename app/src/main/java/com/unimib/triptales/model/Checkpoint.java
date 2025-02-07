
package com.unimib.triptales.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.util.Objects;

@Entity(
        tableName = "Checkpoint",
        foreignKeys = @ForeignKey(
                entity = Diary.class,
                parentColumns = "id",
                childColumns = "diary_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "diary_id")}
)
public class Checkpoint {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "start_date")
    public String startDate;

    @ColumnInfo(name = "end_date")
    public String endDate;

    @ColumnInfo(name = "image_uri")
    public String imageUri;

    @ColumnInfo(name = "diary_id")
    public int diaryId;

    public Checkpoint(int diaryId, String name, String startDate, String endDate, String imageUri) {
        this.diaryId = diaryId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUri = imageUri;
    }
    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getImageUri(){
        return imageUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checkpoint that = (Checkpoint) o;
        return id == that.id; // or use Objects.equals() for null-safe comparison
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
