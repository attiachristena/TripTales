package com.unimib.triptales.source.diary;

import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.model.Diary;

import java.util.List;

public class DiaryLocalDataSource extends BaseDiaryLocalDataSource {

    private final DiaryDao diaryDao;

    public DiaryLocalDataSource(DiaryDao diaryDao) {
        this.diaryDao = diaryDao;
    }

    // Inserisce un nuovo diario nel database e restituisce l'ID
    @Override
    public long insertDiary(Diary diary) {
        return diaryDao.insert(diary);
    }

    // Aggiorna un diario intero
    @Override
    public void updateDiary(Diary diary) {
        diaryDao.update(diary);
    }

    // Aggiorna solo il nome del diario
    @Override
    public void updateDiaryName(int diaryId, String newName) {
        diaryDao.updateName(diaryId, newName);
    }

    // Aggiorna lo stato "isSelected" del diario
    @Override
    public void updateDiaryIsSelected(int diaryId, boolean newIsSelected) {
        diaryDao.updateIsSelected(diaryId, newIsSelected);
    }

    // Aggiorna la data di partenza
    @Override
    public void updateDiaryStartDate(int diaryId, String newStartDate) {
        diaryDao.updateStartDate(diaryId, newStartDate);
    }

    // Aggiorna la data di ritorno
    @Override
    public void updateDiaryEndDate(int diaryId, String newEndDate) {
        diaryDao.updateEndDate(diaryId, newEndDate);
    }

    // Aggiorna l'immagine di copertina
    @Override
    public void updateDiaryCoverImage(int diaryId, String newCoverImage) {
        diaryDao.updateCoverImage(diaryId, newCoverImage);
    }

    // Aggiorna il budget
    @Override
    public void updateDiaryBudget(int diaryId, String newBudget) {
        diaryDao.updateBudget(diaryId, newBudget);
    }

    // Aggiorna il paese
    @Override
    public void updateDiaryCountry(int diaryId, String newCountry) {
        diaryDao.updateCountry(diaryId, newCountry);
    }

    // Elimina un diario specifico
    @Override
    public void deleteDiary(Diary diary) {
        diaryDao.delete(diary);
    }

    // Elimina tutti i diari forniti
    @Override
    public void deleteAllDiaries(List<Diary> diaries) {
        diaryDao.deleteAll(diaries);
    }

    // Restituisce tutti i diari
    @Override
    public List<Diary> getAllDiaries() {
        return diaryDao.getAllDiaries();
    }

    // Restituisce tutti i diari selezionati
    @Override
    public List<Diary> getSelectedDiaries() {
        return diaryDao.getSelectedDiaries();
    }

    // Restituisce tutti i diari associati a un utente specifico
    @Override
    public List<Diary> getAllDiariesByUserId(String userId) {
        return diaryDao.getAllDiariesByUserId(userId);
    }

    // Restituisce tutti i paesi visitati da un utente specifico
    @Override
    public List<String> getAllCountriesByUserId(String userId) {
        return diaryDao.getAllCountriesByUserId(userId);
    }
}
