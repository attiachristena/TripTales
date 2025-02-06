package com.unimib.triptales.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ImageCardItem",
        foreignKeys = @ForeignKey(
                entity = CheckpointDiary.class,
                parentColumns = "id",
                childColumns = "checkpointDiaryId",
                onDelete = ForeignKey.CASCADE
        )
)
public class ImageCardItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int checkpointDiaryId;
    private String title;
    private String description;
    private String date;
    private String imageUri;

    public ImageCardItem(String title, String date, String description, String imageUri, int checkpointDiaryId) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageUri = imageUri;
        this.checkpointDiaryId = checkpointDiaryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckpointDiaryId() {
        return checkpointDiaryId;
    }

    public void setCheckpointDiaryId(int checkpointDiaryId) {
        this.checkpointDiaryId = checkpointDiaryId;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}

