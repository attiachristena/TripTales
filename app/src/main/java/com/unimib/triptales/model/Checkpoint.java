
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

    @ColumnInfo(name = "diary_id")
    public int diaryId;

    public Checkpoint(int diaryId) {
        this.diaryId = diaryId;
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
