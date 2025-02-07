package com.unimib.triptales.repository.imageCardItem;

import androidx.lifecycle.LiveData;

import com.unimib.triptales.database.CheckpointDiaryDao;
import com.unimib.triptales.database.ImageCardItemDao;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.source.imageCardItem.BaseImageCardItemLocalDataSource;

import java.util.List;

public class ImageCardItemRepository implements IImageCardItemRepository {
    private BaseImageCardItemLocalDataSource imageCardItemLocalDataSource;
    private CheckpointDiaryDao checkpointDiaryDao;
    private ImageCardItemDao imageCardItemDao;

    public ImageCardItemRepository(BaseImageCardItemLocalDataSource imageCardItemLocalDataSource) {
        this.imageCardItemLocalDataSource = imageCardItemLocalDataSource;
    }

    public List<ImageCardItem> getAllImageCardItems() {
        return imageCardItemLocalDataSource.getAllImageCardItems();
    }

    public void insertImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.insertImageCardItem(imageCardItem);
    }

    public void updateImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.updateImageCardItem(imageCardItem);
    }

    public void deleteImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemLocalDataSource.deleteImageCardItem(imageCardItem);
    }

    public void deleteAllImageCardItems() {
        imageCardItemLocalDataSource.deleteAllImageCardItems();
    }

    public void deleteImageCardItemById(int id) {
        imageCardItemLocalDataSource.deleteImageCardItemById(id);
    }

    public List<ImageCardItem> getImageCardItemById(int id) {
        return imageCardItemLocalDataSource.getImageCardItemById(id);
    }

    public void updateImageCardItemTitle(int id, String title) {
        imageCardItemLocalDataSource.updateImageCardItemTitle(id, title);
    }

    public void updateImageCardItemDescription(int id, String description) {
        imageCardItemLocalDataSource.updateImageCardItemDescription(id, description);
    }

    public void updateImageCardItemDate(int id, String date) {
        imageCardItemLocalDataSource.updateImageCardItemDate(id, date);
    }

    public void updateImageCardItemImageUri(int id, String imageUri) {
        imageCardItemLocalDataSource.updateImageCardItemImageUri(id, imageUri);
    }

    public List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId) {
        return imageCardItemLocalDataSource.getImageCardItemByCheckpointDiaryId(checkpointDiaryId);
    }

    public List<ImageCardItem> getSelectedImageCardItems() {
        return imageCardItemLocalDataSource.getSelectedImageCardItems();
    }

    public void updateImageCardItemIsSelected(int id, boolean isSelected) {
        imageCardItemLocalDataSource.updateImageCardItemIsSelected(id, isSelected);
    }


    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
