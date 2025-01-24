package com.unimib.triptales.ui.homepage.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> diaryName = new MutableLiveData<>();
    private final MutableLiveData<String> diaryCountry = new MutableLiveData<>();
    private final MutableLiveData<String> startDate = new MutableLiveData<>();
    private final MutableLiveData<String> endDate = new MutableLiveData<>();

    public LiveData<String> getDiaryName() {
        return diaryName;
    }

    public void setDiaryName(String name) { diaryName.setValue(name); }

    public MutableLiveData<String> getDiaryCountry() { return diaryCountry; }

    public void setDiaryCountry(String country) { diaryCountry.setValue(country); }

    public LiveData<String> getStartDate() {
        return startDate;
    }

    public void setStartDate(String date) {
        startDate.setValue(date);
    }

    public LiveData<String> getEndDate() {
        return endDate;
    }

    public void setEndDate(String date) {
        endDate.setValue(date);
    }
}

