package com.unimib.triptales.ui.diary.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.CheckpointDiary;
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
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public ImageCardItemViewModel(IImageCardItemRepository imageCardItemRepository) {
        this.imageCardItemRepository = imageCardItemRepository;
    }

    public LiveData<List<ImageCardItem>> getAllImageCardItems() {
        return imageCardItemsLiveData;
    }

    public LiveData<List<ImageCardItem>> getSelectedImageCardItems() {
        return selectedImageCardItems;
    }

    public boolean isImageCardItemSelected(ImageCardItem imageCardItem) {
        List<ImageCardItem> currentSelection = selectedImageCardItems.getValue();
        if (currentSelection == null) return false;
        return currentSelection.stream().anyMatch(c -> c.getId() == imageCardItem.getId());
    }


    public void insertImageCardItem(String title, String description, String date, Uri imageUri, Context context) {
        Log.d("ViewModel", "Starting insert process");

        String checkpointDiaryIdStr = SharedPreferencesUtils.getCheckpointDiaryId(context);
        Log.d("ViewModel", "Retrieved checkpointDiaryId: " + checkpointDiaryIdStr);

        if (checkpointDiaryIdStr == null || checkpointDiaryIdStr.isEmpty()) {
            Log.e("ViewModel", "CheckpointDiary ID is null or empty - aborting insert");
            return;
        }

        int checkpointDiaryId = Integer.parseInt(checkpointDiaryIdStr);
        Log.d("ViewModel", "Creating new ImageCardItem with ID: " + checkpointDiaryId);

        ImageCardItem imageCardItem = new ImageCardItem(title, description, date, imageUri.toString(), checkpointDiaryId);

        executorService.execute(() -> {
            Log.d("ViewModel", "Executing insert on background thread");
            imageCardItemRepository.insertImageCardItem(imageCardItem);
            Log.d("ViewModel", "Insert completed, loading updated items");
            loadImageCardItems(context);
        });
    }





    public void deleteImageCardItem(ImageCardItem imageCardItem, Context context) {
        imageCardItemRepository.deleteImageCardItem(imageCardItem);
        loadImageCardItems(context);
    }

    public void updateImageCardItem(ImageCardItem imageCardItem, Context context) {
        imageCardItemRepository.updateImageCardItem(imageCardItem);
        loadImageCardItems(context);
    }

    public void loadImageCardItems(Context context) {
        Log.d("ViewModel", "Starting loadImageCardItems");
        executorService.execute(() -> {
            String checkpointDiaryIdStr = SharedPreferencesUtils.getCheckpointDiaryId(context);
            Log.d("ViewModel", "DEBUG - Retrieved ID from SharedPreferences: " + checkpointDiaryIdStr);

            if (checkpointDiaryIdStr == null) {
                Log.e("ViewModel", "DEBUG - CheckpointDiaryId is null");
                imageCardItemsLiveData.postValue(new ArrayList<>());
                return;
            }

            try {
                int checkpointDiaryId = Integer.parseInt(checkpointDiaryIdStr);
                Log.d("ViewModel", "DEBUG - Parsed ID: " + checkpointDiaryId);

                List<ImageCardItem> imageCardItems = (List<ImageCardItem>) imageCardItemRepository.getImageCardItemById(checkpointDiaryId);
                Log.d("ViewModel", "DEBUG - Retrieved items: " + (imageCardItems != null ? imageCardItems.size() : "null"));

                imageCardItemsLiveData.postValue(imageCardItems);
            } catch (NumberFormatException e) {
                Log.e("ViewModel", "DEBUG - Error parsing ID: " + e.getMessage());
                imageCardItemsLiveData.postValue(new ArrayList<>());
            }
        });
    }




}
