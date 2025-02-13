package com.unimib.triptales.repository.imageCardItem;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.source.imageCardItem.BaseImageCardItemLocalDataSource;
import com.unimib.triptales.source.imageCardItem.BaseImageCardItemRemoteDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageCardItemRepository implements IImageCardItemRepository, ImageCardItemResponseCallBack {
    private BaseImageCardItemLocalDataSource imageCardItemLocalDataSource;
    private final BaseImageCardItemRemoteDataSource imageCardItemRemoteDataSource;
    private final MutableLiveData<List<ImageCardItem>> selectedImageCardItemsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ImageCardItem>> imageCardItemsLiveData = new MutableLiveData<>();;
    private boolean remoteDelete = false;
    private boolean localDelete = false;
    private boolean isRemoteOperation = false;
    private final int checkpointDiaryId;



    public ImageCardItemRepository(BaseImageCardItemLocalDataSource imageCardItemLocalDataSource,
                                   BaseImageCardItemRemoteDataSource imageCardItemRemoteDataSource,
                                   int checkpointDiaryId) {
        this.checkpointDiaryId = checkpointDiaryId;
        this.imageCardItemLocalDataSource = imageCardItemLocalDataSource;
        this.imageCardItemRemoteDataSource = imageCardItemRemoteDataSource;
        this.imageCardItemLocalDataSource.setImageCardItemCallback(this);
        this.imageCardItemRemoteDataSource.setImageCardItemCallback(this);
    }

    @Override
    public List<ImageCardItem> getAllImageCardItems() {
        imageCardItemLocalDataSource.getAllImageCardItems();
        imageCardItemRemoteDataSource.getAllImageCardItems();

        List<ImageCardItem> allItems = imageCardItemsLiveData.getValue();
        if (allItems != null) {
            return allItems.stream()
                    .filter(item -> item.getCheckpointDiaryId() == this.checkpointDiaryId)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void insertImageCardItem(ImageCardItem imageCardItem) {
        imageCardItem.setCheckpointDiaryId(this.checkpointDiaryId);
        imageCardItemLocalDataSource.insertImageCardItem(imageCardItem);
        imageCardItemRemoteDataSource.insertImageCardItem(imageCardItem);
    }

    public void deleteImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.deleteImageCardItem(imageCardItem);
        imageCardItemRemoteDataSource.deleteImageCardItem(imageCardItem);
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
        List<ImageCardItem> imageCardItems =
                imageCardItemLocalDataSource.getImageCardItemByCheckpointDiaryId(checkpointDiaryId);
        imageCardItemsLiveData.postValue(imageCardItems);
        return imageCardItems;
    }

    @Override
    public void onSuccessDeleteFromRemote() {
        remoteDelete = true;
    }

    @Override
    public void onSuccessFromRemote(List<ImageCardItem> imageCardItems) {
        List<ImageCardItem> filteredItems = imageCardItems.stream()
                .filter(item -> item.getCheckpointDiaryId() == this.checkpointDiaryId)
                .collect(Collectors.toList());

        AppRoomDatabase.databaseWriteExecutor.execute(() -> {

            for (ImageCardItem imageCardItem : filteredItems) {
                imageCardItemLocalDataSource.insertImageCardItem(imageCardItem);
            }
            imageCardItemLocalDataSource.getAllImageCardItems();
            remoteDelete = false;
            localDelete = false;
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
        List<ImageCardItem> filteredItems = imageCardItems.stream()
                .filter(item -> item.getCheckpointDiaryId() == this.checkpointDiaryId)
                .collect(Collectors.toList());

        imageCardItemsLiveData.setValue(filteredItems);
    }

    @Override
    public void onSuccessSelectionFromLocal(List<ImageCardItem> imageCardItems) {
        selectedImageCardItemsLiveData.postValue(imageCardItems);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
    }

}