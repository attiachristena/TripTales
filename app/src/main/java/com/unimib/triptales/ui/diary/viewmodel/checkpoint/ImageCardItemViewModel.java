package com.unimib.triptales.ui.diary.viewmodel.checkpoint;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageCardItemViewModel extends ViewModel {
    private final IImageCardItemRepository imageCardItemRepository;
    private final MutableLiveData<List<ImageCardItem>> imageCardItemsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ImageCardItem>> selectedImageCardItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>();;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    int checkpointDiaryIdStr;

    public void setCheckpointDiaryId(int diaryId) {
        this.checkpointDiaryIdStr = diaryId;
        fetchAllImageCardItems();
    }


    public ImageCardItemViewModel(IImageCardItemRepository imageCardItemRepository) {
        this.imageCardItemRepository = imageCardItemRepository;
    }


    public LiveData<List<ImageCardItem>> getSelectedImageCardItems() {
        return selectedImageCardItems;
    }

    public boolean isImageCardItemSelected(ImageCardItem imageCardItem) {
        List<ImageCardItem> currentSelection = selectedImageCardItems.getValue();
        if (currentSelection == null) return false;
        return currentSelection.stream().anyMatch(c -> c.getId() == imageCardItem.getId());
    }

    public void fetchAllImageCardItems() {
        imageCardItemsLiveData.setValue(imageCardItemRepository.getImageCardItemById(checkpointDiaryIdStr));
    }

    public void insertImageCardItem(String title, String description, String date, Uri imageUri, Context context) {
        if (checkpointDiaryIdStr == 0) {
            checkpointDiaryIdStr = SharedPreferencesUtils.getCheckpointDiaryId(context);
        }

        if (checkpointDiaryIdStr == -1) {
            operationStatus.postValue(false);
            return;
        }

        ImageCardItem imageCardItem = new ImageCardItem(title, description, date,
                imageUri.toString(), false, checkpointDiaryIdStr);

        imageCardItemRepository.insertImageCardItem(imageCardItem);
        imageCardItemsLiveData.postValue(imageCardItemRepository.getImageCardItemById(checkpointDiaryIdStr));
        fetchAllImageCardItems();
        imageCardItemsLiveData.getValue();
    }


    public void deleteImageCardItem(ImageCardItem imageCardItem, Context context) {
        imageCardItemRepository.deleteImageCardItem(imageCardItem);
    }

    public void updateImageCardItem(ImageCardItem imageCardItem, Context context) {
        imageCardItemRepository.updateImageCardItem(imageCardItem);
    }

    public LiveData<List<ImageCardItem>> getImageCardItemsLiveData() {
        return imageCardItemsLiveData;
    }
}
