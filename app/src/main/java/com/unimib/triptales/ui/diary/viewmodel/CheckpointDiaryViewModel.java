package com.unimib.triptales.ui.diary.viewmodel;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.unimib.triptales.model.CheckpointDiary;
import com.unimib.triptales.repository.checkpointDiary.ICheckpointDiaryRepository;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckpointDiaryViewModel extends ViewModel {

    private final ICheckpointDiaryRepository checkpointDiaryRepository;
    private final MutableLiveData<List<CheckpointDiary>> checkpointDiariesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CheckpointDiary>> selectedCheckpointDiaries = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>();;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();;
    private final MutableLiveData<String> searchErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<LatLng, String>> searchedLocationLiveData = new MutableLiveData<>();

    public CheckpointDiaryViewModel(ICheckpointDiaryRepository checkpointDiaryRepository) {
        this.checkpointDiaryRepository = checkpointDiaryRepository;
    }

    public LiveData<List<CheckpointDiary>> getCheckpointDiaries() {
        return checkpointDiariesLiveData;
    }

    public LiveData<List<CheckpointDiary>> getSelectedCheckpoints() {
        return selectedCheckpointDiaries;
    }

    public void toggleCheckpointSelection(CheckpointDiary checkpointDiary) {
        if (checkpointDiary == null) return;

        List<CheckpointDiary> currentSelection = selectedCheckpointDiaries.getValue();
        if (currentSelection == null) {
            currentSelection = new ArrayList<>();
        }

        List<CheckpointDiary> updatedSelection = new ArrayList<>(currentSelection);
        boolean wasRemoved = updatedSelection.removeIf(c -> c.getId() == checkpointDiary.getId());

        if (!wasRemoved) {
            updatedSelection.add(checkpointDiary);
        }

        selectedCheckpointDiaries.setValue(updatedSelection);
    }

    public boolean isCheckpointSelected(CheckpointDiary checkpointDiary) {
        List<CheckpointDiary> currentSelection = selectedCheckpointDiaries.getValue();
        if (currentSelection == null) return false;
        return currentSelection.stream().anyMatch(c -> c.getId() == checkpointDiary.getId());
    }

    public LiveData<List<CheckpointDiary>> getMapCheckpoints() {
        MutableLiveData<List<CheckpointDiary>> mapCheckpoints = new MutableLiveData<>();
        executorService.execute(() -> {
            List<CheckpointDiary> checkpointDiaries = checkpointDiaryRepository.getAllCheckpointDiaries();
            mapCheckpoints.postValue(checkpointDiaries);
        });
        return mapCheckpoints;
    }

    public void clearSelectedCheckpoints() {
        selectedCheckpointDiaries.setValue(new ArrayList<>());
    }

    public void loadCheckpoints(Context context) {
        executorService.execute(() -> {
            String diaryIdStr = SharedPreferencesUtils.getDiaryId(context);
            if (diaryIdStr == null) {
                checkpointDiariesLiveData.postValue(new ArrayList<>());
                return;
            }

            List<CheckpointDiary> checkpoints = checkpointDiaryRepository.getCheckpointDiariesByDiaryId(diaryIdStr);

            new Handler(Looper.getMainLooper()).post(() -> {
                checkpointDiariesLiveData.setValue(checkpoints);
            });
        });
    }


    public void insertCheckpoint(String nome, String data, Uri imageUri, LatLng latLng, Context context) {

        String diaryIdStr = SharedPreferencesUtils.getDiaryId(context);
        if (diaryIdStr == null) {
            operationStatus.postValue(false);
            return;
        }

        CheckpointDiary nuovaCheckpoint = new CheckpointDiary(diaryIdStr, nome, data, imageUri.toString(), latLng.latitude, latLng.longitude);

        try {
            long insertedId = checkpointDiaryRepository.insertCheckpointDiary(nuovaCheckpoint);
            if (insertedId > 0) {
                SharedPreferencesUtils.saveCheckpointDiaryId(context, (int)insertedId);
                loadCheckpoints(context);
                operationStatus.postValue(true);
            }
        } catch (Exception e) {
            operationStatus.postValue(false);
        }
    }

    public void updateCheckpointDiary(int checkpointId, String newName, String newDate, Uri newImageUri, Context context) {
        executorService.execute(() -> {
            try {
                if (newName != null) {
                    checkpointDiaryRepository.updateCheckpointDiaryName(checkpointId, newName);
                }
                if (newDate != null) {
                    checkpointDiaryRepository.updateCheckpointDiaryDate(checkpointId, newDate);
                }
                if (newImageUri != null) {
                    checkpointDiaryRepository.updateCheckpointDiaryImageUri(checkpointId, newImageUri.toString());
                }
                loadCheckpoints(context);
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    }


    public void deleteSelectedCheckpoints(List<CheckpointDiary> selectedCheckpoints, Context context) {
        executorService.execute(() -> {
            try {
                for (CheckpointDiary checkpointDiary : selectedCheckpoints) {
                    checkpointDiaryRepository.deleteCheckpointDiary(checkpointDiary);
                }
                loadCheckpoints(context);
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    }

    public void loadMapCheckpoints() {
        executorService.execute(() -> {
            if (checkpointDiariesLiveData.getValue() != null) {
                List<CheckpointDiary> mapCheckpoints = checkpointDiaryRepository.getAllCheckpointDiaries();
                checkpointDiariesLiveData.postValue(mapCheckpoints);
            }
        });
    }

    public LiveData<Pair<LatLng, String>> getSearchedLocationWithName() {
        return searchedLocationLiveData;
    }

    public LiveData<String> getSearchError() {
        return searchErrorLiveData;
    }

    public void searchLocation(String location, Context context) {
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocationName(location, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    String featureName = address.getFeatureName(); // Nome del luogo
                    searchedLocationLiveData.postValue(new Pair<>(latLng, featureName));
                } else {
                    searchErrorLiveData.postValue("Luogo non trovato");
                }
            } catch (IOException e) {
                searchErrorLiveData.postValue("Errore nella ricerca del luogo");
            }
        });
    }


}
