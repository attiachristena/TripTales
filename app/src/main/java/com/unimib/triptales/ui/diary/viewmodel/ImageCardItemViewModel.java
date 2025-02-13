package com.unimib.triptales.ui.diary.viewmodel;

import android.content.Context;
import android.net.Uri;

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
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    public ImageCardItemViewModel(IImageCardItemRepository imageCardItemRepository) {
        this.imageCardItemRepository = imageCardItemRepository;
        loadAllImageCardItems();
    }

    public LiveData<List<ImageCardItem>> getImageCardItemsLiveData() {
        return imageCardItemsLiveData;
    }


    public void insertImageCardItem(String title, String description, String date, Uri imageUri,
                                    Context context, int checkpointDiaryId) {

        ImageCardItem newItem = new ImageCardItem();
        newItem.setTitle(title);
        newItem.setDescription(description);
        newItem.setDate(date);
        newItem.setImageUri(imageUri.toString());
        newItem.setCheckpointDiaryId(checkpointDiaryId);
        newItem.setSelected(false);
        imageCardItemRepository.insertImageCardItem(newItem);

        fetchAllImageCardItems(context);
        operationStatus.postValue(true);
    }

    public void fetchAllImageCardItems(Context context) {
        int checkpointDiaryId = SharedPreferencesUtils.getCheckpointDiaryId(context);
        if (checkpointDiaryId <= 0) {
            operationStatus.postValue(false);
            return;
        }
        List<ImageCardItem> imageCardItems =
                imageCardItemRepository.getImageCardItemByCheckpointDiaryId(checkpointDiaryId);
        imageCardItemsLiveData.postValue(imageCardItems);
    }

    public void loadAllImageCardItems() {
        List<ImageCardItem> items = imageCardItemRepository.getAllImageCardItems();
        imageCardItemsLiveData.postValue(items);
    }


    public void deleteSelectedImageCardItems(List<ImageCardItem> selectedImageCardItems, Context context) {
        executorService.execute(() -> {
            try {
                for (ImageCardItem item : selectedImageCardItems) {
                    imageCardItemRepository.deleteImageCardItem(item);
                }
                fetchAllImageCardItems(context);
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    }

    public void updateImageCardItem(int cardId, String newTitle, String newDescription, String newDate, Uri newImageUri, Context context) {
        executorService.execute(() -> {
            try {
                if (newTitle != null) {
                    imageCardItemRepository.updateImageCardItemTitle(cardId, newTitle);
                }
                if (newDescription != null) {
                    imageCardItemRepository.updateImageCardItemDescription(cardId, newDescription);
                }
                if (newDate != null) {
                    imageCardItemRepository.updateImageCardItemDate(cardId, newDate);
                }
                if (newImageUri != null) {
                    imageCardItemRepository.updateImageCardItemImageUri(cardId, newImageUri.toString());
                }
                fetchAllImageCardItems(context);
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}