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
    long insertImageCardItem(ImageCardItem imageCardItem);


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


    @Query("SELECT * FROM ImageCardItem WHERE checkpoint_diary_id = :checkpointDiaryId")
    List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId);

    @Query("SELECT * FROM ImageCardItem")
    List<ImageCardItem> getAllImageCardItems();

    @Query("UPDATE ImageCardItem SET is_selected = :isSelected WHERE id = :id")
    void updateImageCardItemIsSelected(int id, boolean isSelected);

    @Query("SELECT * FROM ImageCardItem WHERE is_selected = 1")
    List<ImageCardItem> getSelectedImageCardItems();

}