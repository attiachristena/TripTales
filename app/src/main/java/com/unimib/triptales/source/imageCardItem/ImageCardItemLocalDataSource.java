package com.unimib.triptales.source.imageCardItem;

import androidx.lifecycle.LiveData;

import com.unimib.triptales.database.ImageCardItemDao;
import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

public class ImageCardItemLocalDataSource implements BaseImageCardItemLocalDataSource{
    private final ImageCardItemDao imageCardItemDao;

    public ImageCardItemLocalDataSource(ImageCardItemDao imageCardItemDao) {
        this.imageCardItemDao = imageCardItemDao;
    }

    @Override
    public LiveData<List<ImageCardItem>> getAllImageCardItems() {
        return imageCardItemDao.getAllImageCardItems();
    }

    @Override
    public void insertImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemDao.insertImageCardItem(imageCardItem);
    }

    @Override
    public void updateImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemDao.updateImageCardItem(imageCardItem);
    }

    @Override
    public void deleteImageCardItem(ImageCardItem imageCardItem) {
        imageCardItemDao.deleteImageCardItem(imageCardItem);
    }

    @Override
    public void deleteAllImageCardItems() {
        imageCardItemDao.deleteAllImageCardItems();
    }

    @Override
    public void deleteImageCardItemById(int id) {
        imageCardItemDao.deleteImageCardItemById(id);
    }

    @Override
    public LiveData<ImageCardItem> getImageCardItemById(int id) {
        return imageCardItemDao.getImageCardItemById(id);
    }

    @Override
    public void updateImageCardItemTitle(int id, String title) {
        imageCardItemDao.updateImageCardItemTitle(id, title);
    }

    @Override
    public void updateImageCardItemDescription(int id, String description) {
        imageCardItemDao.updateImageCardItemDescription(id, description);
    }

    @Override
    public void updateImageCardItemDate(int id, String date) {
        imageCardItemDao.updateImageCardItemDate(id, date);
    }

    @Override
    public void updateImageCardItemImageUri(int id, String imageUri) {
        imageCardItemDao.updateImageCardItemImageUri(id, imageUri);
    }

}
