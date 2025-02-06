package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.source.diary.BaseDiaryLocalDataSource;

import java.util.List;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.source.diary.BaseDiaryRemoteDataSource;


public class DiaryRepository implements IDiaryRepository, DiaryResponseCallBack {

    private final BaseDiaryLocalDataSource diaryLocalDataSource;
    private final BaseDiaryRemoteDataSource diaryRemoteDataSource;
    private final MutableLiveData<List<Diary>> diariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Diary>> selectedDiariesLiveData = new MutableLiveData<>();

    public DiaryRepository(BaseDiaryLocalDataSource diaryLocalDataSource, BaseDiaryRemoteDataSource diaryRemoteDataSource) {
        this.diaryLocalDataSource = diaryLocalDataSource;
        this.diaryRemoteDataSource = diaryRemoteDataSource;
        this.diaryLocalDataSource.setDiaryCallback(this);
        this.diaryRemoteDataSource.setDiaryCallback(this);
    }

    // Inserisce un nuovo diario
    @Override
    public void insertDiary(Diary diary) {
        diaryLocalDataSource.insertDiary(diary);
        diaryRemoteDataSource.insertDiary(diary);
    }

    // Aggiorna un diario intero
    @Override
    public void updateDiary(Diary diary) {
        diaryLocalDataSource.updateDiary(diary);
        diaryRemoteDataSource.updateDiary(diary);
        List<Diary> updatedDiaries = diaryLocalDataSource.getAllDiariesByUserId(diary.getUserId());
        diariesLiveData.postValue(updatedDiaries);
    }

    // Aggiorna solo il nome del diario
    @Override
    public void updateDiaryName(int diaryId, String newName) {
        diaryLocalDataSource.updateDiaryName(diaryId, newName);
        diaryRemoteDataSource.updateDiaryName(diaryId, newName);
    }

    // Aggiorna lo stato di selezione del diario
    @Override
    public void updateDiaryIsSelected(int diaryId, boolean isSelected) {
        diaryLocalDataSource.updateDiaryIsSelected(diaryId, isSelected);
        diaryRemoteDataSource.updateDiaryIsSelected(diaryId, isSelected);
    }

    // Aggiorna la data di partenza
    @Override
    public void updateDiaryStartDate(int diaryId, String newStartDate) {
        diaryLocalDataSource.updateDiaryStartDate(diaryId, newStartDate);
        diaryRemoteDataSource.updateDiaryStartDate(diaryId, newStartDate);
    }

    // Aggiorna la data di ritorno
    @Override
    public void updateDiaryEndDate(int diaryId, String newEndDate) {
        diaryLocalDataSource.updateDiaryEndDate(diaryId, newEndDate);
        diaryRemoteDataSource.updateDiaryEndDate(diaryId, newEndDate);
    }

    // Aggiorna l'immagine di copertina
    @Override
    public void updateDiaryCoverImage(int diaryId, String newCoverImage) {
        diaryLocalDataSource.updateDiaryCoverImage(diaryId, newCoverImage);
        diaryRemoteDataSource.updateDiaryCoverImage(diaryId, newCoverImage);
    }

    // Aggiorna il budget
    @Override
    public void updateDiaryBudget(int diaryId, String newBudget) {
        diaryLocalDataSource.updateDiaryBudget(diaryId, newBudget);
        diaryRemoteDataSource.updateDiaryBudget(diaryId, newBudget);
    }

    // Aggiorna il paese
    @Override
    public void updateDiaryCountry(int diaryId, String newCountry) {
        diaryLocalDataSource.updateDiaryCountry(diaryId, newCountry);
        diaryRemoteDataSource.updateDiaryCountry(diaryId, newCountry);
    }

    // Elimina un diario
    @Override
    public void deleteDiary(Diary diary) {
        diaryLocalDataSource.deleteDiary(diary);
        diaryRemoteDataSource.deleteDiary(diary);
    }

    // Elimina più diari
    @Override
    public void deleteAllDiaries(List<Diary> diaries) {
        diaryLocalDataSource.deleteAllDiaries(diaries);
        diaryRemoteDataSource.deleteAllDiaries(diaries);
    }

    // Ottiene tutti i diari
    @Override
    public List<Diary> getAllDiaries() {
        diaryLocalDataSource.getAllDiaries();
        diaryRemoteDataSource.getAllDiaries();
        return diariesLiveData.getValue();
    }

    // Ottiene i diari selezionati
    @Override
    public LiveData<List<Diary>> getSelectedDiaries() {
        diaryLocalDataSource.getSelectedDiaries();
        diaryRemoteDataSource.getSelectedDiaries();
        return selectedDiariesLiveData;
    }

    // Ottiene tutti i diari di un utente
    @Override
    public List<Diary> getAllDiariesByUserId(String userId) {
        return diaryLocalDataSource.getAllDiariesByUserId(userId);
    }

    // Ottiene tutti i paesi visitati da un utente
    @Override
    public List<String> getAllCountriesByUserId(String userId) {
        return diaryLocalDataSource.getAllCountriesByUserId(userId);
    }

    // Callback: Successo da database locale
    @Override
    public void onSuccessFromLocal(List<Diary> diaries) {
        diariesLiveData.postValue(diaries);
    }

    // Callback: Successo da database remoto
    @Override
    public void onSuccessFromRemote(List<Diary> diaries) {
        diariesLiveData.postValue(diaries);
    }

    // Callback: Fallimento da database remoto
    @Override
    public void onFailureFromRemote(Exception exception) {
        // Log dell'errore o notifica all'utente
    }

    // Callback: Successo selezione da database remoto
    @Override
    public void onSuccessSelectionFromRemote(List<Diary> diaries) {
        selectedDiariesLiveData.postValue(diaries);
    }

    // Callback: Fallimento da database locale
    @Override
    public void onFailureFromLocal(Exception exception) {
        // Log dell'errore o notifica all'utente
    }

    // Callback: Successo generico da database locale
    @Override
    public void onSuccessFromLocal() {
        // Azioni opzionali dopo un'operazione di successo
    }

    // Callback: Successo selezione da database locale
    @Override
    public void onSuccessSelectionFromLocal(List<Diary> diaries) {
        selectedDiariesLiveData.postValue(diaries);
    }

    // Callback: Successo generico da database remoto
    @Override
    public void onSuccessFromRemote() {
        // Azioni opzionali dopo un'operazione di successo
    }
}
