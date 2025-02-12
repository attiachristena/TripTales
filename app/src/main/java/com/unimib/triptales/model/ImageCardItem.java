package com.unimib.triptales.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ImageCardItem",
        foreignKeys = @ForeignKey(
                entity = CheckpointDiary.class,
                parentColumns = "id",
                childColumns = "checkpoint_diary_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class ImageCardItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "checkpoint_diary_id")
    private int checkpointDiaryId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "imageUri")
    private String imageUri;
    @ColumnInfo(name = "is_selected")
    private boolean isSelected;

    public ImageCardItem() {
    }

    public ImageCardItem(String title, String date, String description, String imageUri, boolean isSelected, int checkpointDiaryId) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageUri = imageUri;
        this.isSelected = isSelected;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
