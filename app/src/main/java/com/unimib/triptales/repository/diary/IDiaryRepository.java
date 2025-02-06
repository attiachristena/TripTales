package com.unimib.triptales.repository.diary;

import com.unimib.triptales.model.Diary;

import java.util.List;
import androidx.lifecycle.LiveData;


public interface IDiaryRepository {


        // Inserisce un diario
        void insertDiary(Diary diary);

        // Aggiorna un diario intero
        void updateDiary(Diary diary);

        // Aggiorna solo il nome del diario
        void updateDiaryName(int diaryId, String newName);

        // Aggiorna lo stato di selezione del diario
        void updateDiaryIsSelected(int diaryId, boolean isSelected);

        // Aggiorna la data di partenza
        void updateDiaryStartDate(int diaryId, String newStartDate);

        // Aggiorna la data di ritorno
        void updateDiaryEndDate(int diaryId, String newEndDate);

        // Aggiorna l'immagine di copertina
        void updateDiaryCoverImage(int diaryId, String newCoverImage);

        // Aggiorna il budget
        void updateDiaryBudget(int diaryId, String newBudget);

        // Aggiorna il paese
        void updateDiaryCountry(int diaryId, String newCountry);

        // Elimina un diario
        void deleteDiary(Diary diary);

        // Elimina più diari
        void deleteAllDiaries(List<Diary> diaries);

        // Ottiene tutti i diari
        List<Diary> getAllDiaries();

        // Ottiene i diari selezionati
        LiveData<List<Diary>> getSelectedDiaries();

        // Ottiene tutti i diari di un utente
        List<Diary> getAllDiariesByUserId(String userId);

        // Ottiene tutti i paesi visitati da un utente
        List<String> getAllCountriesByUserId(String userId);

}
