package com.unimib.triptales.repository.imageCardItem;

import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

public interface IImageCardItemRepository {

    List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId);
    void insertImageCardItem(ImageCardItem imageCardItem);
    void deleteImageCardItem(ImageCardItem imageCardItem);
    void updateImageCardItemTitle(int id, String title);
    void updateImageCardItemDescription(int id, String description);
    void updateImageCardItemDate(int id, String date);
    void updateImageCardItemImageUri(int id, String imageUri);
    List<ImageCardItem> getAllImageCardItems();

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}