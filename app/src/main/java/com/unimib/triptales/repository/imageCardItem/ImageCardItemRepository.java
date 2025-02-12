package com.unimib.triptales.repository.imageCardItem;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.source.imageCardItem.BaseImageCardItemLocalDataSource;
import com.unimib.triptales.source.imageCardItem.BaseImageCardItemRemoteDataSource;

import java.util.List;

public class ImageCardItemRepository implements IImageCardItemRepository, ImageCardItemResponseCallBack {
    private BaseImageCardItemLocalDataSource imageCardItemLocalDataSource;
    private final BaseImageCardItemRemoteDataSource imageCardItemRemoteDataSource;
    private final MutableLiveData<List<ImageCardItem>> selectedImageCardItemsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ImageCardItem>> imageCardItemsLiveData = new MutableLiveData<>();;
    private boolean remoteDelete = false;
    private boolean localDelete = false;
    private boolean isRemoteOperation = false;


    public ImageCardItemRepository(BaseImageCardItemLocalDataSource imageCardItemLocalDataSource,
                                   BaseImageCardItemRemoteDataSource imageCardItemRemoteDataSource) {
        this.imageCardItemLocalDataSource = imageCardItemLocalDataSource;
        this.imageCardItemRemoteDataSource = imageCardItemRemoteDataSource;
        this.imageCardItemLocalDataSource.setImageCardItemCallback(this);
        this.imageCardItemRemoteDataSource.setImageCardItemCallback(this);
    }

    public List<ImageCardItem> getAllImageCardItems() {
        imageCardItemLocalDataSource.getAllImageCardItems();
        imageCardItemRemoteDataSource.getAllImageCardItems();
        return imageCardItemsLiveData.getValue();
    }

    public void insertImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.insertImageCardItem(imageCardItem);
        imageCardItemRemoteDataSource.insertImageCardItem(imageCardItem);
    }

    public void updateImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.updateImageCardItem(imageCardItem);
        imageCardItemRemoteDataSource.updateImageCardItem(imageCardItem);
    }

    public void deleteImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.deleteImageCardItem(imageCardItem);
        imageCardItemRemoteDataSource.deleteImageCardItem(imageCardItem);
    }

    public void deleteImageCardItemById(int id) {
        imageCardItemLocalDataSource.deleteImageCardItemById(id);
    }


    public void updateImageCardItemTitle(int id, String title) {
        imageCardItemLocalDataSource.updateImageCardItemTitle(id, title);
        imageCardItemRemoteDataSource.updateImageCardItemTitle(id, title);
    }

    public void updateImageCardItemDescription(int id, String description) {
        imageCardItemLocalDataSource.updateImageCardItemDescription(id, description);
        imageCardItemRemoteDataSource.updateImageCardItemDescription(id, description);
    }

    public void updateImageCardItemDate(int id, String date) {
        imageCardItemLocalDataSource.updateImageCardItemDate(id, date);
        imageCardItemRemoteDataSource.updateImageCardItemDate(id, date);
    }

    public void updateImageCardItemImageUri(int id, String imageUri) {
        imageCardItemLocalDataSource.updateImageCardItemImageUri(id, imageUri);
        imageCardItemRemoteDataSource.updateImageCardItemImageUri(id, imageUri);
    }

    public List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId) {
        List<ImageCardItem> imageCardItems = imageCardItemLocalDataSource.getImageCardItemByCheckpointDiaryId(checkpointDiaryId);
        imageCardItemsLiveData.postValue(imageCardItems);
        return imageCardItems;
    }

    public List<ImageCardItem> getSelectedImageCardItems() {
        imageCardItemLocalDataSource.getSelectedImageCardItems();
        return selectedImageCardItemsLiveData.getValue();
    }

    public void updateImageCardItemIsSelected(int id, boolean isSelected) {
        imageCardItemLocalDataSource.updateImageCardItemIsSelected(id, isSelected);
        imageCardItemRemoteDataSource.updateImageCardItemIsSelected(id, isSelected);
    }

    @Override
    public void onSuccessDeleteFromRemote() {
        remoteDelete = true;
    }

    @Override
    public void onSuccessFromRemote(List<ImageCardItem> imageCardItems) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (remoteDelete || !localDelete) {
                for (ImageCardItem imageCardItem : imageCardItems) {
                    imageCardItemLocalDataSource.insertImageCardItem(imageCardItem);
                }
                imageCardItemLocalDataSource.getAllImageCardItems();
                remoteDelete = false;
                localDelete = false;
            }
        });
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
    }

    @Override
    public void onSuccessDeleteFromLocal() {
        localDelete = true;
    }

    @Override
    public void onSuccessFromLocal(List<ImageCardItem> imageCardItems) {
        imageCardItemsLiveData.setValue(imageCardItems);
        for (ImageCardItem imageCardItem : imageCardItems) {
            imageCardItemRemoteDataSource.insertImageCardItem(imageCardItem);
        }
    }

    @Override
    public void onSuccessSelectionFromLocal(List<ImageCardItem> imageCardItems) {
        selectedImageCardItemsLiveData.postValue(imageCardItems);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
    }


    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}