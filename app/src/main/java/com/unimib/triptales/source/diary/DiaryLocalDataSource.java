package com.unimib.triptales.source.diary;

import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.model.Diary;

import java.util.List;

public class DiaryLocalDataSource extends BaseDiaryLocalDataSource {

    private final DiaryDao diaryDao;
    private final String userId;

    public DiaryLocalDataSource(DiaryDao diaryDao, String userId) {
        this.diaryDao = diaryDao;
        this.userId = userId;
    }

    // Inserisce un nuovo diario nel database e restituisce l'ID
    @Override
    public void insertDiary(Diary diary) {
        try{
            diaryDao.insert(diary);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna un diario intero
    @Override
    public void updateDiary(Diary diary) {
        try{
            diaryDao.update(diary);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna solo il nome del diario
    @Override
    public void updateDiaryName(String diaryId, String newName) {
        try{
            diaryDao.updateName(diaryId, newName);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna lo stato "diary_isSelected" del diario
    @Override
    public void updateDiaryIsSelected(String diaryId, boolean newIsSelected) {
        try{
            diaryDao.updateIsSelected(diaryId, newIsSelected);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna la data di partenza
    @Override
    public void updateDiaryStartDate(String diaryId, String newStartDate) {
        try{
            diaryDao.updateStartDate(diaryId, newStartDate);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna la data di ritorno
    @Override
    public void updateDiaryEndDate(String diaryId, String newEndDate) {
        try{
            diaryDao.updateEndDate(diaryId, newEndDate);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna l'immagine di copertina
    @Override
    public void updateDiaryCoverImage(String diaryId, String newCoverImage) {
        try{
            diaryDao.updateCoverImage(diaryId, newCoverImage);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna il budget
    @Override
    public void updateDiaryBudget(String diaryId, String newBudget) {
        try{
            diaryDao.updateBudget(diaryId, newBudget);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Aggiorna il paese
    @Override
    public void updateDiaryCountry(String diaryId, String newCountry) {
        try{
            diaryDao.updateCountry(diaryId, newCountry);
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Elimina un diario specifico
    @Override
    public void deleteDiary(Diary diary) {
        try{
            diaryDao.delete(diary);
            diaryCallback.onSuccessDeleteFromLocal();
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Elimina tutti i diari forniti
    @Override
    public void deleteAllDiaries(List<Diary> diaries) {
        try{
            diaryDao.deleteAll(diaries);
            diaryCallback.onSuccessDeleteFromLocal();
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Restituisce tutti i diari
    @Override
    public void getAllDiaries() {
        try{
            diaryCallback.onSuccessFromLocal(diaryDao.getAllDiaries(userId));
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Restituisce tutti i diari selezionati
    @Override
    public void getSelectedDiaries() {
        try{
            diaryCallback.onSuccessSelectionFromLocal(diaryDao.getSelectedDiaries(userId));
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    // Restituisce tutti i paesi visitati da un utente specifico
    @Override
    public void getAllCountries(String userId) {
        try{
            diaryCallback.onSuccessCountriesFromLocal(diaryDao.getAllCountries(userId));
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }

    public void getBudget(String diaryId){
        try{
            diaryCallback.onSuccessBudgetFromLocal(diaryDao.getBudget(diaryId));
        } catch (Exception e){
            diaryCallback.onFailureFromLocal(e);
        }
    }
}
