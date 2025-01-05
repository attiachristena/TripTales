package com.unimib.triptales.ui.homepage.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.repository.diary.DiaryRepository;

import java.util.List;

public class DiaryViewModel extends ViewModel {

    private final DiaryRepository diaryRepository;

    private final MutableLiveData<List<Diary>> diariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    public DiaryViewModel(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public MutableLiveData<List<Diary>> getDiariesLiveData() {
        return diariesLiveData;
    }

    public MutableLiveData<List<Expense>> getExpensesLiveData() {
        return expensesLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public void fetchAllDiaries() {
        loadingLiveData.setValue(true);
        try {
            List<Diary> diaries = diaryRepository.getAllDiaries();
            diariesLiveData.postValue(diaries);
            loadingLiveData.postValue(false);
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public void insertDiary(Diary diary) {
        loadingLiveData.setValue(true);
        try {
            diaryRepository.insertDiary(diary);
            fetchAllDiaries(); // Aggiorna la lista dei diari
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public void updateDiary(Diary diary) {
        loadingLiveData.setValue(true);
        try {
            diaryRepository.updateDiary(diary);
            fetchAllDiaries(); // Aggiorna la lista dei diari
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public void deleteDiary(Diary diary) {
        loadingLiveData.setValue(true);
        try {
            diaryRepository.deleteDiary(diary);
            fetchAllDiaries(); // Aggiorna la lista dei diari
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

}
