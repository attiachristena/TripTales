package com.unimib.triptales.ui.diary.viewmodel;


import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Checkpoint;
import com.unimib.triptales.repository.checkpoint.CheckpointRepository;
import com.unimib.triptales.repository.checkpoint.ICheckpointRepository;
import com.unimib.triptales.source.checkpoint.BaseCheckpointLocalDataSource;
import com.unimib.triptales.source.checkpoint.BaseCheckpointRemoteDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;


public class CheckpointViewModel extends ViewModel {

    private final ICheckpointRepository checkpointRepository;

    private final MutableLiveData<List<Checkpoint>> checkpointsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Checkpoint>> selectedCheckpoints = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Checkpoint>> selectedCheckpointsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>();;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();;
    private final MutableLiveData<String> searchErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<LatLng, String>> searchedLocationLiveData = new MutableLiveData<>();



    public CheckpointViewModel(ICheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
        loadCheckpoints();
    }

    public LiveData<List<Checkpoint>> getCheckpoints() {
        return checkpointsLiveData;
    }

    public LiveData<List<Checkpoint>> getSelectedCheckpoints() {
        return selectedCheckpoints;
    }

    public void toggleCheckpointSelection(Checkpoint checkpoint) {
        if (checkpoint == null) return;

        List<Checkpoint> currentSelection = selectedCheckpoints.getValue();
        if (currentSelection == null) {
            currentSelection = new ArrayList<>();
        }

        List<Checkpoint> updatedSelection = new ArrayList<>(currentSelection);
        boolean wasRemoved = updatedSelection.removeIf(c -> c.getId() == checkpoint.getId());

        if (!wasRemoved) {
            updatedSelection.add(checkpoint);
        }

        selectedCheckpoints.setValue(updatedSelection);
    }

    public boolean isCheckpointSelected(Checkpoint checkpoint) {
        List<Checkpoint> currentSelection = selectedCheckpoints.getValue();
        if (currentSelection == null) return false;
        return currentSelection.stream().anyMatch(c -> c.getId() == checkpoint.getId());
    }


    public LiveData<List<Checkpoint>> getMapCheckpoints() {
        MutableLiveData<List<Checkpoint>> mapCheckpoints = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Checkpoint> checkpoints = checkpointRepository.getAllCheckpoints();
            mapCheckpoints.postValue(checkpoints);
        });
        return mapCheckpoints;
    }




    public void clearSelectedCheckpoints() {
        selectedCheckpoints.setValue(new ArrayList<>());
    }


    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    public void loadCheckpoints() {
        executorService.execute(() -> {
            List<Checkpoint> checkpoints = checkpointRepository.getAllCheckpoints();
            List<Checkpoint> selectedCheckpoints = checkpointRepository.getSelectedCheckpoints();

            checkpointsLiveData.postValue(checkpoints);
            selectedCheckpointsLiveData.postValue(selectedCheckpoints);
        });
    }

    public void insertCheckpoint(String nome, String data, Uri imageUri, LatLng latLng) {
        if (isPosizioneGiàSalvata(latLng)) {
            // Posizione già salvata, quindi non procedere
            return;
        }

        Checkpoint nuovaCheckpoint = new Checkpoint(nome, data, imageUri.toString(), latLng.latitude, latLng.longitude);
        try {
            long insertedId = checkpointRepository.insertCheckpoint(nuovaCheckpoint);
            loadCheckpoints();
            operationStatus.postValue(insertedId > 0);
        } catch (Exception e) {
            operationStatus.postValue(false);
        }
    }

    public interface OnCheckpointInsertedListener {
        void onCheckpointInserted(int checkpointId);
    }

    public boolean isPosizioneGiàSalvata(LatLng latLng) {
        List<Checkpoint> tappeSalvate = checkpointRepository.getAllCheckpoints();
        for (Checkpoint checkpoint : tappeSalvate) {
            if (checkpoint.getLatitude() == latLng.latitude && checkpoint.getLongitude() == latLng.longitude) {
                return true;
            }
        }
        return false;
    }


    public void updateCheckpoint(int checkpointId, String newName, String newDate, Uri newImageUri) {
        executorService.execute(() -> {
            try {
                if (newName != null) {
                    checkpointRepository.updateCheckpointName(checkpointId, newName);
                }
                if (newDate != null) {
                    checkpointRepository.updateCheckpointDate(checkpointId, newDate);
                }
                if (newImageUri != null) {
                    checkpointRepository.updateCheckpointImageUri(checkpointId, newImageUri.toString());
                }
                loadCheckpoints();
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    }

    /* public void deleteCheckpoints(List<Checkpoint> checkpoints) {
        executorService.execute(() -> {
            try {
                checkpointRepository.deleteAllCheckpoints(checkpoints);
                loadCheckpoints();
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    } */

    public void deleteSelectedCheckpoints(List<Checkpoint> selectedCheckpoints) {
        executorService.execute(() -> {
            try {
                for (Checkpoint checkpoint : selectedCheckpoints) {
                    checkpointRepository.deleteCheckpoint(checkpoint);  // Passa ogni checkpoint singolarmente
                }
                loadCheckpoints();  // Ricarica i checkpoint dopo la cancellazione
                operationStatus.postValue(true);
            } catch (Exception e) {
                operationStatus.postValue(false);
            }
        });
    }


    public void setCheckpointSelection(int checkpointId, boolean isSelected) {
        executorService.execute(() -> {
            try {
                checkpointRepository.updateCheckpointIsSelected(checkpointId, isSelected);
                loadCheckpoints();
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

    public void searchCheckpoints(String query) {
        executorService.execute(() -> {
            List<Checkpoint> filteredCheckpoints = checkpointRepository.searchCheckpointsByName(query);
            checkpointsLiveData.postValue(filteredCheckpoints);
        });
    }

    public void loadMapCheckpoints() {
        executorService.execute(() -> {
            if (checkpointsLiveData.getValue() != null) {
                List<Checkpoint> mapCheckpoints = checkpointRepository.getAllCheckpoints();
                checkpointsLiveData.postValue(mapCheckpoints);
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