package com.unimib.triptales.ui.homepage.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.R;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.List;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.app.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.util.*;

import javax.annotation.Nonnull;

public class HomeViewModel extends AndroidViewModel {
    private final IDiaryRepository diaryRepository;

    private final MutableLiveData<List<Diary>> diariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Diary>> selectedDiariesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> budgetLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> countriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> diaryEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> diaryOverlayVisibility = new MutableLiveData<>();

    public HomeViewModel(IDiaryRepository repository, @Nonnull Application application) {
        super(application);
        this.diaryRepository = repository;
        loadDiaries();
    }

    public LiveData<List<Diary>> getDiariesLiveData() {
        return diariesLiveData;
    }
    public LiveData<List<Diary>> getSelectedDiariesLiveData() { return selectedDiariesLiveData;}
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public MutableLiveData<String> getDiaryEvent() {
        return diaryEvent;
    }
    public LiveData<Boolean> getDiaryOverlayVisibility() {
        return diaryOverlayVisibility;
    }

    public void setDiaryOverlayVisibility(boolean visible) {
        diaryOverlayVisibility.postValue(visible);
    }

    public MutableLiveData<String> getBudgetLiveData() {
        return budgetLiveData;
    }

    public MutableLiveData<List<String>> getCountriesLiveData() {
        return countriesLiveData;
    }

    public void loadDiaries() {
        diariesLiveData.setValue(diaryRepository.getAllDiaries());
    }

    public LiveData<Boolean> getLoading(){
        return diaryRepository.getLoading();
    }

    public void loadRemoteDiaries(){
        diaryRepository.getRemoteDiaries();
    }

    public List<String> getAllCountries(String userId) {
        countriesLiveData.setValue(diaryRepository.getAllCountries(userId));
        return  countriesLiveData.getValue();
    }

    public void insertDiary(String diaryName, String startDate, String endDate,
                            String imageUri, String budget, String country) {
        String userId = SharedPreferencesUtils.getLoggedUserId();
        Diary newDiary = new Diary(userId, diaryName, startDate, endDate, imageUri,
                budget, country, System.currentTimeMillis());
        diaryRepository.insertDiary(newDiary);
        loadDiaries();
        diaryEvent.setValue(ADDED);
        countriesLiveData.setValue(diaryRepository.getAllCountries(userId));
    }

    public void updateDiary(Diary diary, String diaryName, String startDate, String endDate,
                            String imageUri, String country) {
        if(!diary.getName().equals(diaryName)){
            updateDiaryName(diary.getId(), diaryName);
            diary.setName(diaryName);
        }
        if(!diary.getStartDate().equals(startDate)){
            updateDiaryStartDate(diary.getId(), startDate);
            diary.setStartDate(startDate);
        }
        if(!diary.getEndDate().equals(endDate)){
            updateDiaryEndDate(diary.getId(), endDate);
            diary.setEndDate(endDate);
        }
        if(!diary.getCoverImageUri().equals(imageUri)){
            updateDiaryCoverImage(diary.getId(), imageUri);
            diary.setCoverImageUri(imageUri);
        }
        if(!diary.getCountry().equals(country)){
            updateDiaryCountry(diary.getId(), country);
            diary.setCountry(country);
        }
        diaryEvent.setValue(UPDATED);
    }

    public void updateDiaryName(String diaryId, String newName){
        diaryRepository.updateDiaryName(diaryId, newName);
        loadDiaries();
    }

    public void updateDiaryIsSelected(String diaryId, boolean newIsSelected){
        diaryRepository.updateDiaryIsSelected(diaryId, newIsSelected);
        loadDiaries();
    }

    public void updateDiaryStartDate(String diaryId, String newStartDate){
        diaryRepository.updateDiaryStartDate(diaryId, newStartDate);
        loadDiaries();
    }

    public void updateDiaryEndDate(String diaryId, String newEndDate){
        diaryRepository.updateDiaryEndDate(diaryId, newEndDate);
        loadDiaries();
    }

    public void updateDiaryCoverImage(String diaryId, String newCoverImage){
        diaryRepository.updateDiaryCoverImage(diaryId, newCoverImage);
        loadDiaries();
    }

    public String getBudget(String diaryId){
        return diaryRepository.getBudget(diaryId);
    }

    public void updateDiaryBudget(String diaryId, String budget){
        diaryRepository.updateDiaryBudget(diaryId, budget);
        budgetLiveData.setValue(budget);
        loadDiaries();
    }

    public void updateDiaryCountry(String diaryId, String country){
        diaryRepository.updateDiaryCountry(diaryId, country);
        countriesLiveData.setValue(diaryRepository.getAllCountries(SharedPreferencesUtils.getLoggedUserId()));
        loadDiaries();
    }

    public void deleteSelectedDiaries(){
        List<Diary> selectedDiaries = getSelectedDiariesLiveData().getValue();
        if(selectedDiaries != null && !selectedDiaries.isEmpty()){
            diaryRepository.deleteAllDiaries(selectedDiaries);
            selectedDiariesLiveData.postValue(Collections.emptyList());
            loadDiaries();
            countriesLiveData.setValue(diaryRepository.getAllCountries(SharedPreferencesUtils.getLoggedUserId()));
            diaryEvent.setValue(DELETED);
        } else {
            diaryEvent.setValue(INVALID_DELETE);
        }
    }

    public void toggleDiarySelection(Diary diary){
        boolean isSelected = diary.isDiary_isSelected();
        diary.setDiary_isSelected(!isSelected);
        updateDiaryIsSelected(diary.getId(), !isSelected);
        selectedDiariesLiveData.setValue(diaryRepository.getSelectedDiaries());
        loadDiaries();
    }

    public void deselectAllDiaries(){
        loadDiaries();
        List<Diary> diaries = diariesLiveData.getValue();
        if(diaries != null){
            for(Diary diary : diaries){
                diary.setDiary_isSelected(false);
                updateDiaryIsSelected(diary.getId(), false);
            }
            diariesLiveData.setValue(diaries);
            selectedDiariesLiveData.postValue(Collections.emptyList());
            diaryRepository.updateAllDiaries(diaries);
        }
    }

    public boolean validateDiaryInput(String diaryName, String startDate, String endDate,
                                      String imageUri, String country, boolean bAdd) {
        boolean correct = true;
        if(startDate.equals("//")) startDate = "";
        if(endDate.equals("//")) endDate = "";
        if(diaryName.isEmpty()){
            errorLiveData.setValue(getApplication().getString(R.string.errore_nome));
        } else if(country.isEmpty()){
            errorLiveData.setValue(getApplication().getString(R.string.errore_paese));
        } else if(startDate.isEmpty()){
            errorLiveData.setValue(getApplication().getString(R.string.errore_data_partenza));
        } else if(endDate.isEmpty()){
            errorLiveData.setValue(getApplication().getString(R.string.errore_data_ritorno));
        } else if(!validateDateOrder(startDate, endDate)){
            errorLiveData.setValue(getApplication().getString(R.string.errore_date));
        } else if(bAdd && (imageUri == null || imageUri.isEmpty())){
            errorLiveData.setValue(getApplication().getString(R.string.errore_date));
        }

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
    }

    private boolean validateDateOrder(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return sdf.parse(startDate).before(sdf.parse(endDate));
        } catch (ParseException e) {
            return false;
        }
    }
}
