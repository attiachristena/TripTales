package com.unimib.triptales.repository.imageCardItem;

import androidx.lifecycle.LiveData;

import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

public interface IImageCardItemRepository {

    LiveData<List<ImageCardItem>> getAllImageCardItems();
    void insertImageCardItem(ImageCardItem imageCardItem);
    void updateImageCardItem(ImageCardItem imageCardItem);
    void deleteImageCardItem(ImageCardItem imageCardItem);
    void deleteAllImageCardItems();
    void deleteImageCardItemById(int id);
    void updateImageCardItemTitle(int id, String title);
    void updateImageCardItemDescription(int id, String description);
    void updateImageCardItemDate(int id, String date);
    void updateImageCardItemImageUri(int id, String imageUri);
    LiveData<ImageCardItem> getImageCardItemById(int id);

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
