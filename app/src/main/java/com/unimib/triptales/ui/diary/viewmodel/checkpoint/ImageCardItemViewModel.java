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
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int checkpointDiaryIdStr = -1;  // Initialize to -1

    public ImageCardItemViewModel(IImageCardItemRepository imageCardItemRepository) {
        this.imageCardItemRepository = imageCardItemRepository;
    }

    public LiveData<List<ImageCardItem>> getImageCardItemsLiveData() {
        return imageCardItemsLiveData;
    }

    public LiveData<List<ImageCardItem>> getSelectedImageCardItems() {
        return selectedImageCardItems;
    }

    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    public void setCheckpointDiaryId(int diaryId) {
        if (diaryId <= 0) {
            return;
        }
        this.checkpointDiaryIdStr = diaryId;
        fetchAllImageCardItems();
    }

    public void insertImageCardItem(String title, String description, String date, Uri imageUri, Context context) {
        if (checkpointDiaryIdStr <= 0) {
            checkpointDiaryIdStr = SharedPreferencesUtils.getCheckpointDiaryId(context);
        }

        if (checkpointDiaryIdStr <= 0) {
            operationStatus.postValue(false);
            return;
        }

        ImageCardItem imageCardItem = new ImageCardItem(title, description, date, imageUri.toString(), false, checkpointDiaryIdStr);
        imageCardItemRepository.insertImageCardItem(imageCardItem);
        fetchAllImageCardItems();
    }

    public void fetchAllImageCardItems() {
        if (checkpointDiaryIdStr <= 0) {
            Log.e("ImageCardItemViewModel", "Invalid checkpoint diary ID: " + checkpointDiaryIdStr);
            return;
        }

        executorService.execute(() -> {
            List<ImageCardItem> items = imageCardItemRepository.getImageCardItemByCheckpointDiaryId(checkpointDiaryIdStr);
            Log.d("ImageCardItemViewModel", "Fetched items: " + items.size() + " for checkpoint ID: " + checkpointDiaryIdStr);
            imageCardItemsLiveData.postValue(items);
        });
    }

    public void deleteImageCardItem(ImageCardItem imageCardItem, Context context) {
        executorService.execute(() -> {
            try {
                imageCardItemRepository.deleteImageCardItem(imageCardItem);
                fetchAllImageCardItems();
                operationStatus.postValue(true);
            } catch (Exception e) {
                Log.e("ImageCardItemViewModel", "Errore durante la cancellazione della card: " + e.getMessage());
                operationStatus.postValue(false);
            }
        });
    }

    public void deleteSelectedImageCardItems(List<ImageCardItem> selectedImageCardItems, Context context) {
        executorService.execute(() -> {
            try {
                for (ImageCardItem item : selectedImageCardItems) {
                    imageCardItemRepository.deleteImageCardItem(item);
                }
                fetchAllImageCardItems();
                operationStatus.postValue(true);
            } catch (Exception e) {
                Log.e("ImageCardItemViewModel", "Errore durante la cancellazione delle card: " + e.getMessage());
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
                fetchAllImageCardItems();
                operationStatus.postValue(true);
            } catch (Exception e) {
                Log.e("ImageCardItemViewModel", "Errore durante l'aggiornamento della card: " + e.getMessage());
                operationStatus.postValue(false);
            }
        });
    }

    public void toggleImageCardItemSelection(ImageCardItem imageCardItem) {
        if (imageCardItem == null) return;

        List<ImageCardItem> currentSelection = selectedImageCardItems.getValue();
        if (currentSelection == null) {
            currentSelection = new ArrayList<>();
        }

        List<ImageCardItem> updatedSelection = new ArrayList<>(currentSelection);
        boolean wasRemoved = updatedSelection.removeIf(c -> c.getId() == imageCardItem.getId());

        if (!wasRemoved) {
            updatedSelection.add(imageCardItem);
        }

        selectedImageCardItems.setValue(updatedSelection);
    }

    public boolean isImageCardItemSelected(ImageCardItem imageCardItem) {
        List<ImageCardItem> currentSelection = selectedImageCardItems.getValue();
        if (currentSelection == null) return false;
        return currentSelection.stream().anyMatch(c -> c.getId() == imageCardItem.getId());
    }

    public void clearSelectedImageCardItems() {
        selectedImageCardItems.setValue(new ArrayList<>());
    }

    public void resetParam(String title, String description, String date){
        title = "";
        description = "";
        date = "";
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
