package com.unimib.triptales.source.imageCardItem;

import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.repository.imageCardItem.ImageCardItemResponseCallBack;

import java.util.List;

public abstract class BaseImageCardItemLocalDataSource {
    protected ImageCardItemResponseCallBack imageCardItemCallback;

    public void setImageCardItemCallback(ImageCardItemResponseCallBack imageCardItemCallback){
        this.imageCardItemCallback = imageCardItemCallback;
    }

    public abstract void getAllImageCardItems();
    public abstract void insertImageCardItem(ImageCardItem imageCardItem);
    public abstract void deleteImageCardItem(ImageCardItem imageCardItem);
    public abstract void updateImageCardItemTitle(int id, String title);
    public abstract void updateImageCardItemDescription(int id, String description);
    public abstract void updateImageCardItemDate(int id, String date);
    public abstract void updateImageCardItemImageUri(int id, String imageUri);
    public abstract List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId);
}