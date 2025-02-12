package com.unimib.triptales.source.imageCardItem;

import com.unimib.triptales.database.ImageCardItemDao;
import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

public class ImageCardItemLocalDataSource extends BaseImageCardItemLocalDataSource{
    private final ImageCardItemDao imageCardItemDao;
    private final int checkpointDiaryId;

    public ImageCardItemLocalDataSource(ImageCardItemDao imageCardItemDao, int checkpointDiaryId) {
        this.imageCardItemDao = imageCardItemDao;
        this.checkpointDiaryId = checkpointDiaryId;
    }

    @Override
    public List<ImageCardItem> getAllImageCardItems() {
        try {
            List<ImageCardItem> imageCardItems = imageCardItemDao.getAllImageCardItems();
            imageCardItemCallback.onSuccessFromLocal(imageCardItems);
            return imageCardItems;
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
            return null;
        }
    }

    @Override
    public void insertImageCardItem(ImageCardItem imageCardItem) {
        try{
            long id = imageCardItemDao.insertImageCardItem(imageCardItem); // Inserimento in Room
            imageCardItem.setId((int) id);
            imageCardItemCallback.onSuccessFromLocal(List.of(imageCardItem));
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateImageCardItem(ImageCardItem imageCardItem) {
        try {
            imageCardItemDao.updateImageCardItem(imageCardItem);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteImageCardItem(ImageCardItem imageCardItem) {
        try {
            imageCardItemDao.deleteImageCardItem(imageCardItem);
            imageCardItemCallback.onSuccessDeleteFromLocal();
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteImageCardItemById(int id) {
        try {
            imageCardItemDao.deleteImageCardItemById(id);
            imageCardItemCallback.onSuccessDeleteFromLocal();
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }


    @Override
    public List<ImageCardItem> getImageCardItemByCheckpointDiaryId(int checkpointDiaryId){
        try {
            return imageCardItemDao.getImageCardItemByCheckpointDiaryId(checkpointDiaryId);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
            return null;
        }
    }

    @Override
    public void updateImageCardItemTitle(int id, String title) {
        try {
            imageCardItemDao.updateImageCardItemTitle(id, title);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateImageCardItemDescription(int id, String description) {
        try {
            imageCardItemDao.updateImageCardItemDescription(id, description);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateImageCardItemDate(int id, String date) {
        try {
            imageCardItemDao.updateImageCardItemDate(id, date);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateImageCardItemImageUri(int id, String imageUri) {
        try {
            imageCardItemDao.updateImageCardItemImageUri(id, imageUri);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }


    @Override
    public List<ImageCardItem> getSelectedImageCardItems() {
        try {
            return imageCardItemDao.getSelectedImageCardItems();
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
            return null;
        }
    }

    @Override
    public void updateImageCardItemIsSelected(int id, boolean isSelected) {
        try {
            imageCardItemDao.updateImageCardItemIsSelected(id, isSelected);
        } catch (Exception e) {
            imageCardItemCallback.onFailureFromLocal(e);
        }
    }

}