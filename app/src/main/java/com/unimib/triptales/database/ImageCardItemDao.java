package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

@Dao
public interface ImageCardItemDao {

    @Insert
    void insertImageCardItem(ImageCardItem imageCardItem);

    @Query("SELECT * FROM ImageCardItem WHERE checkpointDiaryId = :checkpointDiaryId")
    LiveData<List<ImageCardItem>> getImageCardItemsByCheckpointDiaryId(int checkpointDiaryId);

    @Query("DELETE FROM ImageCardItem WHERE checkpointDiaryId = :checkpointDiaryId")
    void deleteImageCardItemsByCheckpointDiaryId(int checkpointDiaryId);

    @Query("DELETE FROM ImageCardItem")
    void deleteAllImageCardItems();

    @Query("DELETE FROM ImageCardItem WHERE id = :id")
    void deleteImageCardItemById(int id);

    @Delete
    void deleteImageCardItem(ImageCardItem imageCardItem);

    @Update
    void updateImageCardItem(ImageCardItem imageCardItem);

    @Query("UPDATE ImageCardItem SET title = :title WHERE id = :id")
    void updateImageCardItemTitle(int id, String title);

    @Query("UPDATE ImageCardItem SET description = :description WHERE id = :id")
    void updateImageCardItemDescription(int id, String description);

    @Query("UPDATE ImageCardItem SET date = :date WHERE id = :id")
    void updateImageCardItemDate(int id, String date);

    @Query("UPDATE ImageCardItem SET imageUri = :imageUri WHERE id = :id")
    void updateImageCardItemImageUri(int id, String imageUri);

    @Query("SELECT * FROM ImageCardItem WHERE id = :id")
    LiveData<ImageCardItem> getImageCardItemById(int id);

    @Query("SELECT * FROM ImageCardItem")
    LiveData<List<ImageCardItem>> getAllImageCardItems();

}
