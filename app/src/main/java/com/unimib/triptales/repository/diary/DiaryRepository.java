package com.unimib.triptales.repository.diary;

import com.unimib.triptales.database.AppRoomDatabase;
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
    private final MutableLiveData<List<String>> countriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private boolean remoteDelete = false;
    private boolean localDelete = false;

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
    }

    public void updateAllDiaries(List<Diary> diaries){
        for(Diary diary : diaries){
            updateDiary(diary);
        }
    }

    // Aggiorna solo il nome del diario
    @Override
    public void updateDiaryName(String diaryId, String newName) {
        diaryLocalDataSource.updateDiaryName(diaryId, newName);
        diaryRemoteDataSource.updateDiaryName(diaryId, newName);
    }

    // Aggiorna lo stato di selezione del diario
    @Override
    public void updateDiaryIsSelected(String diaryId, boolean isSelected) {
        diaryLocalDataSource.updateDiaryIsSelected(diaryId, isSelected);
        diaryRemoteDataSource.updateDiaryIsSelected(diaryId, isSelected);
    }

    // Aggiorna la data di partenza
    @Override
    public void updateDiaryStartDate(String diaryId, String newStartDate) {
        diaryLocalDataSource.updateDiaryStartDate(diaryId, newStartDate);
        diaryRemoteDataSource.updateDiaryStartDate(diaryId, newStartDate);
    }

    // Aggiorna la data di ritorno
    @Override
    public void updateDiaryEndDate(String diaryId, String newEndDate) {
        diaryLocalDataSource.updateDiaryEndDate(diaryId, newEndDate);
        diaryRemoteDataSource.updateDiaryEndDate(diaryId, newEndDate);
    }

    // Aggiorna l'immagine di copertina
    @Override
    public void updateDiaryCoverImage(String diaryId, String newCoverImage) {
        diaryLocalDataSource.updateDiaryCoverImage(diaryId, newCoverImage);
        diaryRemoteDataSource.updateDiaryCoverImage(diaryId, newCoverImage);
    }

    // Aggiorna il budget
    @Override
    public void updateDiaryBudget(String diaryId, String newBudget) {
        diaryLocalDataSource.updateDiaryBudget(diaryId, newBudget);
        diaryRemoteDataSource.updateDiaryBudget(diaryId, newBudget);
    }

    // Aggiorna il paese
    @Override
    public void updateDiaryCountry(String diaryId, String newCountry) {
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
    public List<Diary> getSelectedDiaries() {
        diaryLocalDataSource.getSelectedDiaries();
        return selectedDiariesLiveData.getValue();
    }

    // Ottiene tutti i paesi visitati da un utente
    @Override
    public List<String> getAllCountries(String userId) {
        diaryLocalDataSource.getAllCountries(userId);
        return countriesLiveData.getValue();
    }

    @Override
    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    // Callback: Successo da database locale
    @Override
    public void onSuccessFromLocal(List<Diary> diaries) {
        diariesLiveData.setValue(diaries);
        for(Diary diary : diaries){
            diaryRemoteDataSource.insertDiary(diary);
        }
    }

    @Override
    public void onSuccessDeleteFromLocal() {
        localDelete = true;
    }

    @Override
    public void onSuccessCountriesFromLocal(List<String> countries) {
        countriesLiveData.setValue(countries);
    }

    // Callback: Successo selezione da database locale
    @Override
    public void onSuccessSelectionFromLocal(List<Diary> diaries) {
        selectedDiariesLiveData.setValue(diaries);
    }

    // Callback: Fallimento da database locale
    @Override
    public void onFailureFromLocal(Exception exception) {
        // Log dell'errore o notifica all'utente
    }

    // Callback: Successo da database remoto
    @Override
    public void onSuccessFromRemote(List<Diary> diaries) {
        loadingLiveData.setValue(true);
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(remoteDelete || !localDelete){
                for(Diary diary : diaries){
                    diaryLocalDataSource.insertDiary(diary);
                }
                diaryLocalDataSource.getAllDiaries();
                remoteDelete = false;
                localDelete = false;
                loadingLiveData.postValue(false);
            }
        });
    }

    // Callback: Successo generico da database remoto
    @Override
    public void onSuccessDeleteFromRemote() {
        remoteDelete = true;
    }

    // Callback: Fallimento da database remoto
    @Override
    public void onFailureFromRemote(Exception exception) {
        // Log dell'errore o notifica all'utente
    }
}
