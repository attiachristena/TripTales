package com.unimib.triptales.ui.homepage.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import java.util.ArrayList;
import java.util.List;
import static com.unimib.triptales.util.SharedPreferencesUtils.getLoggedUserId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.util.*;

public class HomeViewModel extends ViewModel {
    private final IDiaryRepository diaryRepository;
    private final MutableLiveData<List<Diary>> diariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Diary>> selectedDiariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> diaryEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> diaryOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> validationErrorsLiveData = new MutableLiveData<>(); // ✅ FIXED: Initialized correctly





    public HomeViewModel(IDiaryRepository repository) {
        this.diaryRepository = repository;
        loadDiaries();
    }

    public LiveData<List<Diary>> getDiariesLiveData() {
        return diariesLiveData;
    }

    public LiveData<Boolean> getDiaryOverlayVisibility() {
        return diaryOverlayVisibility;
    }

    public LiveData<Map<String, String>> getValidationErrorsLiveData() {
        return validationErrorsLiveData;
    }

    public void setDiaryOverlayVisibility(boolean visible) {
        diaryOverlayVisibility.postValue(visible);
    }

    public void loadDiaries() {
        try {
            String userId = getLoggedUserId();
            if (userId != null) {
                List<Diary> diaries = diaryRepository.getAllDiariesByUserId(userId);
                setDiariesLiveData(diaries != null ? diaries : new ArrayList<>());
            } else {
                setDiariesLiveData(new ArrayList<>());
                setErrorMessage("Utente non autenticato");
            }
        } catch (Exception e) {
            setErrorMessage("Errore nel caricamento dei diari: " + e.getMessage());
        }
    }

    public LiveData<List<Diary>> getSelectedDiariesLiveData() {
        return selectedDiariesLiveData;
    }

    // Metodo per aggiornare la lista dei diari selezionati
    public void updateSelectedDiaries(List<Diary> selectedDiaries) {
        selectedDiariesLiveData.setValue(selectedDiaries);
    }

    public void insertDiary(String diaryName, String startDate, String endDate,
                            String imageUri, String budget, String country) {
        if (!validateDiaryInput(diaryName, startDate, endDate, imageUri, country)) {
            return; // Stop if validation fails
        }

        try {
            String currentUserId = getLoggedUserId();
            if (currentUserId == null) {
                setErrorMessage("Utente non autenticato");
                return;
            }

            Diary newDiary = new Diary(0, currentUserId, diaryName, startDate, endDate, imageUri, budget, country);
            diaryRepository.insertDiary(newDiary);
            addDiaryToLiveData(newDiary);

            diaryEvent.setValue("Diario aggiunto con successo!");
            diaryOverlayVisibility.setValue(false);
        } catch (Exception e) {
            setErrorMessage("Errore durante l'inserimento del diario: " + e.getMessage());
        }
    }

    private void addDiaryToLiveData(Diary newDiary) {
        try {
            List<Diary> currentList = diariesLiveData.getValue();
            if (currentList == null) {
                currentList = new ArrayList<>();
            }
            currentList.add(newDiary);
            diariesLiveData.setValue(currentList);
        } catch (Exception e) {
            setErrorMessage("Errore nell'aggiornamento dei dati: " + e.getMessage());
        }
    }

    public void updateDiary(Diary diary, String diaryName, String startDate, String endDate,
                            String imageUri, String country) {
        if (diary == null) {
            setErrorMessage("Nessun diario selezionato per la modifica");
            return;
        }

        if (!validateDiaryInput(diaryName, startDate, endDate, imageUri, country)) {
            return;
        }

        try {
            diary.setName(diaryName);
            diary.setStartDate(startDate);
            diary.setEndDate(endDate);
            diary.setCoverImageUri(imageUri);
            diary.setCountry(country);

            diaryRepository.updateDiary(diary);
            diaryEvent.setValue("Diario aggiornato con successo!");

            diaryOverlayVisibility.setValue(false);
        } catch (Exception e) {
            setErrorMessage("Errore durante l'aggiornamento del diario: " + e.getMessage());
        }
    }


    public void deleteDiaries(List<Diary> diaries) {
        if (diaries == null || diaries.isEmpty()) {
            diaryEvent.setValue("Nessun diario selezionato per l'eliminazione");
            return;
        }

        try {
            for (Diary diary : diaries) {
                diary.setSelected(false);
                diaryRepository.updateDiary(diary); // Ensure selection is cleared before deletion
                diaryRepository.deleteDiary(diary);
            }
            removeDiariesFromLiveData(diaries);
            diaryEvent.setValue("Diario eliminato con successo!");
        } catch (Exception e) {
            setErrorMessage("Errore durante l'eliminazione dei diari: " + e.getMessage());
        }
    }

    private void removeDiariesFromLiveData(List<Diary> diariesToRemove) {
        try {
            List<Diary> currentList = diariesLiveData.getValue();
            if (currentList == null || currentList.isEmpty()) {
                return; // Nothing to remove
            }
            currentList.removeAll(diariesToRemove);
            setDiariesLiveData(currentList);
        } catch (Exception e) {
            setErrorMessage("Errore nella rimozione dei diari: " + e.getMessage());
        }
    }


    private boolean validateDiaryInput(String diaryName, String startDate, String endDate, String imageUri, String country) {
        Map<String, String> errors = new HashMap<>();

        if (diaryName.isEmpty()) errors.put("diaryName", "Inserisci il nome del diario");
        if (startDate.isEmpty()) errors.put("startDate", "Inserisci la data di partenza");
        if (endDate.isEmpty()) errors.put("endDate", "Inserisci la data di ritorno");
        if (!startDate.isEmpty() && !endDate.isEmpty() && !validateDateOrder(startDate, endDate)) {
            errors.put("dateOrder", "La data di partenza deve essere prima della data di ritorno");
        }
        if (imageUri == null || imageUri.isEmpty()) errors.put("image", "Seleziona un'immagine per il diario");
        if (country.isEmpty()) errors.put("country", "Seleziona un paese");

        if (!errors.isEmpty()) {
            validationErrorsLiveData.setValue(errors);
            return false;
        }

        return true;
    }

    private boolean validateDateOrder(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return sdf.parse(startDate).before(sdf.parse(endDate));
        } catch (ParseException e) {
            return false;
        }
    }

    private void setDiariesLiveData(List<Diary> diaries) {
        diariesLiveData.setValue(diaries);
    }

    private void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }
}
