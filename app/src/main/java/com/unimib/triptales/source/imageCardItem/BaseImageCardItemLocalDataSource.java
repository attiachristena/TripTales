package com.unimib.triptales.source.imageCardItem;

import androidx.lifecycle.LiveData;

import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

public interface BaseImageCardItemLocalDataSource {

    List<ImageCardItem> getAllImageCardItems();
    void insertImageCardItem(ImageCardItem imageCardItem);
    void updateImageCardItem(ImageCardItem imageCardItem);
    void deleteImageCardItem(ImageCardItem imageCardItem);
    void deleteAllImageCardItems();
    void deleteImageCardItemById(int id);
    void updateImageCardItemTitle(int id, String title);
    void updateImageCardItemDescription(int id, String description);
    void updateImageCardItemDate(int id, String date);
    void updateImageCardItemImageUri(int id, String imageUri);
    List<ImageCardItem> getImageCardItemById(int id);
    List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId);
    List<ImageCardItem> getSelectedImageCardItems();
    void updateImageCardItemIsSelected(int id, boolean isSelected);
}
