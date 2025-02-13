package com.unimib.triptales.source.imageCardItem;

import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.repository.imageCardItem.ImageCardItemRepository;
import com.unimib.triptales.repository.imageCardItem.ImageCardItemResponseCallBack;

import java.util.List;

public abstract class BaseImageCardItemRemoteDataSource {
    protected ImageCardItemResponseCallBack imageCardItemCallback;

    public void setImageCardItemCallback(ImageCardItemResponseCallBack callback) {
        this.imageCardItemCallback = callback;
    }

    public abstract void getAllImageCardItems();
    public abstract void insertImageCardItem(ImageCardItem imageCardItem);
    public abstract void updateImageCardItem(ImageCardItem imageCardItem);
    public abstract void deleteImageCardItem(ImageCardItem imageCardItem);
    // public abstract void deleteAllImageCardItems(List<ImageCardItem> imageCardItems);
    //public abstract void getImageCardItemById(int id);
    public abstract void updateImageCardItemTitle(int id, String title);
    public abstract void updateImageCardItemDescription(int id, String description);
    public abstract void updateImageCardItemDate(int id, String date);
    public abstract void updateImageCardItemImageUri(int id, String imageUri);
    //public abstract void getImageCardItemByCheckpointDiaryId(int checkpointDiaryId);
    public abstract void updateImageCardItemIsSelected(int id, boolean isSelected);

}