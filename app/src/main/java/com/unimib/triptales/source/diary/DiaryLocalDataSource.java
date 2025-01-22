package com.unimib.triptales.source.diary;

import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.model.Diary;

import java.util.List;

public class DiaryLocalDataSource implements BaseDiaryLocalDataSource {

    private final DiaryDao diaryDao;

    public DiaryLocalDataSource(DiaryDao diaryDao) {
        this.diaryDao = diaryDao;
    }

    @Override
    public long insertDiary(Diary diary) {
        // Inserisce un nuovo diario nel database e restituisce l'ID
        return diaryDao.insert(diary);
    }

    @Override
    public void updateDiaryName(int diaryId, String newName) {
        // Aggiorna il nome del diario dato il suo ID
        diaryDao.updateName(diaryId, newName);
    }

    @Override
    public void updateDiaryIsSelected(int diaryId, boolean newIsSelected) {
        // Aggiorna lo stato "isSelected" del diario dato il suo ID
        diaryDao.updateIsSelected(diaryId, newIsSelected);
    }

    @Override
    public void deleteDiary(Diary diary) {
        // Elimina un diario specifico
        diaryDao.delete(diary);
    }

    @Override
    public void deleteAllDiaries(List<Diary> diaries) {
        // Elimina tutti i diari forniti
        diaryDao.deleteAll(diaries);
    }

    @Override
    public List<Diary> getAllDiaries() {
        // Restituisce tutti i diari
        return diaryDao.getAllDiaries();
    }

    @Override
    public List<Diary> getSelectedDiaries() {
        // Restituisce tutti i diari selezionati
        return diaryDao.getSelectedDiaries();
    }
}
